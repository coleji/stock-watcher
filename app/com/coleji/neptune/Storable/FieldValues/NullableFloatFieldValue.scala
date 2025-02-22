package com.coleji.neptune.Storable.FieldValues

import com.coleji.neptune.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.neptune.Storable.Fields.NullableFloatDatabaseField
import com.coleji.neptune.Storable.{GetSQLLiteralPair, StorableClass}
import play.api.libs.json.{JsNull, JsNumber, JsValue}

class NullableFloatFieldValue(instance: StorableClass, @transient fieldInner: NullableFloatDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[Option[Float]](instance, fieldInner) {
	override def getPersistenceLiteral: (String, List[String]) = GetSQLLiteralPair(super.get)

	override def asJSValue: JsValue = super.get match {
		case None => JsNull
		case Some(v) => JsNumber(BigDecimal.valueOf(v))
	}

	override def updateFromJsValue(v: JsValue): Boolean = v match {
		case n: JsNumber => update(Some(n.value.floatValue))
		case JsNull => update(None)
		case _ => false
	}
}