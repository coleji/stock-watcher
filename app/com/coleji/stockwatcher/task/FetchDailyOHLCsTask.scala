package com.coleji.stockwatcher.task

import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.stockwatcher.StockWatcherTask
import com.coleji.stockwatcher.entity.repository.OHLCRepository
import com.coleji.stockwatcher.remoteapi.polygon.ohlc.OHLC

import java.time.LocalDate

object FetchDailyOHLCsTask extends StockWatcherTask {
	val START_DATE = LocalDate.now.minusYears(2).plusDays(3)

	def run(rc: UnlockedRequestCache): Unit = {
		var currentDate = START_DATE
//		while (currentDate.isBefore(LocalDate.now())) {
			val days = OHLCRepository.getOHLCDayBatchAfterDate(rc, currentDate, 20)
			println(days)
//		}
		OHLC.getOHLC(currentDate)
	}
}
