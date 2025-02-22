package com.coleji.stockwatcher.remoteapi.polygon.dividends

import com.coleji.stockwatcher.remoteapi.polygon.PolygonApi
import org.apache.hc.core5.net.URIBuilder
import org.slf4j.LoggerFactory
import play.api.libs.json.{JsArray, JsObject}

import java.time.LocalDate

object Dividends {
	private val logger = LoggerFactory.getLogger(this.getClass.getName)
	def getDividends(until: LocalDate, log: String => Unit): List[DtoDividendResult] = {
		var ct = 1
		var ret: List[DtoDividendResult] = List.empty
		var cursor: Option[String] = Option.empty
		var firstRun = true

		while (cursor.isDefined || firstRun) {
			firstRun = false
			cursor.foreach(logger.debug)
			val rawResult = PolygonApi.callApi(getPath(cursor))

			try {
				val rawReults = rawResult.asInstanceOf[JsObject].value.get("results").get.asInstanceOf[JsArray].value
				rawReults.foreach(r => {
					DtoDividendResult(r)
				})
				val dto: DtoDividends = DtoDividends(rawResult)
				logger.debug("Successfully fetched " + ct)
				cursor = dto.next_url.flatMap(getCursorFromUrl)
				dto.results match {
					case Some(rr) => {
						logger.debug(s"fetched ${rr.length} dividends from ${rr.head.declaration_date} to ${rr.last.declaration_date}")
						ret = rr.reverse ++ ret
						val lastSeenDate = rr.last.declaration_date
						logger.debug(s"checking lf ${lastSeenDate} is before ${until}")
						if (lastSeenDate.isBefore(until)) {
							logger.debug("yep")
							cursor = None
						}
					}
				}
			} catch {
				case e: Throwable => {
					log(e.getMessage)
					log(rawResult.toString())
					throw e
				}
			}
			ct += 1
		} 
		ret
	}

	def getCursorFromUrl(url: String): Option[String] = {
		// mix java and scala shit, blegh
		val optionalValue = new URIBuilder(url).getQueryParams.stream().filter(_.getName.equals("cursor")).findFirst()
		if (optionalValue.isPresent) Some(optionalValue.get().getValue)
		else None
	}

	def getPath(cursor: Option[String]): String = s"/v3/reference/dividends?limit=1000&sort=declaration_date${cursor.map(c => s"&cursor=$c").getOrElse("")}"
}
