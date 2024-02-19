package com.coleji.neptune.Storable

import com.coleji.neptune.Core.RequestCacheObject
import com.coleji.neptune.IO.PreparedQueries.PreparedValue

trait CastableToStorableObject[T <: CastableToStorableClass] {
	val apexTableName: String
	protected val persistenceFieldsMap: Map[String, T => PreparedValue]
	val pkColumnName: String

	val allowedUserTypes: Set[RequestCacheObject[_]]

	lazy val persistenceFields: List[String] = persistenceFieldsMap.toList.map(t => t._1)

	def persistenceValues(t: T): Map[String, PreparedValue] = persistenceFieldsMap.map(tup => (tup._1, tup._2(t)))

	val getId: T => String
}
