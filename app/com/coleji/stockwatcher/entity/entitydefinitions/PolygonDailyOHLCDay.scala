package com.coleji.stockwatcher.entity.entitydefinitions

import com.coleji.neptune.Storable.FieldValues.{DateFieldValue, DoubleFieldValue, IntFieldValue, StringFieldValue}
import com.coleji.neptune.Storable.Fields.{DateDatabaseField, DoubleDatabaseField, IntDatabaseField, StringDatabaseField}
import com.coleji.neptune.Storable.{FieldsObject, StorableClass, StorableObject, ValuesObject}

import java.time.LocalDate

class PolygonDailyOHLCDay extends StorableClass(PolygonDailyOHLCDay) {
	override object values extends ValuesObject {
		val id = new IntFieldValue(self, PolygonDailyOHLCDay.fields.id)
		val marketDate = new DateFieldValue(self, PolygonDailyOHLCDay.fields.marketDate)
		val resultCount = new IntFieldValue(self, PolygonDailyOHLCDay.fields.resultCount)
		val fetchDate = new DateFieldValue(self, PolygonDailyOHLCDay.fields.fetchDate)
	}
}

object PolygonDailyOHLCDay extends StorableObject[PolygonDailyOHLCDay] {
	override val entityName: String = "s_p_daily_ohlc_day"

	object fields extends FieldsObject {
		val id = new IntDatabaseField(self, "day_id")
		val marketDate = new DateDatabaseField(self, "market_date")
		val resultCount = new IntDatabaseField(self, "result_count")
		val fetchDate = new DateDatabaseField(self, "fetch_date")
	}

	def primaryKey: IntDatabaseField = fields.id

	def apply(marketDate: LocalDate, resultCount: Int, fetchDate: LocalDate): PolygonDailyOHLCDay = {
		val ret = new PolygonDailyOHLCDay
		ret.values.marketDate.update(marketDate)
		ret.values.resultCount.update(resultCount)
		ret.values.fetchDate.update(fetchDate)
		ret
	}
}