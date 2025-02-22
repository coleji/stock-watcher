package com.coleji.stockwatcher.remoteapi.polygon.splits

import com.coleji.stockwatcher.remoteapi.polygon.PolygonApi
import org.apache.hc.core5.net.URIBuilder
import org.slf4j.LoggerFactory

import java.time.LocalDate

object Splits {
	private val logger = LoggerFactory.getLogger(this.getClass.getName)
	def getSplits(until: LocalDate, log: String => Unit): List[DtoSplitResult] = {
		var ct = 1
		var ret: List[DtoSplitResult] = List.empty
		var cursor: Option[String] = Option.empty
		var firstRun = true

		while (cursor.isDefined || firstRun) {
			firstRun = false
			cursor.foreach(logger.debug)
			val rawResult = PolygonApi.callApi(getPath(cursor))

			try {
				val dto: DtoSplits = DtoSplits(rawResult)
				logger.debug("Successfully fetched " + ct)
				cursor = dto.next_url.flatMap(getCursorFromUrl)
				dto.results match {
					case Some(rr) => {
						logger.debug(s"fetched ${rr.length} splits from ${rr.head.execution_date} to ${rr.last.execution_date}")
						ret = rr.reverse ++ ret
						val lastSeenDate = LocalDate.parse(rr.last.execution_date)
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

	def getPath(cursor: Option[String]): String = s"/v3/reference/splits?limit=1000&sort=execution_date${cursor.map(c => s"&cursor=$c").getOrElse("")}"
}
