package com.coleji.neptune.Core

import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory

import java.sql.Connection
import java.util.concurrent.atomic.AtomicInteger

class ConnectionPoolWrapper(private val source: HikariDataSource)  {
	private val logger = LoggerFactory.getLogger(this.getClass.getName)
	var openConnections = new AtomicInteger(0)
	var connectionHighWaterMark = new AtomicInteger(0)

	private[Core] def withConnection[T](block: Connection => T)(implicit PA: PermissionsAuthority): T = {
		var c: Connection = null
		try {
			c = source.getConnection()
			increment()
			val ret = block(c)
			ret
		} catch {
			case e: Throwable => {
				PA.emailLogger.error("Error using a DB connection: ", e)
				throw e
			}
		} finally {
			if (c != null) {
				c.close()
				decrement()
			}
		}
	}

	private[Core] def getConnectionForTransaction: Connection = {
		val c = source.getConnection()
		increment()
		c.setAutoCommit(false)
		c
	}

	private def increment(): Unit = {
		openConnections.incrementAndGet()
		if (openConnections.get() > connectionHighWaterMark.get()) {
			connectionHighWaterMark.set(openConnections.get())
			logger.info("high water mark: " + connectionHighWaterMark.get())
		}
		logger.debug("Grabbed DB connection; in use: " + openConnections.get())
		logger.debug("high water mark: " + connectionHighWaterMark.get())
	}

	private[Core] def decrement(): Unit = {
		openConnections.decrementAndGet()
		logger.debug("Freed DB connection; in use: " + openConnections.get())
	}

	private[Core] def close(): Unit = source.close()
}
