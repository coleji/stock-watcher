package com.coleji.neptune.Core

import org.slf4j.LoggerFactory

class DatabaseGateway(
	val driverName: String,
	private[Core] val mainPool: ConnectionPoolWrapper,
	private[Core] val tempPool: ConnectionPoolWrapper,
	private[Core] val mainSchemaName: String,
	private[Core] val tempSchemaName: String,
	private[Core] val mainUserName: String
) {
	private val logger = LoggerFactory.getLogger(this.getClass.getName)
	private[Core] def close(): Unit = {
		logger.info("  ************    Shutting down!  Closing pool!!  *************  ")
		mainPool.close()
		tempPool.close()
	}
}
