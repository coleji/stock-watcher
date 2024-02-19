package com.coleji.stockwatcher.entity.entitydefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._

class PolygonDailyOHLC extends StorableClass(PolygonDailyOHLC) {
	override object values extends ValuesObject {
		val id = new IntFieldValue(self, PolygonDailyOHLC.fields.id)
		val marketDate = new DateFieldValue(self, PolygonDailyOHLC.fields.marketDate)
		val ticker = new StringFieldValue(self, PolygonDailyOHLC.fields.ticker)
		val open = new DoubleFieldValue(self ,PolygonDailyOHLC.fields.open)
		val close = new DoubleFieldValue(self, PolygonDailyOHLC.fields.close)
		val high = new DoubleFieldValue(self, PolygonDailyOHLC.fields.high)
		val low = new DoubleFieldValue(self, PolygonDailyOHLC.fields.low)
		val volume = new DoubleFieldValue(self, PolygonDailyOHLC.fields.volume)
		val numberTrans = new IntFieldValue(self, PolygonDailyOHLC.fields.numberTrans)
		val volumeWeightedAverage = new DoubleFieldValue(self, PolygonDailyOHLC.fields.volumeWeightedAverage)
	}
}

object PolygonDailyOHLC extends StorableObject[PolygonDailyOHLC] {
	override val entityName: String = "s_p_daily_ohlc"

	object fields extends FieldsObject {
		val id = new IntDatabaseField(self, "ohlc_id")
		val marketDate = new DateDatabaseField(self, "market_date")
		val ticker = new StringDatabaseField(self, "ticker", 50)
		val open = new DoubleDatabaseField(self, "open")
		val close = new DoubleDatabaseField(self, "close")
		val high = new DoubleDatabaseField(self, "high")
		val low = new DoubleDatabaseField(self, "low")
		val volume = new DoubleDatabaseField(self, "volume")
		val numberTrans = new IntDatabaseField(self, "number_trans")
		val volumeWeightedAverage = new DoubleDatabaseField(self, "volume_weighted_avg")
	}

	def primaryKey: IntDatabaseField = fields.id
}