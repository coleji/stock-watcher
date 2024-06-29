package com.coleji.stockwatcher.remoteapi.polygon.financials

import com.coleji.stockwatcher.remoteapi.polygon.PolygonApi
import org.apache.hc.core5.net.URIBuilder
import org.slf4j.LoggerFactory
import play.api.libs.json.{JsArray, JsObject, Json}

import java.time.LocalDate

object Financials {
	private val logger = LoggerFactory.getLogger(this.getClass.getName)
	def getFinancials(until: LocalDate, log: String => Unit): List[DtoFinancialsEvent] = {
		var ct = 1
		var total = 0
		var ret: List[DtoFinancialsEvent] = List.empty
		var cursor: Option[String] = Option.empty

		do {
			cursor.foreach(logger.debug)
			val rawResult = PolygonApi.callApi(getPath(cursor))

			try {
				val resultsArray = rawResult.asInstanceOf[JsObject]("results").asInstanceOf[JsArray]
				val updatedResults = resultsArray.value.map(r => {
					val newEventHash = collection.mutable.Map(r.asInstanceOf[JsObject].value.toSeq: _*)
					val newfinancialsHash = collection.mutable.Map(newEventHash("financials").asInstanceOf[JsObject].value.toSeq: _*)
					if (newfinancialsHash.contains("income_statement")) {
						newfinancialsHash.put("income_statement1", newfinancialsHash("income_statement"))
						newfinancialsHash.put("income_statement2", newfinancialsHash("income_statement"))
						newfinancialsHash.put("income_statement3", newfinancialsHash("income_statement"))
					}
					if (newfinancialsHash.contains("balance_sheet")) {
						newfinancialsHash.put("balance_sheet1", newfinancialsHash("balance_sheet"))
						newfinancialsHash.put("balance_sheet2", newfinancialsHash("balance_sheet"))
					}

					newEventHash.put("financials", Json.toJson(newfinancialsHash))
					newEventHash
				})

				val newApiResultHash = collection.mutable.Map(rawResult.asInstanceOf[JsObject].value.toSeq: _*)
				newApiResultHash.put("results", Json.toJson(updatedResults))

				val dto: DtoFinancialsApiResult = DtoFinancialsApiResult(Json.toJson(newApiResultHash))

				logger.debug("Successfully fetched financials " + ct)
				cursor = dto.next_url.flatMap(getCursorFromUrl)
				val rr = dto.results
				logger.debug(s"fetched ${rr.length} financials from ${rr.head.filing_date} to ${rr.last.filing_date}")
				ret = rr.reverse ++ ret
				total = total + rr.size
				logger.debug("total so far: " + total)
				val lastSeenDate = rr.last.filing_date.getOrElse(LocalDate.parse("1971-01-01"))
				logger.debug(s"checking lf ${lastSeenDate} is before ${until}")
				if (lastSeenDate.isBefore(until)) {
					logger.debug("yep")
					cursor = None
				}
			} catch {
				case e: Throwable => {
					log(e.getMessage)
					log(rawResult.toString())
					throw e
				}
			}
			ct += 1
		} while (cursor.isDefined)
		ret
	}

	def getCursorFromUrl(url: String): Option[String] = {
		// mix java and scala shit, blegh
		val optionalValue = new URIBuilder(url).getQueryParams.stream().filter(_.getName.equals("cursor")).findFirst()
		if (optionalValue.isPresent) Some(optionalValue.get().getValue)
		else None
	}

	def getPath(cursor: Option[String]): String = s"/vX/reference/financials?limit=100&sort=filing_date${cursor.map(c => s"&cursor=$c").getOrElse("")}"
}
