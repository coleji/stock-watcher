package com.coleji.stockwatcher

import com.coleji.neptune.Core.UnlockedRequestCache

trait StockWatcherTask {
	def run(rc: UnlockedRequestCache): Unit
}
