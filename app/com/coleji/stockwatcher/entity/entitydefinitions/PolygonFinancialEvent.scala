package com.coleji.stockwatcher.entity.entitydefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable.{FieldsObject, StorableClass, StorableObject, ValuesObject}

import java.time.{LocalDate, LocalDateTime}

class PolygonFinancialEvent extends StorableClass(PolygonFinancialEvent) {
	override object values extends ValuesObject {
		val financialEventId = new IntFieldValue(self, PolygonFinancialEvent.fields.financialEventId)
		val startDate = new DateFieldValue(self, PolygonFinancialEvent.fields.startDate)
		val endDate = new DateFieldValue(self, PolygonFinancialEvent.fields.endDate)
		val filingDate = new NullableDateFieldValue(self, PolygonFinancialEvent.fields.filingDate)
		val acceptanceDatetime = new NullableDateTimeFieldValue(self, PolygonFinancialEvent.fields.acceptanceDatetime)
		val timeframe = new StringFieldValue(self, PolygonFinancialEvent.fields.timeframe)
		val fiscalPeriod = new StringFieldValue(self, PolygonFinancialEvent.fields.fiscalPeriod)
		val fiscalYear = new NullableIntFieldValue(self, PolygonFinancialEvent.fields.fiscalYear)
		val cik = new NullableStringFieldValue(self, PolygonFinancialEvent.fields.cik)
		val companyName = new NullableStringFieldValue(self, PolygonFinancialEvent.fields.companyName)
		val sourceFilingUrl = new NullableStringFieldValue(self, PolygonFinancialEvent.fields.sourceFilingUrl)
		val sourceFilingFileUrl = new NullableStringFieldValue(self, PolygonFinancialEvent.fields.sourceFilingFileUrl)
	}
}

object PolygonFinancialEvent extends StorableObject[PolygonFinancialEvent] {
	override val entityName: String = "s_p_financials_events"

	object fields extends FieldsObject {
		val financialEventId = new IntDatabaseField(self, "financial_event_id")
		val startDate = new DateDatabaseField(self, "start_date")
		val endDate = new DateDatabaseField(self, "end_date")
		val filingDate = new NullableDateDatabaseField(self, "filing_date")
		val acceptanceDatetime = new NullableDateTimeDatabaseField(self, "acceptance_datetime")
		val timeframe = new StringDatabaseField(self, "timeframe", 20)
		val fiscalPeriod = new StringDatabaseField(self, "fiscal_period", 20)
		val fiscalYear = new NullableIntDatabaseField(self, "fiscal_year")
		val cik = new NullableStringDatabaseField(self, "cik", 20)
		val companyName = new NullableStringDatabaseField(self, "company_name", 100)
		val sourceFilingUrl = new NullableStringDatabaseField(self, "source_filing_url", 255)
		val sourceFilingFileUrl = new NullableStringDatabaseField(self, "source_filing_file_url", 255)
	}

	def primaryKey: IntDatabaseField = fields.financialEventId

	def apply(
		financialEventId: Int,
		startDate: LocalDate,
		endDate: LocalDate,
		filingDate: Option[LocalDate],
		acceptanceDatetime: Option[LocalDateTime],
		timeframe: String,
		fiscalPeriod: String,
		fiscalYear: Option[Int],
		cik: Option[String],
		companyName: Option[String],
		sourceFilingUrl: Option[String],
		sourceFilingFileUrl: Option[String],
	): PolygonFinancialEvent = {
		val ret = new PolygonFinancialEvent
		ret.values.financialEventId.update(financialEventId)
		ret.values.startDate.update(startDate)
		ret.values.endDate.update(endDate)
		ret.values.filingDate.update(filingDate)
		ret.values.acceptanceDatetime.update(acceptanceDatetime)
		ret.values.timeframe.update(timeframe)
		ret.values.fiscalPeriod.update(fiscalPeriod)
		ret.values.fiscalYear.update(fiscalYear)
		ret.values.cik.update(cik)
		ret.values.companyName.update(companyName)
		ret.values.sourceFilingUrl.update(sourceFilingUrl)
		ret.values.sourceFilingFileUrl.update(sourceFilingFileUrl)
		ret
	}
}