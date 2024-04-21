package com.coleji.stockwatcher.task

import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.neptune.IO.PreparedQueries.PreparedQueryForSelect
import com.coleji.neptune.Storable.ResultSetWrapper
import com.coleji.stockwatcher.StockWatcherTask
import com.coleji.stockwatcher.entity.entitydefinitions.PolygonSplit
import com.coleji.stockwatcher.remoteapi.polygon.financials.Financials
import com.coleji.stockwatcher.remoteapi.polygon.splits.Splits
import com.coleji.stockwatcher.task.FetchSplitsTask.appendLog

import java.time.LocalDate

object FetchFinancialsTask extends StockWatcherTask {
	protected override def taskAction(rc: UnlockedRequestCache): Unit = {
		val latestFilingQ = new PreparedQueryForSelect[LocalDate](Set(rc.companion)) {

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): LocalDate = rsw.getLocalDate(1)

			override def getQuery: String = "select max(filing_date) from s_p_financials_events"
		}

		val maxDate = Option(rc.executePreparedQueryForSelect(latestFilingQ).head).getOrElse(LocalDate.MIN)

		println("max date is: " + maxDate)

		val events = Financials.getFinancials(LocalDate.MIN, appendLog)
		println(events.length)
		//		println(splits)
//		val toInsert = events
//			.filter(s => LocalDate.parse(s.filing_date).isAfter(maxDate))
//			.map(s => PolygonSplit(LocalDate.parse(s.execution_date), s.ticker, s.split_from, s.split_to))
		//		println(toInsert)
//		rc.batchInsertObjects(toInsert)
	}
}
