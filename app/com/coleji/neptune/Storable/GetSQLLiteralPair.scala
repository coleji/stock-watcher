package com.coleji.neptune.Storable

import com.coleji.neptune.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PersistenceSystem}
import com.coleji.neptune.Util.DateUtil.HOME_TIME_ZONE

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime, ZonedDateTime}

object GetSQLLiteralPair {
	def apply(i: Int): (String, List[String]) = ("?", List(GetSQLLiteral(i)))
	def apply(d: Double): (String, List[String]) = ("?", List(GetSQLLiteral(d)))

	def apply(b: Boolean): (String, List[String]) =  ("?", List(GetSQLLiteralForPrepared(b)))

	def apply(ld: LocalDate)(implicit persistenceSystem: PersistenceSystem): (String, List[String]) = persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => ("?", List(ld.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
		case PERSISTENCE_SYSTEM_ORACLE => ("TO_DATE(?, 'MM/DD/YYYY')", List(ld.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))))
	}

	def apply(ldt: LocalDateTime, truncateToDay: Boolean)(implicit persistenceSystem: PersistenceSystem): (String, List[String]) = {
		if (truncateToDay) apply(ldt.toLocalDate)
		else persistenceSystem match {
			case PERSISTENCE_SYSTEM_MYSQL => ("?", List(ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
			case PERSISTENCE_SYSTEM_ORACLE => ("TO_DATE('?', 'MM/DD/YYYY HH24:MI:SS')", List(ldt.format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"))))
		}
	}

	def apply(zdt: ZonedDateTime, truncateToDay: Boolean)(implicit persistenceSystem: PersistenceSystem): (String, List[String]) = apply(zdt.withZoneSameInstant(HOME_TIME_ZONE).toLocalDateTime, truncateToDay)

	def apply(o: Option[_]): (String, List[String]) = o match{
		case None => ("?", List(null))
		case Some(i: Int) => apply(i)
		case Some(d: Double) => apply(d)
		case Some(b: Boolean) => apply(b)
		case Some(ld: LocalDate) => apply(ld)
		case Some(ldt: LocalDateTime) => apply(ldt, truncateToDay = false)
		case _ => throw new Exception("Unexpected optioned type in GetSQLLiteralPair")
	}
}
