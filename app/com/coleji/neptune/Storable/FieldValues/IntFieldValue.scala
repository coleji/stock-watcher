package com.coleji.neptune.Storable.FieldValues

import com.coleji.neptune.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.neptune.Storable.Fields.IntDatabaseField
import com.coleji.neptune.Storable.{GetSQLLiteralPair, StorableClass}
import play.api.libs.json.{JsNull, JsNumber, JsValue}

class IntFieldValue(instance: StorableClass, @transient fieldInner: IntDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[Int](instance, fieldInner) {
	override def getPersistenceLiteral: (String, List[String]) = GetSQLLiteralPair(super.get)

	override def asJSValue: JsValue = JsNumber(super.get)

	override def updateFromJsValue(v: JsValue): Boolean = v match {
		case n: JsNumber => update(n.value.toIntExact)
		case JsNull => throw new Exception("JsNull provided to nonnull field " + field.getRuntimeFieldName)
		case _ => false
	}
}
