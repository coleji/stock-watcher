package com.coleji.stockwatcher.entity.entitydefinitions

import com.coleji.neptune.Storable.FieldValues.{IntFieldValue, StringFieldValue}
import com.coleji.neptune.Storable.Fields.{IntDatabaseField, StringDatabaseField}
import com.coleji.neptune.Storable.{FieldsObject, StorableClass, StorableObject, ValuesObject}

class PolygonFinancialEventTicker extends StorableClass(PolygonFinancialEventTicker) {
	override object values extends ValuesObject {
		val mappingId = new IntFieldValue(self, PolygonFinancialEventTicker.fields.mappingId)
		val financialEventId = new IntFieldValue(self, PolygonFinancialEventTicker.fields.financialEventId)
		val ticker = new StringFieldValue(self, PolygonFinancialEventTicker.fields.ticker)
	}
}

object PolygonFinancialEventTicker extends StorableObject[PolygonFinancialEventTicker] {
	override val entityName: String = "s_p_financials_event_tickers"

	object fields extends FieldsObject {
		val mappingId = new IntDatabaseField(self, "mapping_id")
		val financialEventId = new IntDatabaseField(self, "financial_event_id")
		val ticker = new StringDatabaseField(self, "ticker", 15)
	}

	def primaryKey: IntDatabaseField = fields.mappingId

	def apply(
		financialEventId: Int,
		ticker: String,
	): PolygonFinancialEventTicker = {
		val ret = new PolygonFinancialEventTicker
		ret.values.financialEventId.update(financialEventId)
		ret.values.ticker.update(ticker)
		ret
	}
}