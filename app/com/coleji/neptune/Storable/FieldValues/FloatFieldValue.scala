package com.coleji.neptune.Storable.FieldValues

import com.coleji.neptune.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.neptune.Storable.Fields.FloatDatabaseField
import com.coleji.neptune.Storable.{GetSQLLiteralPair, StorableClass}
import play.api.libs.json.{JsNull, JsNumber, JsValue}

class FloatFieldValue(instance: StorableClass, @transient fieldInner: FloatDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[Float](instance, fieldInner) {
	override def getPersistenceLiteral: (String, List[String]) = GetSQLLiteralPair(super.get)

	override def asJSValue: JsValue = JsNumber(BigDecimal.valueOf(super.get))

	override def updateFromJsValue(v: JsValue): Boolean = v match {
		case n: JsNumber => update(n.value.floatValue)
		case JsNull => throw new Exception("JsNull provided to nonnull field " + field.getRuntimeFieldName)
		case _ => false
	}
}
