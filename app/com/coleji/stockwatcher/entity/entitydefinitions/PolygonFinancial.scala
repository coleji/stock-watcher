package com.coleji.stockwatcher.entity.entitydefinitions

import com.coleji.neptune.Storable.FieldValues.{DoubleFieldValue, IntFieldValue, StringFieldValue}
import com.coleji.neptune.Storable.Fields.{DoubleDatabaseField, IntDatabaseField, StringDatabaseField}
import com.coleji.neptune.Storable.{FieldsObject, StorableClass, StorableObject, ValuesObject}

class PolygonFinancial extends StorableClass(PolygonFinancial) {
	override object values extends ValuesObject {
		val financialId = new IntFieldValue(self, PolygonFinancial.fields.financialId)
		val financialEventId = new IntFieldValue(self, PolygonFinancial.fields.financialEventId)
		val financialKey = new StringFieldValue(self,PolygonFinancial.fields.financialKey)
		val label = new StringFieldValue(self, PolygonFinancial.fields.label)
		val financialOrder = new IntFieldValue(self, PolygonFinancial.fields.financialOrder)
		val unit = new StringFieldValue(self, PolygonFinancial.fields.unit)
		val value = new DoubleFieldValue(self, PolygonFinancial.fields.value)
	}
}

object PolygonFinancial extends StorableObject[PolygonFinancial] {
	override val entityName: String = "s_p_financials"

	object fields extends FieldsObject {
		val financialId = new IntDatabaseField(self, "financial_id")
		val financialEventId = new IntDatabaseField(self, "financial_event_id")
		val financialKey = new StringDatabaseField(self, "financial_key", 100)
		val label = new StringDatabaseField(self, "label", 100)
		val financialOrder = new IntDatabaseField(self, "financial_order")
		val unit = new StringDatabaseField(self, "unit", 20)
		val value = new DoubleDatabaseField(self, "value")
	}

	def primaryKey: IntDatabaseField = fields.financialId

	def apply(
		financialEventId: Int,
		financialKey: String,
		label: String,
		financialOrder: Int,
		unit: String,
		value: Double,
	): PolygonFinancial = {
		val ret = new PolygonFinancial
		ret.values.financialEventId.update(financialEventId)
		ret.values.financialKey.update(financialKey)
		ret.values.label.update(label)
		ret.values.financialOrder.update(financialOrder)
		ret.values.unit.update(unit)
		ret.values.value.update(value)
		ret
	}
}