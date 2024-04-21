package com.coleji.stockwatcher.task

import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.stockwatcher.entity.entitydefinitions.{PolygonDailyOHLC, PolygonDailyOHLCDay}
import com.coleji.stockwatcher.entity.repository.OHLCRepository
import com.coleji.stockwatcher.remoteapi.polygon.ohlc.OHLC
import com.coleji.stockwatcher.{StockWatcherTask, TickerReference}

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object FetchDailyOHLCsTask extends StockWatcherTask {
	private val START_DATE = LocalDate.now.minusYears(5).plusDays(3)

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
					appendLog("Got results: " + results.resultsCount + " status: " + results.status)
					if (results.status == "OK") {
						val day = PolygonDailyOHLCDay(currentDate, results.resultsCount, LocalDate.now())
						rc.commitObjectToDatabase(day)
						val ohlcs = results.results.getOrElse(List.empty)
							.filter(r => !TickerReference.blacklistedTickers.contains(r.T))
							.map(r => PolygonDailyOHLC(
								marketDate = currentDate,
								ticker = r.T,
								open = r.o,
								close = r.c,
								high = r.h,
								low = r.l,
								volume = r.v.doubleValue,
								numberTrans = r.n,
								volumeWeightedAverage = r.vw
							))
						rc.batchInsertObjects(ohlcs)
					} else {
						appendLog("Got bad status " + results.status + " for date " + currentDate)
					}
				} else {
					dbRecords = dbRecords.tail
				}
			}

			currentDate = currentDate.plusDays(1)
		}
	}
}
