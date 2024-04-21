package com.coleji.neptune.Storable.Fields

import com.coleji.neptune.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PersistenceSystem}
import com.coleji.neptune.Storable.StorableQuery.{ColumnAlias, FloatColumnAlias, TableAlias}
import com.coleji.neptune.Storable.{ProtoStorable, StorableClass, StorableObject}

class FloatDatabaseField(override val entity: StorableObject[_ <: StorableClass], override val persistenceFieldName: String) extends DatabaseField[Float](entity, persistenceFieldName) {
	def getFieldType(implicit persistenceSystem: PersistenceSystem): String = persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => "float"
		case PERSISTENCE_SYSTEM_ORACLE => "number"
	}

	def isNullable: Boolean = false

	def findValueInProtoStorable(row: ProtoStorable, key: ColumnAlias[_]): Option[Float] = {
		row.floatFields.get(key) match {
			case Some(Some(x)) => Some(x)
			case Some(None) => throw new NonNullFieldWasNullException("non-null Float field " + entity.entityName + "." + this.getRuntimeFieldName + " was null in a proto")
			case _ => None
		}
	}

	def getValueFromString(s: String): Option[Float] = {
		try {
			val d = s.toFloat
			Some(d)
		} catch {
			case _: Throwable => None
		}
	}

	def alias(tableAlias: TableAlias[_ <: StorableObject[_ <: StorableClass]]): FloatColumnAlias =
		FloatColumnAlias(tableAlias, this)

	def alias: FloatColumnAlias =
		FloatColumnAlias(entity.alias, this)
}
