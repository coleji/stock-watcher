package com.coleji.stockwatcher

import com.coleji.neptune.Core.PermissionsAuthority
import com.coleji.neptune.Util.Initializable
import jakarta.mail.internet.{InternetAddress, MimeBodyPart, MimeMessage, MimeMultipart}
import jakarta.mail._
import org.slf4j.LoggerFactory

import java.util.Properties

object SmtpEmailer {
	private val logger = LoggerFactory.getLogger(this.getClass.getName)
	val prop = new Properties
	prop.setProperty("mail.smtp.auth", "false")
	prop.setProperty("mail.smtp.starttls.enable", "false")
	prop.setProperty("mail.smtp.port", "25")

	val session = new Initializable[Session]

	def sendEmail(subject: String, body: String)(implicit PA: PermissionsAuthority): Unit = {
		prop.setProperty("mail.smtp.host", PA.customParams.getString("smtp-host"))
		session.trySet(() => Session.getInstance(prop))

		val message: Message = new MimeMessage(session.get)
		message.setFrom(new InternetAddress(PA.customParams.getString("sengrid-from")))
		message.setRecipients(
			Message.RecipientType.TO, InternetAddress.parse(PA.customParams.getString("sendgrid-to")).asInstanceOf[Array[Address]]
		)
		message.setSubject(subject)

		val mimeBodyPart = new MimeBodyPart()
		mimeBodyPart.setContent(body, "text/html; charset=utf-8")

		val multipart: Multipart = new MimeMultipart()
		multipart.addBodyPart(mimeBodyPart)

		message.setContent(multipart)

		logger.debug("sending email...")
		Transport.send(message)
		logger.debug("sent!")
	}
}
