package com.coleji.neptune.Storable

case class Filter(preparedSQL: String, params: List[String]) {
	override def toString: String = preparedSQL + ", " + params
}

object Filter {
	def and(fs: List[Filter]): Filter = Filter("( " + fs.map(_.preparedSQL).mkString(" AND ") + ") ", fs.flatMap(_.params))
	def or(fs: List[Filter]): Filter = Filter("( " + fs.map(_.preparedSQL).mkString(" OR ") + ") ", fs.flatMap(_.params))
	val noneMatch: Filter = Filter("1=2", List.empty)
	val allMatch: Filter = Filter("1=1", List.empty)
}