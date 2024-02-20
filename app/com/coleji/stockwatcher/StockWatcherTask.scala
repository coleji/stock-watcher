package com.coleji.stockwatcher

import com.coleji.neptune.Core.UnlockedRequestCache

trait StockWatcherTask {
	protected var log = new StringBuilder()
	protected def taskAction(rc: UnlockedRequestCache): Unit
	protected def appendLog(s: String) = log.append("| ").append(s).append("\n")
	def run(rc: UnlockedRequestCache) = {
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
}
