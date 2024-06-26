package com.coleji.neptune.Core

import com.coleji.neptune.Core.Boot.ServerBootLoader
import com.coleji.neptune.Util.PropertiesWrapper
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.slf4j.LoggerFactory

object MysqlDatabaseConnection {
	private val logger = LoggerFactory.getLogger(this.getClass.getName)
	private[Core] def apply(confFileLocation: String): DatabaseGateway = {
		val pw = new PropertiesWrapper(confFileLocation, List("host", "port", "username", "password", "schema"))

		Class.forName("com.mysql.cj.jdbc.Driver")

		val mainSchemaName = pw.getString("schema")
		val tempSchemaName = pw.getOptionalString("temptableschema").getOrElse(mainSchemaName)
		val host = pw.getString("host")
		val port = pw.getString("port")
		val username = pw.getString("username")
		val password = pw.getString("password")
		val tempUsername = pw.getOptionalString("temptableusername").getOrElse(username)
		val tempPassword = pw.getOptionalString("temptablepassword").getOrElse(password)
		val poolSize = pw.getOptionalString("maxPoolSize").map(_.toInt).getOrElse(2)
		val poolSizeTemp = pw.getOptionalString("maxPoolSizeTemp").map(_.toInt).getOrElse(1)

		val mainConfig = getDataSourceConfig(host, port, mainSchemaName, username, password, poolSize)
		val tempConfig = getDataSourceConfig(host, port, tempSchemaName, tempUsername, tempPassword, poolSizeTemp)

		new DatabaseGateway(
			driverName = ServerBootLoader.DB_DRIVER_MYSQL,
			mainPool = new ConnectionPoolWrapper(new HikariDataSource(mainConfig)),
			tempPool = new ConnectionPoolWrapper(new HikariDataSource(tempConfig)),
			mainSchemaName = mainSchemaName,
			tempSchemaName = tempSchemaName,
			mainUserName = username
		)
	}

	private def getDataSourceConfig(
		host: String, port: String, schema: String,
		username: String, password: String, poolSize: Int
	): HikariConfig = {
		val config = new HikariConfig()
		logger.debug("username: " + username + " max pool size: " + poolSize)

		config.setJdbcUrl(s"jdbc:mysql://$host:$port/$schema")
		config.setUsername(username)
		config.setPassword(password)

		// other options
		config.setMaximumPoolSize(poolSize)
		config.setLeakDetectionThreshold(30 * 1000)

		config
	}
}
