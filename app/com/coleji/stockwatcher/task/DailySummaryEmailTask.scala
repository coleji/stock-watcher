package com.coleji.stockwatcher.task

import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.stockwatcher.{SmtpEmailer, StockWatcherTask}
import org.slf4j.LoggerFactory

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DailySummaryEmailTask extends StockWatcherTask{
	private val logger = LoggerFactory.getLogger(this.getClass.getName)
	override def getNextRuntime: ZonedDateTime = {
		val now = ZonedDateTime.now()

		val sendDatetime = if (now.getHour < 10) {
			now.truncatedTo(ChronoUnit.DAYS).plusHours(10)
		} else {
			now.truncatedTo(ChronoUnit.DAYS).plusDays(1).plusHours(10)
		}

		if (sendDatetime.getDayOfWeek.getValue > 5) {
			// 6 is saturday, 7 is sunday; add 2 or 1 respectively
			sendDatetime.plusDays(8-sendDatetime.getDayOfWeek.getValue)
		} else {
			sendDatetime
		}
	}

	override protected def taskAction(rc: UnlockedRequestCache): Unit = {
		val now = ZonedDateTime.now()
		val subject = "Daily Finance Summary for " + now.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
		val body = "This is a test email from the finance app"
		logger.info("sending daily summary email")
		SmtpEmailer.sendEmail(subject, body)
	}
}
