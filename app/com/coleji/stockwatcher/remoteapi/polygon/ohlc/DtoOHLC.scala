package com.coleji.stockwatcher.remoteapi.polygon.ohlc

import play.api.libs.json.{JsValue, Json}

import java.math.BigInteger

case class DtoOHLC (adjusted: Boolean, queryCount: Int, results: Option[List[DtoOHLCResult]], resultsCount: Int, status: String)

object DtoOHLC {
	implicit val formatDtoOHLCResult = Json.format[DtoOHLCResult]
	implicit val format = Json.format[DtoOHLC]

	def apply(v: JsValue): DtoOHLC = {
		try {
			v.as[DtoOHLC]
		} catch {
			case _: Throwable => {
				println("failed to parse the whole thing")
				null
			}
		}
	}
}

case class DtoOHLCResult(
	T: String,
	c: Double,
	h: Double,
	l: Double,
	n: Option[Int],
	o: Double,
	t: Long,
	v: BigDecimal,
	vw: Option[Double]
)

object DtoOHLCResult{
	implicit val format = Json.format[DtoOHLCResult]

	def apply(v: JsValue): DtoOHLCResult = {
		try {
			v.as[DtoOHLCResult]
		} catch {
			case _: Throwable => {
				println("failed to parse: " + v)
				null
			}
		}
	}
}