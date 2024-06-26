package com.coleji.neptune.Core

import com.coleji.neptune.Exception.UnauthorizedAccessException
import com.coleji.neptune.IO.PreparedQueries.{HardcodedQueryForInsert, HardcodedQueryForSelect, HardcodedQueryForUpdateOrDelete, PreparedProcedureCall}
import com.coleji.neptune.Storable.Fields.DatabaseField
import com.coleji.neptune.Storable.StorableQuery.{QueryBuilder, QueryBuilderResultRow}
import com.coleji.neptune.Storable.{Filter, StorableClass, StorableObject}
import org.slf4j.LoggerFactory

import java.sql.Connection

// TODO: decide on one place for all the fetchSize defaults and delete the rest
abstract class PersistenceBroker private[Core](dbConnection: DatabaseGateway, preparedQueriesOnly: Boolean, readOnly: Boolean) {
	private val logger = LoggerFactory.getLogger(this.getClass.getName)
	var transactionConnection: Option[Connection] = None

	override def finalize(): Unit = {
		transactionConnection.foreach(c => {
			logger.error("A transaction connection just got GC'd")
			c.close()
			dbConnection.mainPool.decrement()
		})
		super.finalize()
	}

	def openTransaction(): Unit = {
		logger.debug("getting connection for transaction")
		transactionConnection = Some(dbConnection.mainPool.getConnectionForTransaction)
		logger.debug("transaction connection id: " + transactionConnection.get.hashCode())
	}

	def commit(): Unit = {
		logger.debug("committing connection for transaction")
		transactionConnection.foreach(c => {
			c.commit()
			c.close()
			dbConnection.mainPool.decrement()
		})
		transactionConnection = None
	}

	def rollback(): Unit = {
		logger.debug("rolling back connection for transaction")
		transactionConnection.foreach(c => {
			c.rollback()
			c.close()
			dbConnection.mainPool.decrement()
		})
		transactionConnection = None
	}

	// All public requests need to go through user type-based security
	final def getObjectById[T <: StorableClass](obj: StorableObject[T], id: Int, fieldShutter: Set[DatabaseField[_]]): Option[T] = {
		if (preparedQueriesOnly) throw new UnauthorizedAccessException("Server is in Prepared Queries Only mode.")
		else getObjectByIdImplementation(obj, id, fieldShutter)
	}

	final def getObjectsByIds[T <: StorableClass](obj: StorableObject[T], ids: List[Int], fieldShutter: Set[DatabaseField[_]], fetchSize: Int = 50): List[T] = {
		if (preparedQueriesOnly) throw new UnauthorizedAccessException("Server is in Prepared Queries Only mode.")
		else getObjectsByIdsImplementation(obj, ids, fieldShutter, fetchSize)
	}

	final def countObjectsByFilters[T <: StorableClass](obj: StorableObject[T], filters: List[Filter]): Int = {
		if (preparedQueriesOnly) throw new UnauthorizedAccessException("Server is in Prepared Queries Only mode.")
		countObjectsByFiltersImplementation(obj, filters)
	}

	final def getObjectsByFilters[T <: StorableClass](obj: StorableObject[T], filters: List[Filter], fieldShutter: Set[DatabaseField[_]], limit: Option[Int], orderBy: Option[DatabaseField[_]], orderByDesc: Boolean, fetchSize: Int = 50): List[T] = {
		if (preparedQueriesOnly) throw new UnauthorizedAccessException("Server is in Prepared Queries Only mode.")
		else getObjectsByFiltersImplementation(obj, filters, fieldShutter, limit, orderBy, orderByDesc, fetchSize)
	}

	final def getAllObjectsOfClass[T <: StorableClass](obj: StorableObject[T], fieldShutter: Set[DatabaseField[_]], fetchSize: Int = 50): List[T] = {
		if (preparedQueriesOnly) throw new UnauthorizedAccessException("Server is in Prepared Queries Only mode.")
		else getAllObjectsOfClassImplementation(obj, fieldShutter, fetchSize)
	}

	final def commitObjectToDatabase(i: StorableClass): Unit = {
		if (readOnly) throw new UnauthorizedAccessException("Server is in Database Read Only mode.")
		else if (preparedQueriesOnly) throw new UnauthorizedAccessException("Server is in Prepared Queries Only mode.")
		else if (i.valuesList.isEmpty) throw new Exception("Refusing to commit object with empty valuesList: " + i.companion.entityName)
		else commitObjectToDatabaseImplementation(i)
	}

