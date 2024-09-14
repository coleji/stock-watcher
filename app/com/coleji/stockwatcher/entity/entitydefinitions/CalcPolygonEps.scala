package com.coleji.stockwatcher.entity.entitydefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable.{FieldsObject, StorableClass, StorableObject, ValuesObject}

class CalcPolygonEps extends StorableClass(CalcPolygonEps) {
	override object values extends ValuesObject {
		val id = new IntFieldValue(self, CalcPolygonEps.fields.id)
		val ticker = new StringFieldValue(self, CalcPolygonEps.fields.ticker)
		val endDate = new DateFieldValue(self, CalcPolygonEps.fields.endDate)
		val reportAcceptedDatetime = new NullableDateTimeFieldValue(self, CalcPolygonEps.fields.reportAcceptedDatetime)
		val calculatedDate = new DateFieldValue(self, CalcPolygonEps.fields.calculatedDate)
		val basicEps = new DoubleFieldValue(self, CalcPolygonEps.fields.basicEps)
		val dilutedEps = new DoubleFieldValue(self, CalcPolygonEps.fields.dilutedEps)
	}
}

object CalcPolygonEps extends StorableObject[CalcPolygonEps] {
	override val entityName: String = "c_p_eps"

	object fields extends FieldsObject {
		val id = new IntDatabaseField(self, "eps_id")
		val ticker = new StringDatabaseField(self, "ticker", 50)
		val endDate = new DateDatabaseField(self, "end_date")
		val reportAcceptedDatetime = new NullableDateTimeDatabaseField(self, "report_accepted_date_time")
		val calculatedDate = new DateDatabaseField(self, "calculated_date")
		val basicEps = new DoubleDatabaseField(self, "basic_eps")
		val dilutedEps = new DoubleDatabaseField(self, "diluted_eps")
	}

	def primaryKey: IntDatabaseField = fields.id
}