package com.coleji.neptune.Storable.StorableQuery

import com.coleji.neptune.Core.PermissionsAuthority
import com.coleji.neptune.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE}
import com.coleji.neptune.Storable.Fields.{DatabaseField, NullableDateDatabaseField}
import com.coleji.neptune.Storable.{Filter, StorableClass, StorableObject}
import com.coleji.neptune.Util._

import java.time.LocalDate
import java.time.format.DateTimeFormatter

case class NullableDateColumnAlias(override val table: TableAlias[_ <: StorableObject[_ <: StorableClass]], override val field: NullableDateDatabaseField)
extends ColumnAlias[DatabaseField[Option[LocalDate]]](table, field) {
	val standardPattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

	def isYearConstant(year: Int)(implicit PA: PermissionsAuthority): Filter = PA.systemParams.persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => {
			val jan1 = LocalDate.of(year, 1, 1)
			val nextJan1 = LocalDate.of(year + 1, 1, 1)
			Filter(s"${table.name}.${field.persistenceFieldName} >= ${jan1.format(standardPattern)} AND ${table.name}.${field.persistenceFieldName} < ${nextJan1.format(standardPattern)}", List.empty)
		}
		case PERSISTENCE_SYSTEM_ORACLE => Filter(s"TO_CHAR(${table.name}.${field.persistenceFieldName}, 'YYYY') = $year", List.empty)
	}

	private def dateComparison(date: LocalDate, comp: DateComparison)(implicit PA: PermissionsAuthority): Filter = {
		val comparator: String = comp.comparator
		PA.systemParams.persistenceSystem match {
			case PERSISTENCE_SYSTEM_MYSQL =>
				Filter(s"${table.name}.${field.persistenceFieldName} $comparator '${date.format(standardPattern)}'", List.empty)
			case PERSISTENCE_SYSTEM_ORACLE =>
				Filter(s"TRUNC(${table.name}.${field.persistenceFieldName}) $comparator TO_DATE('${date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))}','MM/DD/YYYY')", List.empty)
		}
	}

	def isDateConstant(date: LocalDate): Filter = dateComparison(date, DATE_=)

	def greaterThanConstant(date: LocalDate): Filter = dateComparison(date, DATE_>)

	def lessThanConstant(date: LocalDate): Filter = dateComparison(date, DATE_<)

	def greaterEqualConstant(date: LocalDate): Filter = dateComparison(date, DATE_>=)

	def lessEqualConstant(date: LocalDate): Filter = dateComparison(date, DATE_<=)
}
