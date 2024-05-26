package com.coleji.stockwatcher.entity.entitydefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable.{FieldsObject, StorableClass, StorableObject, ValuesObject}

import java.time.LocalDate

class PolygonDividend extends StorableClass(PolygonDividend) {
	override object values extends ValuesObject {
		val dividendId = new IntFieldValue(self, PolygonDividend.fields.dividendId)
		val ticker = new StringFieldValue(self, PolygonDividend.fields.ticker)
		val declarationDate = new DateFieldValue(self, PolygonDividend.fields.declarationDate)
		val exDividendDate = new DateFieldValue(self, PolygonDividend.fields.exDividendDate)
		val recordDate = new DateFieldValue(self, PolygonDividend.fields.recordDate)
		val payDate = new NullableDateFieldValue(self, PolygonDividend.fields.payDate)
		val cashAmount = new DoubleFieldValue(self, PolygonDividend.fields.cashAmount)
		val dividendType = new StringFieldValue(self, PolygonDividend.fields.dividendType)
		val frequency = new NullableIntFieldValue(self, PolygonDividend.fields.frequency)
	}
}

object PolygonDividend extends StorableObject[PolygonDividend] {
	override val entityName: String = "s_p_dividends"

	object fields extends FieldsObject {
		val dividendId = new IntDatabaseField(self, "dividend_id")
		val ticker = new StringDatabaseField(self, "ticker", 50)
		val declarationDate = new DateDatabaseField(self, "declaration_date")
		val exDividendDate = new DateDatabaseField(self, "ex_dividend_date")
		val recordDate = new DateDatabaseField(self, "record_date")
		val payDate = new NullableDateDatabaseField(self, "pay_date")
		val cashAmount = new DoubleDatabaseField(self, "cash_amount")
		val dividendType = new StringDatabaseField(self, "dividend_type", 2)
		val frequency = new NullableIntDatabaseField(self, "frequency")
	}

	def primaryKey: IntDatabaseField = fields.dividendId

	def apply(
		 ticker: String, declarationDate: LocalDate, exDividendDate: LocalDate,
		 recordDate: LocalDate, payDate: Option[LocalDate], cashAmount: Double,
		 dividendType: String, frequency: Option[Int]
	 ): PolygonDividend = {
		val ret = new PolygonDividend
		ret.values.ticker.update(ticker)
		ret.values.declarationDate.update(declarationDate)
		ret.values.exDividendDate.update(exDividendDate)
		ret.values.recordDate.update(recordDate)
		ret.values.payDate.update(payDate)
		ret.values.cashAmount.update(cashAmount)
		ret.values.dividendType.update(dividendType)
		ret.values.frequency.update(frequency)
		ret
	}
}
