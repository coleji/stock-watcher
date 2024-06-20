package com.coleji.stockwatcher.endpoints

import com.coleji.neptune.Core.PermissionsAuthority
import com.opencsv.{CSVReader, CSVReaderBuilder, RFC4180ParserBuilder}
import org.apache.hc.client5.http.utils.Hex
import play.api.mvc.{Action, InjectedController, RawBuffer}

import java.io.StringReader
import java.nio.charset.Charset
import javax.inject.Inject
import scala.annotation.tailrec
import scala.concurrent.ExecutionContext

class IngestFidelityActivity @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[RawBuffer] = Action(parse.raw) { req => {
		val bytes = req.body.asBytes().get

		// drop BOM
		var s = if (Hex.encodeHexString(bytes.take(3).toArray) == "efbbbf") {
			bytes.drop(3).decodeString(Charset.defaultCharset())
		} else {
			bytes.decodeString(Charset.defaultCharset())
		}

		// fix f'd up quotes
		s = s.replaceAll("\"Individual - TOD\" X(\\d+)", "\"Individual - TOD X$1\"")
		s = s.replaceAll("\"SIMPLE IRA\" (\\d+)", "\"SIMPLE IRA $1\"")

		val csvParser = new RFC4180ParserBuilder()
			.withSeparator(',')
			.withQuoteChar('"')
			.build
		val csvReader = new CSVReaderBuilder(new StringReader(s)).withCSVParser(csvParser).build

		val headers = nextNonEmptyLine(csvReader).get
		val rawRows = collectRows(csvReader, List.empty)
		val rows = rawRows.map(row => {
			row.zipWithIndex.map(t => headers(t._2) -> t._1.trim).toMap
		})
		rows.foreach(r => {
			println("====================================")
			headers.foreach(h => {
				println(h + ": " + r.get(h))
			})
		})

		Ok("hi")
	}}


	private def nextNonEmptyLine(csvReader: CSVReader): Option[List[String]] = {
		var line: Option[List[String]] = Some(List.empty)
		while (line.nonEmpty && !line.get.exists(_ != "")) {
			line = Option(csvReader.readNext).map(_.toList)
		}
		line
	}

	@tailrec
	private def collectRows(csvReader: CSVReader, lines: List[List[String]]): List[List[String]] = {
		nextNonEmptyLine(csvReader) match {
			case None => lines
			case Some(line) => {
				if (line.length > 1) collectRows(csvReader, lines :+ line)
				else collectRows(csvReader, lines)
			}
		}
	}
}
