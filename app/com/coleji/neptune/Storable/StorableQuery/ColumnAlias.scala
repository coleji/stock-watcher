package com.coleji.neptune.Storable.StorableQuery

import com.coleji.neptune.Storable.Fields.DatabaseField
import com.coleji.neptune.Storable.{Filter, StorableClass, StorableObject}

abstract class ColumnAlias[U <: DatabaseField[_]](val table: TableAlias[_ <: StorableObject[_ <: StorableClass]], val field: U) {
//	def wrapFilter(f: U => String => Filter): Filter = f(field)(table.name)
	def isNull: Filter = Filter(s"${table.name}.${field.persistenceFieldName} IS NULL", List.empty)

	def isNotNull: Filter = Filter(s"${table.name}.${field.persistenceFieldName} IS NOT NULL", List.empty)

	def equalsField[U <: DatabaseField[_]](c: ColumnAlias[_ <: DatabaseField[_]]): Filter = Filter(s"${table.name}.${field.persistenceFieldName} = ${c.table.name}.${c.field.persistenceFieldName}", List.empty)
	override def toString: String = s"${table}.${field.persistenceFieldName}"

	protected def groupValues[T](values: List[T]): List[List[T]] = {
		val MAX_IDS = 900
		if (values.length <= MAX_IDS) List(values)
		else {
			val splitList = values.splitAt(MAX_IDS)
			splitList._1 :: groupValues(splitList._2)
		}
	}
}

//case class ColumnAliasInnerJoined[U <: DatabaseField[_]](override val table: TableAliasInnerJoined[_ <: StorableObject[_ <: StorableClass]], override val field: U) extends ColumnAlias[U](table, field)
//case class ColumnAliasOuterJoined[U <: DatabaseField[_]](override val table: TableAliasOuterJoined[_ <: StorableObject[_ <: StorableClass]], override val field: U) extends ColumnAlias[U](table, field)
//
object ColumnAlias {
	// Only use when you don't care about recovering e.g. IntDatabaseField from this field
//	def wrapForInnerJoin[U <: DatabaseField[_]](field: U): ColumnAlias[U] = field.alias(TableAlias.wrapForInnerJoin(field.entity)).asInstanceOf[ColumnAlias[U]]
//	def wrapForOuterJoin[U <: DatabaseField[_]](field: U): ColumnAlias[U] = field.alias(TableAlias.wrapForOuterJoin(field.entity)).asInstanceOf[ColumnAlias[U]]
	implicit def wrap[U <: DatabaseField[_]](field: U): ColumnAlias[U] = field.abstractAlias.asInstanceOf[ColumnAlias[U]]
}
