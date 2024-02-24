package com.coleji.stockwatcher

object TickerReference {
	val blacklistedTickers: Set[String] = Set(
		"TOPS",
		"RNVA",
		"ASTI",
		"DCTH",
		"CEI",
		"NSPR",
	)
}
