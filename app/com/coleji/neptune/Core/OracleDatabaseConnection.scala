package com.coleji.neptune.Core

import com.coleji.neptune.Core.Boot.ServerBootLoader
import com.coleji.neptune.Util.PropertiesWrapper
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.slf4j.LoggerFactory

object OracleDatabaseConnection {
	private val logger = LoggerFactory.getLogger(this.getClass.getName)
	private[Core] def apply(confFileLocation: String): DatabaseGateway = {
		val pw = new PropertiesWrapper(confFileLocation, List("username", "password", "schema", "temptableschema"))

		Class.forName("oracle.jdbc.driver.OracleDriver")

		val mainSchemaName = pw.getString("schema")
		val tempSchemaName = pw.getString("temptableschema")
		val host = pw.getOptionalString("host")
		val port = pw.getOptionalString("port")
		val sid = pw.getOptionalString("sid")
		val serviceName = pw.getOptionalString("servicename")
		val username = pw.getString("username")
		val password = pw.getString("password")
		val tempUsername = pw.getString("temptableusername")
		val tempPassword = pw.getString("temptablepassword")
		val poolSize = pw.getOptionalString("maxPoolSize").map(_.toInt).getOrElse(2)
		val poolSizeTemp = pw.getOptionalString("maxPoolSizeTemp").map(_.toInt).getOrElse(1)
		val tnsName = pw.getOptionalString("tnsName")

		val mainConfig = getDataSourceConfig(host, port, sid, serviceName, tnsName, username, password, poolSize)
		val tempConfig = getDataSourceConfig(host, port, sid, serviceName, tnsName, tempUsername, tempPassword, poolSizeTemp)

		new DatabaseGateway(
			driverName = ServerBootLoader.DB_DRIVER_ORACLE,
			mainPool = new ConnectionPoolWrapper(new HikariDataSource(mainConfig)),
			tempPool = new ConnectionPoolWrapper(new HikariDataSource(tempConfig)),
			mainSchemaName = mainSchemaName,
			tempSchemaName = tempSchemaName,
			mainUserName = username
		)
	}

	private def getDataSourceConfig(
		host: Option[String], port: Option[String],
		sid: Option[String], serviceName: Option[String], tnsName: Option[String],
		username: String, password: String, poolSize: Int
	): HikariConfig = {
		val config = new HikariConfig()
		logger.debug("username: " + username + " max pool size: " + poolSize)

		val url = if (tnsName.nonEmpty) {
			logger.debug("using tns")
			s"jdbc:oracle:thin:@${tnsName.get}?TNS_ADMIN=conf/private/ora-wallet"
		} else if (sid.nonEmpty) {
			logger.debug("using sid")
			s"jdbc:oracle:thin:@${host.get}:${port.get}:${sid.get}"
		} else if (serviceName.nonEmpty) {
			logger.debug("using servicename")
			s"jdbc:oracle:thin:@${host.get}:${port.get}/${serviceName.get}"
		} else {
			throw new Exception("Oracle connection config: must specify sid or servicename")
		}

		config.setJdbcUrl(url)
		config.setUsername(username)
		config.setPassword(password)

		// other options
		config.setMaximumPoolSize(poolSize)
		config.setLeakDetectionThreshold(30 * 1000)

		config
	}
}
