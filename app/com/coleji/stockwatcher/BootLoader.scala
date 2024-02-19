package org.sailcbi.APIServer.Server

import com.coleji.neptune.Core.Boot.ServerBootLoaderLive
import com.coleji.neptune.Util.PropertiesWrapper
import play.api.inject.ApplicationLifecycle

import javax.inject.Inject

class BootLoader @Inject()(lifecycle: ApplicationLifecycle) extends ServerBootLoaderLive {
  private val PROPERTY_NAMES = BootLoader.PROPERTY_NAMES

  val requiredProperties = List(
    BootLoader.PROPERTY_NAMES.DB_DRIVER
  )

  val paPostBoot: PropertiesWrapper => Unit = pw => {

  }

  println("GIT HASH: $$GITHUB_SHA$$")

  this.init(lifecycle, BootLoader.ENTITY_PACKAGE_PATH, List.empty,  requiredProperties, paPostBoot)
}

object BootLoader {
  val ENTITY_PACKAGE_PATH = "com.coleji.stockwatcher.entity.entitydefinitions"

  object PROPERTY_NAMES {
   val DB_DRIVER = "DBDriver"
  }
}