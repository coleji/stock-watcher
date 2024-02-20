package com.coleji.stockwatcher.task

import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.stockwatcher.StockWatcherTask
import com.coleji.stockwatcher.entity.entitydefinitions.PolygonDailyOHLCDay
import com.coleji.stockwatcher.entity.repository.OHLCRepository
import com.coleji.stockwatcher.remoteapi.polygon.ohlc.OHLC

import java.time.{LocalDate, ZonedDateTime}
import java.time.format.DateTimeFormatter

object FetchDailyOHLCsTask extends StockWatcherTask {
	val START_DATE = LocalDate.now.minusYears(5).plusDays(3)

	protected override def taskAction(rc: UnlockedRequestCache): Unit = {
		var currentDate = START_DATE
		appendLog("starting with " + currentDate)
		var dbRecords: List[PolygonDailyOHLCDay] = List.empty
		while (currentDate.isBefore(LocalDate.now())) {
			if (dbRecords.isEmpty) {
				dbRecords = OHLCRepository.getOHLCDayBatchAfterDate(rc, currentDate.minusDays(1), 20)
			}

			if ((currentDate.format(DateTimeFormatter.ofPattern("e")).toInt-1)%6 != 0) { // skip weekends (1 and 7)
				if (dbRecords.isEmpty || dbRecords.head.values.marketDate.get.isAfter(currentDate)) {
					appendLog("Fetching: " + currentDate + " " + currentDate.format(DateTimeFormatter.ofPattern("e")).toInt)
					val results = OHLC.getOHLC(currentDate, appendLog)
					appendLog("Got results: " + results.resultsCount)
				} else {
					dbRecords = dbRecords.tail
				}
			}

			currentDate = currentDate.plusDays(1)
		}
	}

	def main(args: Array[String]): Unit = {
		(1 to 10).foreach(i => {
			val d = ZonedDateTime.now().plusDays(i)
			println("=====")
			println(d)
			println(d.format(DateTimeFormatter.ofPattern("E")))
			println(d.format(DateTimeFormatter.ofPattern("e")))
			println((d.format(DateTimeFormatter.ofPattern("e")).toInt-1)%6 != 0)
		})
		(1 to 10).foreach(i => {
			val d = LocalDate.now().plusDays(i)
			println("=====")
			println(d)
			println(d.format(DateTimeFormatter.ofPattern("E")))
			println(d.format(DateTimeFormatter.ofPattern("e")))
			println((d.format(DateTimeFormatter.ofPattern("e")).toInt-1)%6 != 0)
		})
	}
}
