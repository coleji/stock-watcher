package com.coleji.stockwatcher.entity.entitydefinitions

import com.coleji.neptune.Storable.FieldValues.{DateFieldValue, DoubleFieldValue, IntFieldValue, NullableDoubleFieldValue, NullableIntFieldValue, StringFieldValue}
import com.coleji.neptune.Storable.Fields.{DateDatabaseField, DoubleDatabaseField, IntDatabaseField, NullableDoubleDatabaseField, NullableIntDatabaseField, StringDatabaseField}
import com.coleji.neptune.Storable.{FieldsObject, StorableClass, StorableObject, ValuesObject}

import java.time.LocalDate

class CalculatedDailyValue extends StorableClass(CalculatedDailyValue) {
	override object values extends ValuesObject {
		val id = new IntFieldValue(self, CalculatedDailyValue.fields.id)
		val marketDate = new DateFieldValue(self, CalculatedDailyValue.fields.marketDate)
		val ticker = new StringFieldValue(self, CalculatedDailyValue.fields.ticker)
		val dataSource = new StringFieldValue(self, CalculatedDailyValue.fields.dataSource)
		val priceEffectiveDate = new DateFieldValue(self, CalculatedDailyValue.fields.priceEffectiveDate) // which spilts are applied to the historical price values
		val dailyBasicEps = new DoubleFieldValue(self, CalculatedDailyValue.fields.dailyBasicEps)
		val dailyDilutedEps = new DoubleFieldValue(self, CalculatedDailyValue.fields.dailyDilutedEps)
		val dailyBasicPE = new DoubleFieldValue(self, CalculatedDailyValue.fields.dailyBasicPE)
		val dailyDilutedPE = new DoubleFieldValue(self, CalculatedDailyValue.fields.dailyDilutedPE)
	}
}

object CalculatedDailyValue extends StorableObject[CalculatedDailyValue] {
	val DATA_SOURCE_POLYGON = "polygon"

	override val entityName: String = "c_daily_values"

	object fields extends FieldsObject {
		val id = new IntDatabaseField(self, "id")
		val marketDate = new DateDatabaseField(self, "market_date")
		val ticker = new StringDatabaseField(self, "ticker", 50)
		val dataSource = new StringDatabaseField(self, "data_source", 50) // eg polygon
		val priceEffectiveDate = new DateDatabaseField(self, "price_effective_date") // which spilts are applied to the historical price values
		val dailyBasicEps = new DoubleDatabaseField(self, "daily_basic_eps")
		val dailyDilutedEps = new DoubleDatabaseField(self, "daily_diluted_eps")
		val dailyBasicPE = new DoubleDatabaseField(self, "daily_basic_pe")
		val dailyDilutedPE = new DoubleDatabaseField(self, "daily_diluted_pe")
	}

	def primaryKey: IntDatabaseField = fields.id

	def getMultiplier(start: LocalDate, end: LocalDate, splits: List[PolygonSplit]): Float = {
		val relevantSplits = splits.filter(s => {
			val d = s.values.executionDate.get
			d.isAfter(start) && !d.isBefore(end)
		})
		val (from, to) = relevantSplits.foldLeft((1f, 1f))(
			(ft, s) => (ft._1 * s.values.splitFrom.get, ft._2 * s.values.splitTo.get)
		)
		to / from
	}

	def apply(ohlc: PolygonDailyOHLC, now: LocalDate, splits: List[PolygonSplit], financials: List[(LocalDate, PolygonFinancial)]): CalculatedDailyValue = {
		val ret = new CalculatedDailyValue
		ret.values.marketDate.update(ohlc.values.marketDate.get)
		ret.values.ticker.update(ohlc.values.ticker.get)
		ret.values.dataSource.update(DATA_SOURCE_POLYGON)
		ret.values.priceEffectiveDate.update(now)

		// TODO: EPS and PE based on financial storables

		ret
	}
}