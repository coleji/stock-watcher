package com.coleji.neptune.Core

import io.sentry.Sentry
import play.api.libs.Files
import play.api.libs.json.JsValue
import play.api.mvc._

case class ParsedRequest(
	headers: Headers,
	cookies: Cookies,
	path: String,
	method: String,
	remoteAddress: String,
	postParams: Map[String, String],
	body: AnyContent
) {
	def addHeader(h: (String, String)): ParsedRequest = ParsedRequest(
		headers.add(h),
		cookies,
		path,
		method,
		remoteAddress,
		postParams,
		body
	)

	lazy val postJSON: Option[JsValue] = body.asJson
	lazy val bodyMultipartFormData: Option[MultipartFormData[Files.TemporaryFile]] = body.asMultipartFormData
}

object ParsedRequest {
	object methods {
		val GET = "GET"
	}

	def apply(request: Request[AnyContent])(implicit PA: PermissionsAuthority): ParsedRequest = try {
		ParsedRequest(
			headers = request.headers,
			cookies = request.cookies,
			path = request.path,
			method = request.method,
			remoteAddress = request.remoteAddress,
			postParams = getPostParams(request),
			body = request.body
		)
	} catch {
		case e: Throwable => {
			PA.logger.error("Failure to parse request", e)
			Sentry.captureException(e)
			throw e
	}}

	private def getPostParams(request: Request[AnyContent]): Map[String, String] = {
		request.body.asFormUrlEncoded match {
			case None => Map.empty[String, String]
			case Some(v) =>
				v.map(Function.tupled((s: String, ss: Seq[String]) => (s, ss.mkString(""))))
		}
	}
}