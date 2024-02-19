package com.coleji.neptune.Storable.FieldValues

import com.coleji.neptune.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.neptune.Storable.Fields.BooleanDatabaseField
import com.coleji.neptune.Storable.{GetSQLLiteral, StorableClass}
import play.api.libs.json.{JsBoolean, JsNull, JsValue}

class BooleanFieldValue(instance: StorableClass, @transient fieldInner: BooleanDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[Boolean](instance, fieldInner) {
	override def getPersistenceLiteral: (String, List[String]) = (GetSQLLiteral(super.get), List.empty)

	override def asJSValue: JsValue = JsBoolean(super.get)

	override def updateFromJsValue(v: JsValue): Boolean = v match {
		case b: JsBoolean => update(b.value)
		case JsNull => throw new Exception("JsNull provided to nonnull field " + field.getRuntimeFieldName)
		case _ => false
	}
}
