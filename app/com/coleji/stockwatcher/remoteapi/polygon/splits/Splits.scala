package com.coleji.stockwatcher.remoteapi.polygon.splits

import com.coleji.stockwatcher.remoteapi.polygon.PolygonApi
import org.apache.hc.core5.net.URIBuilder

import java.time.LocalDate

object Splits {
	def getSplits(until: LocalDate, log: String => Unit): List[DtoSplitResult] = {
		var ct = 1
		var ret: List[DtoSplitResult] = List.empty
		var cursor: Option[String] = Option.empty

		do {
			cursor.foreach(println)
			val rawResult = PolygonApi.callApi(getPath(cursor))

			try {
				val dto: DtoSplits = DtoSplits(rawResult)
				println("Successfully fetched " + ct)
				cursor = dto.next_url.flatMap(getCursorFromUrl)
				dto.results match {
					case Some(rr) => {
						println(s"fetched ${rr.length} splits from ${rr.head.execution_date} to ${rr.last.execution_date}")
						ret = rr.reverse ++ ret
						val lastSeenDate = LocalDate.parse(rr.last.execution_date)
						println(s"checking lf ${lastSeenDate} is before ${until}")
						if (lastSeenDate.isBefore(until)) {
							println("yep")
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
		} while (cursor.isDefined)
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
