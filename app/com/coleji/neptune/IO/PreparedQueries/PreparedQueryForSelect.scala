package com.coleji.neptune.IO.PreparedQueries

import com.coleji.neptune.Core.RequestCacheObject


abstract class PreparedQueryForSelect[T](
	override val allowedUserTypes: Set[RequestCacheObject[_]],
	override val useTempSchema: Boolean = false
) extends HardcodedQueryForSelect[T](allowedUserTypes, useTempSchema) {
	val params: List[String] = List.empty
	val preparedParams: List[PreparedValue] = List.empty

	def getParams: List[PreparedValue] = {
		if (params.nonEmpty && preparedParams.isEmpty) {
			// legacy mode, use the old string params
			params.map(PreparedString)
		} else {
			preparedParams
		}
	}
}
