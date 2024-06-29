package com.coleji.stockwatcher

import com.coleji.neptune.Core.UnlockedRequestCache

import java.time.ZonedDateTime

trait StockWatcherTask {
	val API_FETCH_HOUR = 1

	protected var log = new StringBuilder()
	protected def appendLog(s: String): StringBuilder = log.append("| ").append(s).append("\n")

	def run(rc: UnlockedRequestCache): Unit = {
		try {
			log = new StringBuilder()
			taskAction(rc)
		} finally {
			println("------------------------------")
			println("| TASK LOG")
			println("------------------------------")
			println(log.toString())
		}
	}

	def getNextRuntime: ZonedDateTime
	protected def taskAction(rc: UnlockedRequestCache): Unit
}
