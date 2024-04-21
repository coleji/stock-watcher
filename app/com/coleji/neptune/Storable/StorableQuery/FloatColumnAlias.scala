package com.coleji.neptune.Storable.StorableQuery

import com.coleji.neptune.Core.PermissionsAuthority
import com.coleji.neptune.Core.PermissionsAuthority.PERSISTENCE_SYSTEM_RELATIONAL
import com.coleji.neptune.Storable.Fields.{DatabaseField, FloatDatabaseField}
import com.coleji.neptune.Storable.{Filter, StorableClass, StorableObject}

case class FloatColumnAlias(override val table: TableAlias[_ <: StorableObject[_ <: StorableClass]], override val field: FloatDatabaseField)
extends ColumnAlias[DatabaseField[Float]](table, field) {

	def lessThanConstant(c: Float): Filter = {
		Filter(s"${table.name}.${field.persistenceFieldName} < $c", List.empty)
	}

	def inList(l: List[Float])(implicit PA: PermissionsAuthority): Filter = PA.systemParams.persistenceSystem match {
		case r: PERSISTENCE_SYSTEM_RELATIONAL => {
			if (l.isEmpty) Filter.noneMatch
			else Filter.or(groupValues(l).map(group => Filter(
				s"${table.name}.${field.persistenceFieldName} in (${group.mkString(", ")})",
				List.empty
			)))
		}
	}

	def equalsConstant(d: Float): Filter = Filter(s"${table.name}.${field.persistenceFieldName} = $d", List.empty)
}
