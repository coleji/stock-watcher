package com.coleji.stockwatcher

import com.coleji.neptune.Core.Boot.ServerBootLoaderLive
import com.coleji.neptune.Util.PropertiesWrapper
import org.slf4j.LoggerFactory
import play.api.inject.ApplicationLifecycle

import javax.inject.Inject

class BootLoader @Inject()(lifecycle: ApplicationLifecycle, taskDispatcher: TaskDispatcher) extends ServerBootLoaderLive {
	private val logger = LoggerFactory.getLogger(this.getClass.getName)
	private val PROPERTY_NAMES = BootLoader.PROPERTY_NAMES

	val requiredProperties = List(
		BootLoader.PROPERTY_NAMES.DB_DRIVER
	)

	val paPostBoot: PropertiesWrapper => Unit = pw => {

	}

	logger.info("GIT HASH: $$GITHUB_SHA$$")

	val PA = this.init(lifecycle, BootLoader.ENTITY_PACKAGE_PATH, List.empty,  requiredProperties, paPostBoot)
	val taskThread = new Thread(() => {
		taskDispatcher.start()
	})
	taskThread.setName("Task Runner")
	taskThread.start()
}

object BootLoader {
	val ENTITY_PACKAGE_PATH = "com.coleji.stockwatcher.entity.entitydefinitions"

	object PROPERTY_NAMES {
		val DB_DRIVER = "DBDriver"
	}
}
