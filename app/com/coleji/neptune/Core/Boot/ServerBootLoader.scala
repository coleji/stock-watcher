package com.coleji.neptune.Core.Boot

import com.coleji.neptune.Core._
import com.coleji.neptune.Util.PropertiesWrapper
import org.slf4j.LoggerFactory
import play.api.inject.ApplicationLifecycle
import redis.clients.jedis.JedisPool

import scala.concurrent.Future

object ServerBootLoader {
	private val logger = LoggerFactory.getLogger(this.getClass.getName)
	val DB_DRIVER_ORACLE = "oracle"
	val DB_DRIVER_MYSQL = "mysql"

	private def getDBPools(dbDriver: String, attempts: Int = 0): DatabaseGateway = {
		val MAX_ATTEMPTS = 3
		try {
			dbDriver match {
				case DB_DRIVER_MYSQL => MysqlDatabaseConnection("conf/private/mysql-credentials")
				case _ => OracleDatabaseConnection("conf/private/oracle-credentials")
			}
		} catch {
			case e: Exception => {
				if (attempts < MAX_ATTEMPTS) {
					logger.warn("failed to get pools, sleeping and trying again....")
					Thread.sleep(1000)
					getDBPools(dbDriver, attempts + 1)
				} else {
					throw e
				}
			}
		}
	}

	private[Boot] def load(
		lifecycle: Option[ApplicationLifecycle],
		isTestMode: Boolean,
		readOnlyDatabase: Boolean,
		entityPackagePath: String,
		definedAuthMechanisms: List[(RequestCacheObject[_], String, PropertiesWrapper => Boolean)],
		requiredProperties: List[String],
		paPostBoot: PropertiesWrapper => Unit
	): PermissionsAuthority = {
		if (PermissionsAuthority.isBooted) PermissionsAuthority.PA
		else {
			logger.info(" ***************     BOOTING UP SERVER   ***************  ")
			logger.info(System.getProperty("java.vendor") + " - " + System.getProperty("java.version"))
			logger.info(System.getProperty("java.home"))

			// Get server instance properties
			val paramFile = new PropertiesWrapper("conf/private/server-properties", requiredProperties)

			val enabledAuthMechanisms: List[RequestCacheObject[_]] =
				definedAuthMechanisms
					.filter(t => paramFile.getBoolean(t._2))
					.filter(t => t._3(paramFile)) // check the nuke function
					.map(t => t._1)


			val dbDriver = paramFile.getOptionalString("DBDriver") match {
				case Some(DB_DRIVER_MYSQL) => DB_DRIVER_MYSQL
				case _ => DB_DRIVER_ORACLE
			}

			logger.info("Using DB Driver: " + dbDriver)

			val dbConnection = getDBPools(dbDriver)
			val redisPool = new JedisPool(paramFile.getOptionalString("RedisHost").getOrElse("localhost"), 6379)
			lifecycle match {
				case Some(lc) => lc.addStopHook(() => Future.successful({
					logger.info("****************************    Stop hook: closing pools  **********************")
					dbConnection.close()
				}))
				case None =>
			}

			val preparedQueriesOnly = paramFile.getOptionalString("PreparedQueriesOnly").getOrElse("true").equals("true")
			val forceReadOnlyDatabase = paramFile.getOptionalString("ForceReadOnlyDatabase").getOrElse("false").equals("true")

			val doReadOnlyDatabase = readOnlyDatabase || forceReadOnlyDatabase
			logger.info("ready only database? " + doReadOnlyDatabase)

			new PermissionsAuthority(
				systemParams = SystemServerParameters(
					entityPackagePath = entityPackagePath,
					serverTimeOffsetSeconds = 0,
					isDebugMode = paramFile.getOptionalString("PADebug").getOrElse("false").equals("true"),
					isTestMode = isTestMode,
					readOnlyDatabase = doReadOnlyDatabase,
					allowableUserTypes = enabledAuthMechanisms,
					preparedQueriesOnly = preparedQueriesOnly,
					persistenceSystem = PermissionsAuthority.PERSISTENCE_SYSTEM_MYSQL,
					emailCrashesTo = paramFile.getOptionalString("EmailCrashesTo").getOrElse("jon@community-boating.org"),
					dbDriver = dbDriver
				),
				customParams = paramFile,
				dbGateway = dbConnection,
				redisPool = redisPool,
				paPostBoot
			)
		}
	}
}
