package com.coleji.neptune.Core.Boot

import com.coleji.neptune.Core.{PermissionsAuthority, RequestCacheObject}
import com.coleji.neptune.Util.PropertiesWrapper
import org.slf4j.LoggerFactory
import play.api.inject.ApplicationLifecycle

import java.nio.charset.Charset

class ServerBootLoaderLive {
	private val logger = LoggerFactory.getLogger(this.getClass.getName)
	protected def init(
		lifecycle: ApplicationLifecycle,
		entityPackagePath: String,
		definedAuthMechanisms: List[(RequestCacheObject[_], String, PropertiesWrapper => Boolean)],
		requiredProperties: List[String],
		paPostBoot: PropertiesWrapper => Unit
	): PermissionsAuthority = {
		val pa = ServerBootLoader.load(Some(lifecycle), isTestMode = false, readOnlyDatabase = false, entityPackagePath, definedAuthMechanisms, requiredProperties, paPostBoot)
		logger.info("Live loader::::: setting PA!")
		logger.info("Default charset: " + Charset.defaultCharset().displayName())
		PermissionsAuthority.setPA(pa)
		logger.info("Starting entity boot")
		pa.instantiateAllEntityCompanions(entityPackagePath)
		logger.info("finished entity boot")
		pa.bootChecks()
		pa
	}
}
