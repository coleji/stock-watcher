package com.coleji.neptune.IO.HTTP

import play.api.libs.json.{JsValue, Json}
import scalaj.http.Http

import scala.concurrent.{ExecutionContext, Future}

class FromScalajHTTP(implicit exec: ExecutionContext) extends HTTPMechanism {
	def getString(
						 url: String,
						 method: HTTPMethod,
						 body: Option[Map[String, String]],
						 basicAuthUsername: Option[String],
						 basicAuthPassword: Option[String]
				 ): Future[String] = Future {
		val req = basicAuthUsername match {
			case None => Http(url)
			case Some(username) => {
				val password = basicAuthPassword match {
					case Some(s) => s;
					case None => ""
				}
				Http(url).auth(username, password)
			}
		}
		method match {
			case GET => req.asString.body
			case POST => req.postForm(body.get.toSeq).asString.body
		}
	}

	def getJSON(
					   url: String,
					   method: HTTPMethod,
					   body: Option[Map[String, String]],
					   basicAuthUsername: Option[String],
					   basicAuthPassword: Option[String]
			   ): Future[JsValue] = getString(url, method, body, basicAuthUsername, basicAuthPassword).map(s => Json.parse(s))
}