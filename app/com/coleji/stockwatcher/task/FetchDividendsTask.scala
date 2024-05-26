package com.coleji.stockwatcher.task

import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.neptune.IO.PreparedQueries.PreparedQueryForSelect
import com.coleji.neptune.Storable.ResultSetWrapper
import com.coleji.stockwatcher.StockWatcherTask
import com.coleji.stockwatcher.entity.entitydefinitions.PolygonDividend
import com.coleji.stockwatcher.remoteapi.polygon.dividends.Dividends

import java.time.LocalDate

object FetchDividendsTask extends StockWatcherTask {
	protected override def taskAction(rc: UnlockedRequestCache): Unit = {
		val latestDividendQ = new PreparedQueryForSelect[LocalDate](Set(rc.companion)) {

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): LocalDate = rsw.getLocalDate(1)

			override def getQuery: String = "select max(declaration_date) from s_p_dividends"
		}

		val maxDate = Option(rc.executePreparedQueryForSelect(latestDividendQ).head).getOrElse(LocalDate.MIN)

		println("max date is: " + maxDate)

		val dividends = Dividends.getDividends(maxDate, appendLog)
//		println(dividends)
		val toInsert = dividends
			.filter(d => d.declaration_date.isAfter(maxDate))
			.map(d => PolygonDividend(d.ticker, d.declaration_date, d.ex_dividend_date, d.record_date, d.pay_date, d.cash_amount, d.dividend_type, d.frequency))
		println("no pay date: " + dividends.count(_.pay_date.isEmpty))
		println("no frequency: " + dividends.count(_.frequency.isEmpty))
//		println(toInsert)
		rc.batchInsertObjects(toInsert)
	}
}