	final def batchInsertObjectsToDatabase(is: List[StorableClass]): Unit = {
		if (readOnly) throw new UnauthorizedAccessException("Server is in Database Read Only mode.")
		else if (preparedQueriesOnly) throw new UnauthorizedAccessException("Server is in Prepared Queries Only mode.")
		else {
			is.foreach(i => {
				if (i.valuesList.isEmpty) throw new Exception("Refusing to commit object with empty valuesList: " + i.companion.entityName)
			})
			batchInsertObjectsToDatabaseImplementation(is)
		}
	}

	final def executePreparedQueryForSelect[T](pq: HardcodedQueryForSelect[T], fetchSize: Int = 50): List[T] = {
		executePreparedQueryForSelectImplementation(pq, fetchSize)
	}

	final def executePreparedQueryForInsert(pq: HardcodedQueryForInsert): Option[String] = {
		if (readOnly) throw new UnauthorizedAccessException("Server is in Database Read Only mode.")
		else executePreparedQueryForInsertImplementation(pq)
	}

	final def executePreparedQueryForUpdateOrDelete(pq: HardcodedQueryForUpdateOrDelete): Int = {
		if (readOnly) throw new UnauthorizedAccessException("Server is in Database Read Only mode.")
		else executePreparedQueryForUpdateOrDeleteImplementation(pq)
	}

	final def executeQueryBuilder(qb: QueryBuilder, fetchSize: Int = 50): List[QueryBuilderResultRow] = {
		if (preparedQueriesOnly) throw new UnauthorizedAccessException("Server is in Prepared Queries Only mode.")
		else executeQueryBuilderImplementation(qb, fetchSize)
	}

	final def executeProcedure[T](pc: PreparedProcedureCall[T]): T = {
		if (readOnly) throw new UnauthorizedAccessException("Server is in Database Read Only mode.")
		else executeProcedureImpl(pc)
	}

	final def deleteObjectsByIds[T <: StorableClass](obj: StorableObject[T], ids: List[Int]): Unit = {
		if (readOnly) throw new UnauthorizedAccessException("Server is in Database Read Only mode.")
		else if (preparedQueriesOnly) throw new UnauthorizedAccessException("Server is in Prepared Queries Only mode.")
		else deleteObjectsByIdsImplementation(obj, ids)
	}

	// Implementations of PersistenceBroker should implement these.  Assume user type security has already been passed if you're calling these
	protected def getObjectByIdImplementation[T <: StorableClass](obj: StorableObject[T], id: Int, fieldShutter: Set[DatabaseField[_]]): Option[T]

	protected def getObjectsByIdsImplementation[T <: StorableClass](obj: StorableObject[T], ids: List[Int], fieldShutter: Set[DatabaseField[_]], fetchSize: Int): List[T]

	protected def getObjectsByFiltersImplementation[T <: StorableClass](obj: StorableObject[T], filters: List[Filter], fieldShutter: Set[DatabaseField[_]], limit: Option[Int], orderBy: Option[DatabaseField[_]], orderByDesc: Boolean, fetchSize: Int): List[T]

	protected def countObjectsByFiltersImplementation[T <: StorableClass](obj: StorableObject[T], filters: List[Filter]): Int

	protected def getAllObjectsOfClassImplementation[T <: StorableClass](obj: StorableObject[T], fieldShutter: Set[DatabaseField[_]], fetchSize: Int): List[T]

	protected def commitObjectToDatabaseImplementation(i: StorableClass): Unit

	protected def batchInsertObjectsToDatabaseImplementation(is: List[StorableClass]): Unit

	protected def executePreparedQueryForSelectImplementation[T](pq: HardcodedQueryForSelect[T], fetchSize: Int = 50): List[T]

	protected def executePreparedQueryForInsertImplementation(pq: HardcodedQueryForInsert): Option[String]

	protected def executePreparedQueryForUpdateOrDeleteImplementation(pq: HardcodedQueryForUpdateOrDelete): Int

	protected def executeQueryBuilderImplementation(qb: QueryBuilder, fetchSize: Int): List[QueryBuilderResultRow]

	protected def executeProcedureImpl[T](pc: PreparedProcedureCall[T]): T

	protected def deleteObjectsByIdsImplementation[T <: StorableClass](obj: StorableObject[T], ids: List[Int]): Unit
}
