package com.coleji.neptune.Core

import com.coleji.neptune.Core.Boot.ServerBootLoader
import com.coleji.neptune.Core.access.Permission
import com.coleji.neptune.IO.PreparedQueries.{HardcodedQueryForInsert, HardcodedQueryForSelect, HardcodedQueryForUpdateOrDelete, PreparedProcedureCall}
import com.coleji.neptune.Storable.Fields.DatabaseField
import com.coleji.neptune.Storable.StorableQuery.{QueryBuilder, QueryBuilderResultRow}
import com.coleji.neptune.Storable.{Filter, StorableClass, StorableObject}
import com.coleji.neptune.Util.PropertiesWrapper
import org.slf4j.LoggerFactory
import redis.clients.jedis.JedisPool

// TODO: Some sort of security on the CacheBroker so arbitrary requests can't see the authentication tokens
// TODO: mirror all PB methods on RC so the RC can either pull from redis or dispatch to oracle etc
sealed abstract class RequestCache private[Core](
	val userName: String,
	serverParams: PropertiesWrapper,
	dbGateway: DatabaseGateway,
	redisPool: JedisPool
)(implicit val PA: PermissionsAuthority) {
	private val logger = LoggerFactory.getLogger(this.getClass.getName)
	def companion: RequestCacheObject[_]

	protected val pb: PersistenceBroker = {
		dbGateway.driverName match {
			case ServerBootLoader.DB_DRIVER_MYSQL => {
				if (this.isInstanceOf[RootRequestCache]) new MysqlBroker(dbGateway, false, PA.systemParams.readOnlyDatabase)
				else new MysqlBroker(dbGateway, PA.systemParams.preparedQueriesOnly, PA.systemParams.readOnlyDatabase)
			}
			case _ => {
				if (this.isInstanceOf[RootRequestCache]) new OracleBroker(dbGateway, false, PA.systemParams.readOnlyDatabase)
				else new OracleBroker(dbGateway, PA.systemParams.preparedQueriesOnly, PA.systemParams.readOnlyDatabase)
			}
		}
	}

	val cb: CacheBroker = new RedisBroker(redisPool)

	def hasPermission(p: Permission): Boolean = false

	def assertUnlocked: UnlockedRequestCache = this match {
		case urc: UnlockedRequestCache => urc
		case _ => throw new Exception("Failed to assert rc was unlocked")
	}

	def executePreparedQueryForSelect[T](pq: HardcodedQueryForSelect[T], fetchSize: Int = 50): List[T] = {
		this.companion.test(pq.allowedUserTypes)
		pb.executePreparedQueryForSelect(pq, fetchSize)
	}

	def executePreparedQueryForInsert(pq: HardcodedQueryForInsert): Option[String] = {
		this.companion.test(pq.allowedUserTypes)
		pb.executePreparedQueryForInsert(pq)
	}

	def executePreparedQueryForUpdateOrDelete(pq: HardcodedQueryForUpdateOrDelete): Int = {
		this.companion.test(pq.allowedUserTypes)
		pb.executePreparedQueryForUpdateOrDelete(pq)
	}

	def executeQueryBuilder(qb: QueryBuilder, fetchSize: Int = 50): List[QueryBuilderResultRow] = {
		pb.executeQueryBuilder(qb, fetchSize)
	}

	def executeProcedure[T](pc: PreparedProcedureCall[T]): T = {
		this.companion.test(pc.allowedUserTypes)
		pb.executeProcedure(pc)
	}

	def withTransaction[L, R](block: () => Either[L, R]): Either[L, R] = {
		logger.debug("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&  TRANSACTION OPENED")
		pb.openTransaction()
		try {
			val result = block()
			result match {
				case l: Left[L, R] => {
					logger.debug("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&  RRRRROLLING IT BACK")
					pb.rollback()
					l
				}
				case r: Right[L, R] => {
					pb.commit()
					r
				}
			}
		} catch {
			case e: Throwable => {
				pb.rollback()
				throw e
			}
		}
	}
}

abstract class LockedRequestCache(
	override val userName: String,
	serverParams: PropertiesWrapper,
	dbGateway: DatabaseGateway,
	redisPool: JedisPool,
) extends RequestCache(userName, serverParams, dbGateway, redisPool) {

}

abstract class UnlockedRequestCache(
	override val userName: String,
	serverParams: PropertiesWrapper,
	dbGateway: DatabaseGateway,
	redisPool: JedisPool,
) extends RequestCache(userName, serverParams, dbGateway, redisPool) {
	def getObjectById[T <: StorableClass](obj: StorableObject[T], id: Int, fieldShutter: Set[DatabaseField[_]]): Option[T] =
		pb.getObjectById(obj, id, fieldShutter)

	def getObjectsByIds[T <: StorableClass](obj: StorableObject[T], ids: List[Int], fieldShutter: Set[DatabaseField[_]], fetchSize: Int = 50): List[T] =
		pb.getObjectsByIds(obj, ids, fieldShutter, fetchSize)

	def countObjectsByFilters[T <: StorableClass](obj: StorableObject[T], filters: List[Filter]): Int = {
		pb.countObjectsByFilters(obj, filters)
	}

	def getObjectsByFilters[T <: StorableClass](obj: StorableObject[T], filters: List[Filter], fieldShutter: Set[DatabaseField[_]], limit: Option[Int], orderBy: Option[DatabaseField[_]], orderByDesc: Boolean, fetchSize: Int = 50): List[T] =
		pb.getObjectsByFilters(obj, filters, fieldShutter, limit, orderBy, orderByDesc, fetchSize)

	def getAllObjectsOfClass[T <: StorableClass](obj: StorableObject[T], fieldShutter: Set[DatabaseField[_]], fetchSize: Int = 50): List[T] =
		pb.getAllObjectsOfClass(obj, fieldShutter, fetchSize)

	def commitObjectToDatabase(i: StorableClass): Unit =
		pb.commitObjectToDatabase(i)

	def batchInsertObjects(is: List[StorableClass]): Unit =
		pb.batchInsertObjectsToDatabase(is)

	def deleteObjectsById[T <: StorableClass](obj: StorableObject[T], ids: List[Int]): Unit =
		pb.deleteObjectsByIds(obj, ids)
}
