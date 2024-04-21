package com.coleji.neptune.Storable.Fields

import com.coleji.neptune.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PersistenceSystem}
import com.coleji.neptune.Storable.StorableQuery.{ColumnAlias, NullableFloatColumnAlias, TableAlias}
import com.coleji.neptune.Storable.{ProtoStorable, StorableClass, StorableObject}

class NullableFloatDatabaseField(override val entity: StorableObject[_ <: StorableClass], override val persistenceFieldName: String) extends DatabaseField[Option[Float]](entity, persistenceFieldName) {
	def findValueInProtoStorable(row: ProtoStorable, key: ColumnAlias[_]): Option[Option[Float]] = row.floatFields.get(key)

	def isNullable: Boolean = true

	def getFieldType(implicit persistenceSystem: PersistenceSystem): String = persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => "decimal"
		case PERSISTENCE_SYSTEM_ORACLE => "number"
	}

	def getValueFromString(s: String): Option[Option[Float]] = {
		if (s == "") Some(None)
		else {
			try {
				val d = s.toFloat
				Some(Some(d))
			} catch {
				case _: Throwable => None
			}
		}
	}

	def alias(tableAlias: TableAlias[_ <: StorableObject[_ <: StorableClass]]): NullableFloatColumnAlias =
		NullableFloatColumnAlias(tableAlias, this)

	def alias: NullableFloatColumnAlias =
		NullableFloatColumnAlias(entity.alias, this)
}
