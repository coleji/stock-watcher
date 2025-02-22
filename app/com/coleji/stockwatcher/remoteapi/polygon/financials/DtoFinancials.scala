package com.coleji.stockwatcher.remoteapi.polygon.financials

import play.api.libs.json.{Format, JsValue, Json}

import java.time.{LocalDate, LocalDateTime}

case class DtoFinancial(label: String, order: Int, unit: String, value: Double)

object DtoFinancial {
	implicit val format: Format[DtoFinancial] = Json.format[DtoFinancial]
	def apply(v: JsValue): DtoFinancial = v.as[DtoFinancial]
}

case class DtoFinancialsComprehensiveIncome(
	comprehensive_income_loss: Option[DtoFinancial],
	comprehensive_income_loss_attributable_to_noncontrolling_interest: Option[DtoFinancial],
	comprehensive_income_loss_attributable_to_parent: Option[DtoFinancial],
	other_comprehensive_income_loss_attributable_to_parent: Option[DtoFinancial],
	other_comprehensive_income_loss: Option[DtoFinancial],
	other_comprehensive_income_loss_attributable_to_noncontrolling_interest: Option[DtoFinancial]
)

object DtoFinancialsComprehensiveIncome {
	implicit val format: Format[DtoFinancialsComprehensiveIncome] = Json.format[DtoFinancialsComprehensiveIncome]
	def apply(v: JsValue): DtoFinancialsComprehensiveIncome = v.as[DtoFinancialsComprehensiveIncome]
}

case class DtoFinancialsBalanceSheet1(
	liabilities: Option[DtoFinancial],
	assets: Option[DtoFinancial],
	noncurrent_assets: Option[DtoFinancial],
	current_assets: Option[DtoFinancial],
	liabilities_and_equity: Option[DtoFinancial],
	equity: Option[DtoFinancial],
	other_noncurrent_assets: Option[DtoFinancial],
	inventory: Option[DtoFinancial],
	fixed_assets: Option[DtoFinancial],
	other_current_assets: Option[DtoFinancial],
	other_current_liabilities: Option[DtoFinancial],
	noncurrent_liabilities: Option[DtoFinancial],
	accounts_payable: Option[DtoFinancial],
	intangible_assets: Option[DtoFinancial],
	wages: Option[DtoFinancial],
	equity_attributable_to_noncontrolling_interest: Option[DtoFinancial],
)

object DtoFinancialsBalanceSheet1 {
	implicit val format: Format[DtoFinancialsBalanceSheet1] = Json.format[DtoFinancialsBalanceSheet1]
	def apply(v: JsValue): DtoFinancialsBalanceSheet1 = v.as[DtoFinancialsBalanceSheet1]
}

case class DtoFinancialsBalanceSheet2 (
	current_liabilities: Option[DtoFinancial],
	equity_attributable_to_parent: Option[DtoFinancial],
	temporary_equity: Option[DtoFinancial],
	redeemable_noncontrolling_interest: Option[DtoFinancial],
	long_term_debt: Option[DtoFinancial],
	other_noncurrent_liabilities: Option[DtoFinancial],
	commitments_and_contingencies: Option[DtoFinancial],
	prepaid_expenses: Option[DtoFinancial],
	accounts_receivable: Option[DtoFinancial],
	cash: Option[DtoFinancial],
	temporary_equity_attributable_to_parent: Option[DtoFinancial],
	redeemable_noncontrolling_interest_preferred: Option[DtoFinancial],
	interest_payable: Option[DtoFinancial],
	long_term_investments: Option[DtoFinancial],
	redeemable_noncontrolling_interest_common: Option[DtoFinancial],
	noncurrent_prepaid_expenses: Option[DtoFinancial],
	redeemable_noncontrolling_interest_other: Option[DtoFinancial],
	other_than_fixed_noncurrent_assets: Option[DtoFinancial]
)

object DtoFinancialsBalanceSheet2 {
	implicit val format: Format[DtoFinancialsBalanceSheet2] = Json.format[DtoFinancialsBalanceSheet2]
	def apply(v: JsValue): DtoFinancialsBalanceSheet2 = v.as[DtoFinancialsBalanceSheet2]
}

