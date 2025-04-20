package com.coleji.stockwatcher.endpoints.IngestFidelityActivity

import com.coleji.neptune.Core.PermissionsAuthority
import com.coleji.neptune.Util.StringUtil
import com.coleji.stockwatcher.SmtpEmailer
import com.coleji.stockwatcher.entity.entitydefinitions.PolygonSplit
import com.opencsv.{CSVReader, CSVReaderBuilder, RFC4180ParserBuilder}
import org.apache.hc.client5.http.utils.Hex
import org.slf4j.LoggerFactory
import play.api.mvc.{Action, InjectedController, RawBuffer}

import java.io.{FileInputStream, StringReader}
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import scala.annotation.tailrec
import scala.collection.mutable
import scala.concurrent.ExecutionContext

class IngestFidelityActivityController @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	private val logger = LoggerFactory.getLogger(this.getClass.getName)
	def post()(implicit PA: PermissionsAuthority): Action[RawBuffer] = Action(parse.raw) { req => {
		val rc = PA.rootRC // use a real one
		logger.info("========================================== starting")
		val file = req.body.asFile
		val bytes = (new FileInputStream(file)).readAllBytes().toList

		val excludedSymbols = Set(
			"SPAXX",
			"FMILX",
			"FBGRX",
			"BGSAX",
			"PSGAX",
			"FAGIX",
			"CPOAX"
		)

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
//			.withQuoteChar('"')
			.build
		val csvReader = new CSVReaderBuilder(new StringReader(s)).withCSVParser(csvParser).build

		val headers = nextNonEmptyLine(csvReader).get
		val rawRows = collectRows(csvReader, List.empty)

		logger.info("rows: "+rawRows.size)
		logger.info(headers.toString())
		logger.info(headers.size+"")
		logger.info(csvReader.getLinesRead+"")
		val rows = rawRows.map(row => {
			val map = row.zipWithIndex.filter(t => t._2 < 18).map(t => headers(t._2) -> t._1.trim).toMap

//			logger.info(map("Symbol") + " -> " + StringUtil.optionNoEmptyString(map("Symbol").trim()))

			FidelityActivityDto(
				amountExcluded = StringUtil.optionNoEmptyString(map("Amt Excluded")).flatMap(_.toIntOption),
				runDate = LocalDate.parse(map("Run Date").trim(), DateTimeFormatter.ofPattern("MM/dd/yyyy")),
//				account = map("Account"),
				action = map("Action").trim(),
				symbol = StringUtil.optionNoEmptyString(map("Symbol").trim()),
				description = map("Description").trim(),
				`type` = StringUtil.optionNoEmptyString(map("Type")),
				exchangeQuantity = StringUtil.optionNoEmptyString(map("Exchange Quantity")).map(_.toInt),
				exchangeCurrency = StringUtil.optionNoEmptyString(map("Exchange Currency")),
				quantity = StringUtil.optionNoEmptyString(map("Quantity")).flatMap(_.toDoubleOption),
				effectiveQuantity = StringUtil.optionNoEmptyString(map("Quantity")).flatMap(_.toDoubleOption),
				currency = StringUtil.optionNoEmptyString(map("Currency")),
				price = StringUtil.optionNoEmptyString(map("Price")).flatMap(_.toDoubleOption),
				exchangeRate = StringUtil.optionNoEmptyString(map("Exchange Rate")).flatMap(_.toDoubleOption),
				commission = StringUtil.optionNoEmptyString(map("Commission")).flatMap(_.toDoubleOption),
				fees = StringUtil.optionNoEmptyString(map("Fees")).flatMap(_.toDoubleOption),
				accruedInterest = StringUtil.optionNoEmptyString(map("Accrued Interest")).flatMap(_.toDoubleOption),
				amount = map("Amount").toDouble,
				settlementDate = StringUtil.optionNoEmptyString(map("Settlement Date"))
					.map(d => LocalDate.parse(d, DateTimeFormatter.ofPattern("MM/dd/yyyy"))),
				splitTo = 1f,
				splitFrom = 1f
			)
		})

		val buys: mutable.Map[String, List[FidelityActivityDto]] = mutable.Map.empty
		val sells: mutable.Map[String, List[FidelityActivityDto]] = mutable.Map.empty
		val tickers: mutable.Set[String] = mutable.Set.empty

		val splits = PA.rootRC.getAllObjectsOfClass(PolygonSplit, Set(
			PolygonSplit.fields.ticker,
			PolygonSplit.fields.splitFrom,
			PolygonSplit.fields.splitTo,
			PolygonSplit.fields.executionDate,
		)).foldLeft(mutable.Map[String, List[PolygonSplit]]())((m: mutable.Map[String, List[PolygonSplit]], s: PolygonSplit) => {
			val ticker = s.values.ticker.get
			if (!m.contains(ticker)) m(ticker) = List.empty
			m(ticker) = s :: m(ticker)
			m
		})

		rows.reverse/*.filter(_.account.startsWith("Individual"))*/
		.filter(_.symbol.isDefined)
		.filter(r => !excludedSymbols.contains(r.symbol.getOrElse("")))
		.filter(r => r.symbol.get.matches("^\\D.*"))
		.foreach(r => {
			val sym = r.symbol.get
			tickers.add(sym)

			val matchingSplits = splits.getOrElse(r.symbol.get, List.empty).filter(_.values.executionDate.get.isAfter(r.runDate))
			if (matchingSplits.nonEmpty) logger.info("****************** " + r.symbol.get + " " + matchingSplits.toString())
			val splitTo = matchingSplits.foldLeft(r.splitTo)((acc, s) => acc * s.values.splitTo.get)
			val splitFrom = matchingSplits.foldLeft(r.splitFrom)((acc, s) => acc * s.values.splitFrom.get)
			r.effectiveQuantity = r.quantity.map(q => (q * splitTo) / splitFrom)

			if (
				r.action.startsWith("YOU BOUGHT") &&
				r.symbol.nonEmpty
			) {
				val thisBuys = buys.getOrElse(sym, List.empty)
				buys(sym) = r :: thisBuys
				val storable = r.toStorable()
				rc.commitObjectToDatabase(storable)
			} else if (
				r.action.startsWith("YOU SOLD") &&
				r.symbol.nonEmpty
			) {
				val thisSells = sells.getOrElse(sym, List.empty)
				sells(sym) = r :: thisSells
				val storable = r.toStorable()
				rc.commitObjectToDatabase(storable)
			} //else logger.debug(r.action)
		})

		tickers.toList.sorted.foreach(sym => {
//			logger.info(sym)
			val thisBuys = buys.getOrElse(sym, List.empty)
			val thisSells = sells.getOrElse(sym, List.empty)
//			thisBuys.foreach(dto => {
//				if (dto.amountExcluded.getOrElse(0) > 0) logger.info("Excluded buy (typo)" + dto.toString)
//				if (dto.symbol.getOrElse("") == "EG") logger.info("Buy EG " + dto.runDate + " " + dto.quantity.getOrElse(0d) + " " + dto.effectiveQuantity.getOrElse(0d))
//			})
//			thisSells.foreach(dto => {
//				val excluded = dto.amountExcluded.getOrElse(0).toDouble
//				if (excluded > -1*dto.quantity.getOrElse(0d)) logger.info("excluded>sold " + dto.toString)
//				if (dto.symbol.getOrElse("") == "EG") logger.info("Sell EG " + dto.runDate + " " + ((-1 * dto.quantity.getOrElse(0d)) - dto.amountExcluded.getOrElse(0).toDouble)+ " " + ((-1 * dto.effectiveQuantity.getOrElse(0d)) - dto.amountExcluded.getOrElse(0).toDouble))
//			})
			val buyCount = thisBuys.foldLeft(0d)((agg, dto) => agg + dto.effectiveQuantity.getOrElse(0d))
			val sellCount = thisSells.foldLeft(0d)((agg, dto) => agg + (-1 * dto.effectiveQuantity.getOrElse(0d)) - dto.amountExcluded.getOrElse(0).toDouble)
			if (sellCount != buyCount) logger.info(sym + ": buys-sells is " + (buyCount-sellCount))
		})

//		SmtpEmailer.sendEmail("Test email", "Test email body")

		Ok("hi")
	}}


	private def nextNonEmptyLine(csvReader: CSVReader): Option[List[String]] = {
		var line: Option[List[String]] = Some(List.empty)
		while (line.nonEmpty && !line.get.exists(_ != "")) {
			line = Option(csvReader.readNext()).map(_.toList)
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
