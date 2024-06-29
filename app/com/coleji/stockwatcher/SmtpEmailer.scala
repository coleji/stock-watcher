package com.coleji.stockwatcher

import com.coleji.neptune.Core.PermissionsAuthority
import com.coleji.neptune.Util.Initializable
import jakarta.mail.internet.{InternetAddress, MimeBodyPart, MimeMessage, MimeMultipart}
import jakarta.mail.{Address, Authenticator, Message, Multipart, PasswordAuthentication, Session, Transport}

import java.util.Properties

object SmtpEmailer {
	val prop = new Properties
	prop.setProperty("mail.smtp.auth", "true")
	prop.setProperty("mail.smtp.starttls.enable", "true")
	prop.setProperty("mail.smtp.host", "smtp.sendgrid.net")
	prop.setProperty("mail.smtp.port", "587")
	prop.setProperty("mail.smtp.ssl.trust", "smtp.sendgrid.net")

	val session = new Initializable[Session]

	def sendEmail(subject: String, body: String)(implicit PA: PermissionsAuthority): Unit = {
		session.trySet(() => Session.getInstance(prop, new Authenticator {
			override def getPasswordAuthentication: PasswordAuthentication =
				new PasswordAuthentication("apikey", PA.customParams.getString("sendgrid-api-key"))
		}))

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

		println("sending email...")
		Transport.send(message)
		println("sent!")
	}
}