case class DtoFinancialsIncomeStatement1(
	net_income_loss: Option[DtoFinancial],
	basic_average_shares: Option[DtoFinancial],
	cost_of_revenue: Option[DtoFinancial],
	income_tax_expense_benefit_current: Option[DtoFinancial],
	income_tax_expense_benefit: Option[DtoFinancial],
	participating_securities_distributed_and_undistributed_earnings_loss_basic: Option[DtoFinancial],
	net_income_loss_attributable_to_noncontrolling_interest: Option[DtoFinancial],
	net_income_loss_available_to_common_stockholders_basic: Option[DtoFinancial],
	revenues: Option[DtoFinancial],
	preferred_stock_dividends_and_other_adjustments: Option[DtoFinancial],
	research_and_development: Option[DtoFinancial],
	diluted_earnings_per_share: Option[DtoFinancial],
	income_tax_expense_benefit_deferred: Option[DtoFinancial],
	operating_income_loss: Option[DtoFinancial],
	other_operating_expenses: Option[DtoFinancial],
	gross_profit: Option[DtoFinancial],
	benefits_costs_expenses: Option[DtoFinancial],
	basic_earnings_per_share: Option[DtoFinancial],
	costs_and_expenses: Option[DtoFinancial],
	income_loss_from_continuing_operations_before_tax: Option[DtoFinancial],
	net_income_loss_attributable_to_parent: Option[DtoFinancial],

)

object DtoFinancialsIncomeStatement1 {
	implicit val format: Format[DtoFinancialsIncomeStatement1] = Json.format[DtoFinancialsIncomeStatement1]
	def apply(v: JsValue): DtoFinancialsIncomeStatement1 = v.as[DtoFinancialsIncomeStatement1]
}

case class DtoFinancialsIncomeStatement2(
	operating_expenses: Option[DtoFinancial],
	diluted_average_shares: Option[DtoFinancial],
	income_loss_from_continuing_operations_after_tax: Option[DtoFinancial],
	interest_and_dividend_income_operating: Option[DtoFinancial],
	net_income_loss_attributable_to_redeemable_noncontrolling_interest: Option[DtoFinancial],
	nonoperating_income_loss: Option[DtoFinancial],
	income_loss_before_equity_method_investments: Option[DtoFinancial],
	income_loss_from_equity_method_investments: Option[DtoFinancial],
	interest_expense_operating: Option[DtoFinancial],
	depreciation_and_amortization: Option[DtoFinancial],
	selling_general_and_administrative_expenses: Option[DtoFinancial],
	common_stock_dividends: Option[DtoFinancial],
	income_loss_from_discontinued_operations_net_of_tax: Option[DtoFinancial],
	provision_for_loan_lease_and_other_losses: Option[DtoFinancial],

)

object DtoFinancialsIncomeStatement2 {
	implicit val format: Format[DtoFinancialsIncomeStatement2] = Json.format[DtoFinancialsIncomeStatement2]
	def apply(v: JsValue): DtoFinancialsIncomeStatement2 = v.as[DtoFinancialsIncomeStatement2]
}

case class DtoFinancialsIncomeStatement3(
	undistributed_earnings_loss_allocated_to_participating_securities_basic: Option[DtoFinancial],
	interest_income_expense_after_provision_for_losses: Option[DtoFinancial],
	interest_income_expense_operating_net: Option[DtoFinancial],
	noninterest_expense: Option[DtoFinancial],
	other_operating_income_expenses: Option[DtoFinancial],
	noninterest_income: Option[DtoFinancial],
	interest_and_debt_expense: Option[DtoFinancial],
	cost_of_revenue_goods: Option[DtoFinancial],
	cost_of_revenue_services: Option[DtoFinancial],
	income_loss_from_discontinued_operations_net_of_tax_gain_loss_on_disposal: Option[DtoFinancial],
	gain_loss_on_sale_properties_net_tax: Option[DtoFinancial],
	net_income_loss_attributable_to_nonredeemable_noncontrolling_interest: Option[DtoFinancial],
	income_loss_from_discontinued_operations_net_of_tax_during_phase_out: Option[DtoFinancial],
	income_loss_from_discontinued_operations_net_of_tax_provision_for_gain_loss_on_disposal: Option[DtoFinancial],
	income_loss_from_discontinued_operations_net_of_tax_adjustment_to_prior_year_gain_loss_on_disposal: Option[DtoFinancial]
)

