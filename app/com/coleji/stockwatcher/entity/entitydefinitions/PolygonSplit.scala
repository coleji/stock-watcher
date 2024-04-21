package com.coleji.stockwatcher.entity.entitydefinitions

import com.coleji.neptune.Storable.FieldValues.{DateFieldValue, FloatFieldValue, IntFieldValue, StringFieldValue}
import com.coleji.neptune.Storable.Fields.{DateDatabaseField, FloatDatabaseField, IntDatabaseField, StringDatabaseField}
import com.coleji.neptune.Storable.{FieldsObject, StorableClass, StorableObject, ValuesObject}

import java.time.LocalDate

class PolygonSplit extends StorableClass(PolygonSplit) {
	override object values extends ValuesObject {
		val splitId = new IntFieldValue(self, PolygonSplit.fields.splitId)
		val executionDate = new DateFieldValue(self, PolygonSplit.fields.executionDate)
		val ticker = new StringFieldValue(self, PolygonSplit.fields.ticker)
		val splitFrom = new FloatFieldValue(self ,PolygonSplit.fields.splitFrom)
		val splitTo = new FloatFieldValue(self ,PolygonSplit.fields.splitTo)
	}
}

object PolygonSplit extends StorableObject[PolygonSplit] {
	override val entityName: String = "s_p_splits"

	object fields extends FieldsObject {
		val splitId = new IntDatabaseField(self, "split_id")
		val executionDate = new DateDatabaseField(self, "execution_date")
		val ticker = new StringDatabaseField(self, "ticker", 50)
		val splitFrom = new FloatDatabaseField(self, "split_from")
		val splitTo = new FloatDatabaseField(self, "split_to")
	}

	def primaryKey: IntDatabaseField = fields.splitId

	def apply(executionDate: LocalDate, ticker: String, splitFrom: Float, splitTo: Float): PolygonSplit = {
		val ret = new PolygonSplit
		ret.values.executionDate.update(executionDate)
		ret.values.ticker.update(ticker)
		ret.values.splitFrom.update(splitFrom)
		ret.values.splitTo.update(splitTo)
		ret
	}
}