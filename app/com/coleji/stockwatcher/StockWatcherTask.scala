package com.coleji.stockwatcher

import com.coleji.neptune.Core.UnlockedRequestCache
import org.slf4j.LoggerFactory

import java.time.ZonedDateTime

trait StockWatcherTask {
	private val logger = LoggerFactory.getLogger(this.getClass.getName)
	val API_FETCH_HOUR = 1

	protected var log = new StringBuilder()
	protected def appendLog(s: String): StringBuilder = log.append("| ").append(s).append("\n")

	def run(rc: UnlockedRequestCache): Unit = {
		try {
			log = new StringBuilder()
			taskAction(rc)
		} finally {
			logger.info("------------------------------")
			logger.info("| TASK LOG " + this.getClass.getCanonicalName)
			logger.info("------------------------------")
			log.toString().split("\n").foreach(logger.info)
		}
	}

	def getNextRuntime: ZonedDateTime
	protected def taskAction(rc: UnlockedRequestCache): Unit
}
