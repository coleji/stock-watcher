package com.coleji.neptune.Storable.FieldValues

import com.coleji.neptune.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.neptune.Storable.Fields.NullableDateTimeDatabaseField
import com.coleji.neptune.Storable.{GetSQLLiteralPair, StorableClass}
import play.api.libs.json.{JsNull, JsString, JsValue}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NullableDateTimeFieldValue(instance: StorableClass, @transient fieldInner: NullableDateTimeDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[Option[LocalDateTime]](instance, fieldInner) {
	override def getPersistenceLiteral: (String, List[String]) = GetSQLLiteralPair(super.get)

	override def asJSValue: JsValue = super.get match {
		case None => JsNull
		case Some(v) => JsString(v.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
	}

	override def updateFromJsValue(v: JsValue): Boolean = v match {
		case s: JsString => update(Some(LocalDateTime.parse(s.value)))
		case JsNull => update(None)
		case _ => false
	}
}