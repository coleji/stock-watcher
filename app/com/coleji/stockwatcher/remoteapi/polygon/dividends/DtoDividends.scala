package com.coleji.stockwatcher.remoteapi.polygon.dividends

import play.api.libs.json.{JsValue, Json}

import java.time.LocalDate

case class DtoDividends(results: Option[List[DtoDividendResult]], status: String, next_url: Option[String])

object DtoDividends {
	implicit val formatResult = Json.format[DtoDividendResult]
	implicit val format = Json.format[DtoDividends]

	def apply(v: JsValue): DtoDividends = v.as[DtoDividends]
}

case class DtoDividendResult(
								cash_amount: Double,
								declaration_date: LocalDate,
								dividend_type: String,
								ex_dividend_date: LocalDate,
								frequency: Option[Int],
								pay_date: Option[LocalDate],
								record_date: LocalDate,
								ticker: String
							)

object DtoDividendResult {
	implicit val format = Json.format[DtoDividendResult]

	def apply(v: JsValue): DtoDividendResult = v.as[DtoDividendResult]
}
