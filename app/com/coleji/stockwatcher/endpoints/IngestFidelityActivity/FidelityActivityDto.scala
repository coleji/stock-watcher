package com.coleji.stockwatcher.endpoints.IngestFidelityActivity

import com.coleji.stockwatcher.entity.entitydefinitions.AccountActivity

import java.time.LocalDate

case class FidelityActivityDto(
	amountExcluded: Option[Int],
	runDate: LocalDate,
//	account: String,
	action: String,
	symbol: Option[String],
	description: String,
	`type`: Option[String],
	exchangeQuantity: Option[Int],
	exchangeCurrency: Option[String],
	quantity: Option[Double],
	currency: Option[String],
	price: Option[Double],
	exchangeRate: Option[Double],
	commission: Option[Double],
	fees: Option[Double],
	accruedInterest: Option[Double],
	amount: Double,
	settlementDate: Option[LocalDate]
) {
	def toStorable(): AccountActivity = {
		val ret = new AccountActivity
		ret.values.runDate.update(this.runDate)
		ret.values.symbol.update(this.symbol.get)
		ret.values.description.update(this.description)
		ret.values.quantity.update(this.quantity.get)
		ret.values.price.update(this.price.get)
		ret.values.fees.update(this.fees)
		ret.values.amount.update(this.amount)
		ret.values.settlementDate.update(this.settlementDate)
		ret
	}
}