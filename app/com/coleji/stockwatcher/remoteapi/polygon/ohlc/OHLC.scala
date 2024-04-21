package com.coleji.stockwatcher.remoteapi.polygon.ohlc

import com.coleji.stockwatcher.remoteapi.polygon.PolygonApi
import play.api.libs.json.{JsArray, JsObject}

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object OHLC {
	def getOHLC(forDate: LocalDate, log: String => Unit): DtoOHLC = {
		val rawResult = PolygonApi.callApi(getPath(forDate))

		try {
			val dto: DtoOHLC = DtoOHLC(rawResult)
			log("Successfully fetched " + forDate)
			if (dto.results.isDefined) {
				val volumesArray = rawResult.asInstanceOf[JsObject]("results").asInstanceOf[JsArray]
				val volumes = volumesArray.value
				val parsedVolumes = dto.results.get
				parsedVolumes.zipWithIndex.foreach(t => {
					val index = t._2
					val original = volumes(index).asInstanceOf[JsObject].value("v").toString()
					if (t._1.v.doubleValue.toString != original.toDouble.toString) {
						log("*** Losing data: original: " + original + "; parsed double value: " + t._1.v.doubleValue)
					}
				})
			}
			dto
		} catch {
			case e: Throwable => {
				log(e.getMessage)
				log(rawResult.toString())
				throw e
			}
		}
	}

	def getPath(forDate: LocalDate): String = s"/v2/aggs/grouped/locale/us/market/stocks/${forDate.format(DateTimeFormatter.ISO_LOCAL_DATE)}"
}
