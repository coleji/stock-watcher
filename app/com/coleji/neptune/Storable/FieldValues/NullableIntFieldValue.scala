package com.coleji.neptune.Storable.FieldValues

import com.coleji.neptune.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.neptune.Storable.Fields.NullableIntDatabaseField
import com.coleji.neptune.Storable.{GetSQLLiteralPair, StorableClass}
import play.api.libs.json.{JsNull, JsNumber, JsValue}

class NullableIntFieldValue(instance: StorableClass, @transient fieldInner: NullableIntDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[Option[Int]](instance, fieldInner) {
	override def getPersistenceLiteral: (String, List[String]) = GetSQLLiteralPair(super.get)

	override def asJSValue: JsValue = super.get match {
		case None => JsNull
		case Some(v) => JsNumber(v)
	}

	override def updateFromJsValue(v: JsValue): Boolean = v match {
		case n: JsNumber => update(Some(n.value.toIntExact))
		case JsNull => update(None)
		case _ => false
	}
}