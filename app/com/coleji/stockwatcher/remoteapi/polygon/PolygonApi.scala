package com.coleji.stockwatcher.remoteapi.polygon

import com.coleji.neptune.Core.PermissionsAuthority
import org.apache.hc.core5.net.URIBuilder
import play.api.libs.json.{JsValue, Json}

import java.net.http.{HttpClient, HttpRequest, HttpResponse}

object PolygonApi {
	val host: String = "https://api.polygon.io"
	private def getApiKey(implicit PA: PermissionsAuthority): String = PA.customParams.getString("polygon-api-key")


	def callApi(path: String): JsValue = {
		val builder = HttpRequest.newBuilder
			.uri(new URIBuilder(host + path)
				.addParameter("apiKey", getApiKey)
				.build
			)
		val req = builder.build

		Thread.sleep(100)

		val res = HttpClient.newBuilder
			.build
			.send(req, HttpResponse.BodyHandlers.ofString())

		Json.parse(res.body())
	}
}
