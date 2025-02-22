package com.coleji.stockwatcher.remoteapi.polygon.splits

import play.api.libs.json.{Format, JsValue, Json}

case class DtoSplits(results: Option[List[DtoSplitResult]], status: String, next_url: Option[String])

object DtoSplits {
	implicit val formatResult: Format[DtoSplitResult] = Json.format[DtoSplitResult]
	implicit val format: Format[DtoSplits] = Json.format[DtoSplits]

	def apply(v: JsValue): DtoSplits = v.as[DtoSplits]
}

case class DtoSplitResult(execution_date: String, split_from: Float, split_to: Float, ticker: String)

object DtoSplitResult {
	implicit val format: Format[DtoSplitResult] = Json.format[DtoSplitResult]

	def apply(v: JsValue): DtoSplitResult = v.as[DtoSplitResult]
}
