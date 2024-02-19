package com.coleji.stockwatcher.entity.repository

import com.coleji.neptune.Core.{RequestCache, RootRequestCache}
import com.coleji.neptune.IO.PreparedQueries.PreparedQueryForSelect
import com.coleji.neptune.Storable.ResultSetWrapper
import com.coleji.stockwatcher.entity.entitydefinitions.PolygonDailyOHLCDay

import java.time.LocalDate

object OHLCRepository {
	def getOHLCDayBatchAfterDate(rc: RequestCache, afterDate: LocalDate, batchSize: Int = 20): List[PolygonDailyOHLCDay] = {
		rc.assertUnlocked.getObjectsByFilters(PolygonDailyOHLCDay, List(
			PolygonDailyOHLCDay.fields.marketDate.alias.greaterThanConstant(afterDate)
		), PolygonDailyOHLCDay.fieldList.toSet, Some(batchSize), Some(PolygonDailyOHLCDay.fields.marketDate), orderByDesc = false)
	}
}
