package com.coleji.stockwatcher.task

import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.neptune.Util.DateUtil
import com.coleji.stockwatcher.StockWatcherTask
import com.coleji.stockwatcher.entity.entitydefinitions.PolygonFinancialEventTicker

import java.time.ZonedDateTime

object CalcEpsTask extends StockWatcherTask {
	override def getNextRuntime: ZonedDateTime = DateUtil.setHour(ZonedDateTime.now().plusDays(1), API_CALC_HOUR)

	override protected def taskAction(rc: UnlockedRequestCache): Unit = {
		val ticker = "MSFT"

		val eventTickers = rc.getObjectsByFilters(
			PolygonFinancialEventTicker,
			List(PolygonFinancialEventTicker.fields.ticker.alias.equalsConstant(ticker)),
			Set(
				PolygonFinancialEventTicker.fields.mappingId,
				PolygonFinancialEventTicker.fields.ticker,
				PolygonFinancialEventTicker.fields.financialEventId
			),
			limit=None,
			orderBy = None,
			orderByDesc = true,
			fetchSize = 500
		)

		val eventIds = eventTickers.map(_.values.financialEventId.get).distinct

		println(eventIds)
	}
}
