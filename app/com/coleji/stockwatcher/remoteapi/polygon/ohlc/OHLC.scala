package com.coleji.stockwatcher.remoteapi.polygon.ohlc

import com.coleji.stockwatcher.remoteapi.polygon.PolygonApi
import play.api.libs.json.{JsArray, JsObject, JsValue, Json}
import play.api.libs.ws.WSClient

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object OHLC {
	def getOHLC(forDate: LocalDate): DtoOHLC = {
		val rawResult = PolygonApi.callApi(getPath(forDate))
		val volumesArray = rawResult.asInstanceOf[JsObject]("results").asInstanceOf[JsArray]
		val volumes = volumesArray.value

		val dto: DtoOHLC = DtoOHLC(rawResult)
		val parsedVolumes = dto.results.get
		parsedVolumes.zipWithIndex.foreach(t => {
			val index = t._2
			val original = volumes(index).asInstanceOf[JsObject].value("v").toString()
			if (t._1.v.doubleValue.toString != original.toDouble.toString) {
				println("*** Losing data: original: " + original + "; parsed double value: " + t._1.v.doubleValue)
			}

		})
		println("parse successful")
		dto
	}

	def getPath(forDate: LocalDate): String = s"/v2/aggs/grouped/locale/us/market/stocks/${forDate.format(DateTimeFormatter.ISO_LOCAL_DATE)}"
}
