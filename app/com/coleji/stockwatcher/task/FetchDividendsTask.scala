package com.coleji.stockwatcher.task

import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.neptune.IO.PreparedQueries.PreparedQueryForSelect
import com.coleji.neptune.Storable.ResultSetWrapper
import com.coleji.neptune.Util.DateUtil
import com.coleji.stockwatcher.StockWatcherTask
import com.coleji.stockwatcher.entity.entitydefinitions.PolygonDividend
import com.coleji.stockwatcher.remoteapi.polygon.dividends.Dividends
import org.slf4j.LoggerFactory

import java.time.{LocalDate, ZonedDateTime}

object FetchDividendsTask extends StockWatcherTask {
	private val logger = LoggerFactory.getLogger(this.getClass.getName)
	override def getNextRuntime: ZonedDateTime = DateUtil.setHour(ZonedDateTime.now().plusDays(1), API_FETCH_HOUR)

	protected override def taskAction(rc: UnlockedRequestCache): Unit = {
		val latestDividendQ = new PreparedQueryForSelect[LocalDate](Set(rc.companion)) {

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): LocalDate = rsw.getLocalDate(1)

			override def getQuery: String = "select max(declaration_date) from s_p_dividends"
		}

		val maxDate = Option(rc.executePreparedQueryForSelect(latestDividendQ).head).getOrElse(LocalDate.MIN)

		logger.debug("max date is: " + maxDate)

		val dividends = Dividends.getDividends(maxDate, appendLog)
		val toInsert = dividends
			.filter(d => d.declaration_date.isAfter(maxDate))
			.map(d => PolygonDividend(d.ticker, d.declaration_date, d.ex_dividend_date, d.record_date, d.pay_date, d.cash_amount, d.dividend_type, d.frequency))
		logger.debug("no pay date: " + dividends.count(_.pay_date.isEmpty))
		logger.debug("no frequency: " + dividends.count(_.frequency.isEmpty))
		rc.batchInsertObjects(toInsert)
	}
}
