package com.coleji.stockwatcher.endpoints

import com.coleji.neptune.Core.PermissionsAuthority
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class Ping @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action { _ => {
		Ok("pong")
	}}
}