object DtoFinancialsIncomeStatement3 {
	implicit val format: Format[DtoFinancialsIncomeStatement3] = Json.format[DtoFinancialsIncomeStatement3]
	def apply(v: JsValue): DtoFinancialsIncomeStatement3 = v.as[DtoFinancialsIncomeStatement3]
}

case class DtoFinancialsCashFlowStatement(
	net_cash_flow_from_financing_activities_continuing: Option[DtoFinancial],
	net_cash_flow_from_financing_activities: Option[DtoFinancial],
	net_cash_flow: Option[DtoFinancial],
	net_cash_flow_continuing: Option[DtoFinancial],
	net_cash_flow_from_operating_activities: Option[DtoFinancial],
	net_cash_flow_from_operating_activities_continuing: Option[DtoFinancial],
	exchange_gains_losses: Option[DtoFinancial],
	net_cash_flow_from_investing_activities_continuing: Option[DtoFinancial],
	net_cash_flow_from_investing_activities: Option[DtoFinancial],
	net_cash_flow_from_operating_activities_discontinued: Option[DtoFinancial],
	net_cash_flow_discontinued: Option[DtoFinancial],
	net_cash_flow_from_investing_activities_discontinued: Option[DtoFinancial],
	net_cash_flow_from_financing_activities_discontinued: Option[DtoFinancial]
)

object DtoFinancialsCashFlowStatement {
	implicit val format: Format[DtoFinancialsCashFlowStatement] = Json.format[DtoFinancialsCashFlowStatement]
	def apply(v: JsValue): DtoFinancialsCashFlowStatement = v.as[DtoFinancialsCashFlowStatement]
}

case class DtoFinancialsGrouping(
	comprehensive_income: Option[DtoFinancialsComprehensiveIncome],
	income_statement1: Option[DtoFinancialsIncomeStatement1],
	income_statement2: Option[DtoFinancialsIncomeStatement2],
	income_statement3: Option[DtoFinancialsIncomeStatement3],
	balance_sheet1: Option[DtoFinancialsBalanceSheet1],
	balance_sheet2: Option[DtoFinancialsBalanceSheet2],
	cash_flow_statement: Option[DtoFinancialsCashFlowStatement]
)

object DtoFinancialsGrouping {
	implicit val format: Format[DtoFinancialsGrouping] = Json.format[DtoFinancialsGrouping]
	def apply(v: JsValue): DtoFinancialsGrouping = v.as[DtoFinancialsGrouping]
}

case class DtoFinancialsEvent(
//	id: String,
	start_date: LocalDate,
	end_date: LocalDate,
	filing_date: Option[LocalDate],
	acceptance_datetime: Option[LocalDateTime],
	timeframe: String,
	fiscal_period: String,
	fiscal_year: String,
	cik: String,
	sic: String,
	tickers: Option[List[String]],
	company_name: String,
	source_filing_url: Option[String],
	source_filing_file_url: Option[String],
	financials: DtoFinancialsGrouping
)

object DtoFinancialsEvent {
	implicit val format: Format[DtoFinancialsEvent] = Json.format[DtoFinancialsEvent]
	def apply(v: JsValue): DtoFinancialsEvent = v.as[DtoFinancialsEvent]
}

case class DtoFinancialsApiResult(
	results: List[DtoFinancialsEvent],
	status: String,
	request_id: String,
	next_url: Option[String]
)

object DtoFinancialsApiResult {
	implicit val format: Format[DtoFinancialsApiResult] = Json.format[DtoFinancialsApiResult]
	def apply(v: JsValue): DtoFinancialsApiResult = v.as[DtoFinancialsApiResult]
}