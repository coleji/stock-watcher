package com.coleji.neptune.Core.Emailer

import com.coleji.neptune.Core.Shell.ShellManager
import org.slf4j.LoggerFactory

class SSMTPEmailer private[Core](alwaysSendTo: Option[String]) extends Emailer {
	private val logger = LoggerFactory.getLogger(this.getClass.getName)
	private def sanitize(s: String): String = s.replace("\"", "\\\"").replace("'", "\\'")

	private[Core] def send(subject: String, body: String, to: String = "jon@community-boating.org"): Unit = {
//		val sendTo = alwaysSendTo match {
//			case Some(s) => s
//			case None => to
//		}

		val command = "ssmtp " + to + " -F API"
		val stdin = "Subject: " + subject + "\n\n" + body
		logger.info(command)
		ShellManager.execute(command, None, Some(20000), None, Some(stdin))
	}
}
