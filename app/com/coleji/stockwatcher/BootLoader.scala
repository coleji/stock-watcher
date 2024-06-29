package com.coleji.stockwatcher

import com.coleji.neptune.Core.Boot.ServerBootLoaderLive
import com.coleji.neptune.Util.PropertiesWrapper
import play.api.inject.ApplicationLifecycle

import javax.inject.Inject

class BootLoader @Inject()(lifecycle: ApplicationLifecycle, taskDispatcher: TaskDispatcher) extends ServerBootLoaderLive {
	private val PROPERTY_NAMES = BootLoader.PROPERTY_NAMES

	val requiredProperties = List(
		BootLoader.PROPERTY_NAMES.DB_DRIVER
	)

	val paPostBoot: PropertiesWrapper => Unit = pw => {

	}

	println("GIT HASH: $$GITHUB_SHA$$")

	val PA = this.init(lifecycle, BootLoader.ENTITY_PACKAGE_PATH, List.empty,  requiredProperties, paPostBoot)
	println("about to start task thread")
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
