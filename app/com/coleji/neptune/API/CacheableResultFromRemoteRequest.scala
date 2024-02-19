package com.coleji.neptune.API

import com.coleji.neptune.Core._
import play.api.libs.json.{JsObject, JsValue}
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.mvc.{Action, AnyContent, InjectedController}

import scala.concurrent.Future

trait CacheableResultFromRemoteRequest[T <: ParamsObject, U] extends CacheableResult[T, U] with InjectedController {
	private def getFuture(cb: CacheBroker, rc: RequestCache, params: T, ws: WSClient, url: String): Future[String] = {
		val calculateValue: (() => Future[JsObject]) = () => {
			val request: WSRequest = ws.url(url)
			println("*** Making remote web request!")
			val futureResponse: Future[WSResponse] = request.get()
			val jsonFuture: Future[JsObject] = futureResponse.map(_.json match {
				case json: JsObject => json
				case v: JsValue => new JsObject(Map("data" -> v))
				case _ => throw new Exception("Received unparsable json response")
			})
			jsonFuture
		}
		getFuture(cb, rc, params, calculateValue)
	}

	protected def evaluate[T_User <: RequestCache](ut: RequestCacheObject[T_User], params: T, ws: WSClient, url: String)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request => {
		PA.withRequestCache[T_User](ut)(None, ParsedRequest(request), rc => {
			val cb: CacheBroker = rc.cb
			getFuture(cb, rc, params, ws, url).map(s => {
				Ok(s).as("application/json")
			})
		})
	}
	}
}
