package com.coleji.stockwatcher.endpoints.IngestFidelityActivity

import com.coleji.neptune.Core.PermissionsAuthority
import com.coleji.neptune.Util.StringUtil
import com.opencsv.{CSVReader, CSVReaderBuilder, RFC4180ParserBuilder}
import org.apache.hc.client5.http.utils.Hex
import play.api.mvc.{Action, InjectedController, RawBuffer}

import java.io.{FileInputStream, StringReader}
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import scala.annotation.tailrec
import scala.collection.mutable
import scala.concurrent.ExecutionContext

class IngestFidelityActivityController @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[RawBuffer] = Action(parse.raw) { req => {
		val file = req.body.asFile
		val bytes = (new FileInputStream(file)).readAllBytes().toList

		// drop BOM
		var s = if (Hex.encodeHexString(bytes.take(3).toArray) == "efbbbf") {
			new String(bytes.drop(3).toArray, "UTF-8")
		} else {
			new String(bytes.toArray, "UTF-8")
		}

		// fix f'd up quotes
		s = s.replaceAll("\"Individual - TOD\" X(\\d+)", "\"Individual - TOD X$1\"")
		s = s.replaceAll("\"SIMPLE IRA\" (\\d+)", "\"SIMPLE IRA $1\"")
		s = s.replaceAll("\"ACCUSERVE SOLUTIONS\" (\\d+)F", "\"ACCUSERVE SOLUTIONS $1F\"")

		val csvParser = new RFC4180ParserBuilder()
			.withSeparator(',')
			.withQuoteChar('"')
			.build
		val csvReader = new CSVReaderBuilder(new StringReader(s)).withCSVParser(csvParser).build

		val headers = nextNonEmptyLine(csvReader).get
		val rawRows = collectRows(csvReader, List.empty)
		val rows = rawRows.map(row => {
//			println(row)
			val map = row.zipWithIndex.filter(t => t._2 < 17).map(t => headers(t._2) -> t._1.trim).toMap
//			map
//			println(map)
			FidelityActivityDto(
				runDate = LocalDate.parse(map("Run Date"), DateTimeFormatter.ofPattern("MMM-dd-yyyy")),
				account = map("Account"),
				action = map("Action"),
				symbol = StringUtil.optionNoEmptyString(map("Symbol")),
				description = map("Description"),
				`type` = StringUtil.optionNoEmptyString(map("Type")),
				exchangeQuantity = StringUtil.optionNoEmptyString(map("Exchange Quantity")).map(_.toInt),
				exchangeCurrency = StringUtil.optionNoEmptyString(map("Exchange Currency")),
				quantity = StringUtil.optionNoEmptyString(map("Quantity")).flatMap(_.toDoubleOption),
				currency = StringUtil.optionNoEmptyString(map("Currency")),
				price = StringUtil.optionNoEmptyString(map("Price")).flatMap(_.toDoubleOption),
				exchangeRate = StringUtil.optionNoEmptyString(map("Exchange Rate")).flatMap(_.toDoubleOption),
				commission = StringUtil.optionNoEmptyString(map("Commission")).flatMap(_.toDoubleOption),
				fees = StringUtil.optionNoEmptyString(map("Fees")).flatMap(_.toDoubleOption),
				accruedInterest = StringUtil.optionNoEmptyString(map("Accrued Interest")).flatMap(_.toDoubleOption),
				amount = map("Amount").toDouble,
				settlementDate = StringUtil.optionNoEmptyString(map("Settlement Date"))
					.map(d => LocalDate.parse(d, DateTimeFormatter.ofPattern("MMM-dd-yyyy")))
			)
		})

		var owned: mutable.Map[String, Int] = mutable.Map.empty


		rows.reverse.filter(_.account.startsWith("Individual")).foreach(r => {
			if (r.symbol.isDefined && r.symbol.get == "VTI") println(r)
			if (r.action.startsWith("YOU BOUGHT") && r.symbol.nonEmpty) {
//				println("buy")
				val sym = r.symbol.get
				val existing = owned.getOrElse(sym, 0)
				owned(sym) = existing + r.quantity.get.toInt
			} else if (r.action.startsWith("YOU SOLD") && r.symbol.nonEmpty) {
//				println("sell")
				val sym = r.symbol.get
				val existing = owned.getOrElse(sym, 0)
				val newTotal = existing + r.quantity.get.toInt
				if (newTotal < 0) println(s"sold ${r.quantity} of ${r.symbol} new total is ${newTotal}")
				else owned(sym) = newTotal

			} //else println(r.action)
			println(owned.get("VTI"))
		})


		println(owned.filter(t => t._2 > 0))

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
