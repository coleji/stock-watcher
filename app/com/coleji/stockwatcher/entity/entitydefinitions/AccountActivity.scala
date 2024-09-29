package com.coleji.stockwatcher.entity.entitydefinitions

import com.coleji.neptune.Storable.FieldValues.{DateFieldValue, DoubleFieldValue, IntFieldValue, NullableDateFieldValue, NullableDoubleFieldValue, StringFieldValue}
import com.coleji.neptune.Storable.Fields.{DateDatabaseField, DoubleDatabaseField, IntDatabaseField, NullableDateDatabaseField, NullableDoubleDatabaseField, StringDatabaseField}
import com.coleji.neptune.Storable.{FieldsObject, StorableClass, StorableObject, ValuesObject}

class AccountActivity extends StorableClass(AccountActivity) {
	override object values extends ValuesObject {
		val id = new IntFieldValue(self, AccountActivity.fields.id)
		val runDate = new DateFieldValue(self, AccountActivity.fields.runDate)
		val symbol = new StringFieldValue(self, AccountActivity.fields.symbol)
		val description = new StringFieldValue(self, AccountActivity.fields.description)
		val quantity = new DoubleFieldValue(self, AccountActivity.fields.quantity)
		val price = new DoubleFieldValue(self, AccountActivity.fields.price)
		val fees = new NullableDoubleFieldValue(self, AccountActivity.fields.fees)
		val amount = new DoubleFieldValue(self, AccountActivity.fields.amount)
		val settlementDate = new NullableDateFieldValue(self, AccountActivity.fields.settlementDate)
	}
}

object AccountActivity extends StorableObject[AccountActivity] {
	override val entityName: String = "account_activity"

	object fields extends FieldsObject {
		val id = new IntDatabaseField(self, "id")
		val activityType = new StringDatabaseField(self, "activityType", 1)  // B, S for bought, sold
		val runDate = new DateDatabaseField(self, "runDate")
		val symbol = new StringDatabaseField(self, "symbol", 50)
		val description = new StringDatabaseField(self, "description", 200)
		val quantity = new DoubleDatabaseField(self, "quantity")
		val price = new DoubleDatabaseField(self, "price")
		val fees = new NullableDoubleDatabaseField(self, "fees")
		val amount = new DoubleDatabaseField(self, "amount")
		val settlementDate = new NullableDateDatabaseField(self, "settlementDate")
	}

	def primaryKey: IntDatabaseField = fields.id
}

/*
CREATE TABLE `account_activity` (
  `id` int NOT NULL AUTO_INCREMENT,
  `runDate` date NOT NULL,
  `symbol` varchar(50) COLLATE utf8mb4_bin NOT NULL,
  `description` varchar(200) COLLATE utf8mb4_bin NOT NULL,
  `quantity` decimal(11,2) NOT NULL,
  `price` decimal(11,2) NOT NULL,
  `fees` decimal(11,2) DEFAULT NULL,
  `amount` decimal(11,2) NOT NULL,
  `settlementDate` date,
  PRIMARY KEY(`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24493051 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin

 */