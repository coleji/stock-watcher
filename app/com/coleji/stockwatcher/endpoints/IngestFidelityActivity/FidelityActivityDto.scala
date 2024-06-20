package com.coleji.stockwatcher.endpoints.IngestFidelityActivity

import java.time.LocalDate

case class FidelityActivityDto(
	runDate: LocalDate,
	account: String,
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
)