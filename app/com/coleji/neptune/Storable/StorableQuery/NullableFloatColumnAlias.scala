package com.coleji.neptune.Storable.StorableQuery

import com.coleji.neptune.Storable.Fields.{DatabaseField, NullableFloatDatabaseField}
import com.coleji.neptune.Storable.{Filter, StorableClass, StorableObject}

case class NullableFloatColumnAlias(override val table: TableAlias[_ <: StorableObject[_ <: StorableClass]], override val field: NullableFloatDatabaseField)
extends ColumnAlias[DatabaseField[Option[Float]]](table, field) {
	def lessThanConstant(c: Float): Filter = {
		Filter(s"${table.name}.${field.persistenceFieldName} < $c", List.empty)
	}

	def inList(l: List[Float]): Filter = {
		if (l.isEmpty) Filter.noneMatch
		else Filter.or(groupValues(l).map(group => Filter(
			s"${table.name}.${field.persistenceFieldName} in (${group.mkString(", ")})",
			List.empty
		)))
	}

	def equalsConstant(i: Option[Float]): Filter = i match {
		case Some(x: Float) => Filter(s"${table.name}.${field.persistenceFieldName} = $i", List.empty)
		case None => Filter(s"${table.name}.${field.persistenceFieldName} IS NULL", List.empty)
	}
}
