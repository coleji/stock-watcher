package com.coleji.stockwatcher.entity.entitydefinitions

import com.coleji.neptune.Storable.FieldValues.{DoubleFieldValue, IntFieldValue, StringFieldValue}
import com.coleji.neptune.Storable.Fields.{DoubleDatabaseField, IntDatabaseField, StringDatabaseField}
import com.coleji.neptune.Storable.{FieldsObject, StorableClass, StorableObject, ValuesObject}
import com.coleji.stockwatcher.remoteapi.polygon.financials.DtoFinancial

class PolygonFinancial extends StorableClass(PolygonFinancial) {
	override object values extends ValuesObject {
		val financialId = new IntFieldValue(self, PolygonFinancial.fields.financialId)
		val financialEventId = new IntFieldValue(self, PolygonFinancial.fields.financialEventId)
		val financialKey = new StringFieldValue(self,PolygonFinancial.fields.financialKey)
		val label = new StringFieldValue(self, PolygonFinancial.fields.label)
		val financialOrder = new IntFieldValue(self, PolygonFinancial.fields.financialOrder)
		val unit = new StringFieldValue(self, PolygonFinancial.fields.unit)
		val value = new DoubleFieldValue(self, PolygonFinancial.fields.value)
	}
}

object PolygonFinancial extends StorableObject[PolygonFinancial] {
	override val entityName: String = "s_p_financials"

	object fields extends FieldsObject {
		val financialId = new IntDatabaseField(self, "financial_id")
		val financialEventId = new IntDatabaseField(self, "financial_event_id")
		val financialKey = new StringDatabaseField(self, "financial_key", 100)
		val label = new StringDatabaseField(self, "label", 100)
		val financialOrder = new IntDatabaseField(self, "financial_order")
		val unit = new StringDatabaseField(self, "unit", 20)
		val value = new DoubleDatabaseField(self, "value")
	}

	def primaryKey: IntDatabaseField = fields.financialId

	val apply: Int => String => DtoFinancial => PolygonFinancial = financialEventId => key => dto => {
		val ret = new PolygonFinancial
		ret.values.financialEventId.update(financialEventId)
		ret.values.financialKey.update(key)
		ret.values.label.update(dto.label)
		ret.values.financialOrder.update(dto.order)
		ret.values.unit.update(dto.unit)
		ret.values.value.update(dto.value)
		ret
	}

	def apply(
		financialEventId: Int,
		financialKey: String,
		label: String,
		financialOrder: Int,
		unit: String,
		value: Double,
	): PolygonFinancial = {
		val ret = new PolygonFinancial
		ret.values.financialEventId.update(financialEventId)
		ret.values.financialKey.update(financialKey)
		ret.values.label.update(label)
		ret.values.financialOrder.update(financialOrder)
		ret.values.unit.update(unit)
		ret.values.value.update(value)
		ret
	}

	object FINANCIAL_KEYS {
		val FINANCIAL_KEY_accounts_payable = "accounts_payable"
		val FINANCIAL_KEY_accounts_receivable = "accounts_receivable"
		val FINANCIAL_KEY_assets = "assets"
		val FINANCIAL_KEY_basic_average_shares = "basic_average_shares"
		val FINANCIAL_KEY_basic_earnings_per_share = "basic_earnings_per_share"
		val FINANCIAL_KEY_benefits_costs_expenses = "benefits_costs_expenses"
		val FINANCIAL_KEY_cash = "cash"
		val FINANCIAL_KEY_commitments_and_contingencies = "commitments_and_contingencies"
		val FINANCIAL_KEY_common_stock_dividends = "common_stock_dividends"
		val FINANCIAL_KEY_comprehensive_income_loss = "comprehensive_income_loss"
		val FINANCIAL_KEY_comprehensive_income_loss_attributable_to_noncontrolling_interest = "comprehensive_income_loss_attributable_to_noncontrolling_interest"
		val FINANCIAL_KEY_comprehensive_income_loss_attributable_to_parent = "comprehensive_income_loss_attributable_to_parent"
		val FINANCIAL_KEY_cost_of_revenue = "cost_of_revenue"
		val FINANCIAL_KEY_cost_of_revenue_goods = "cost_of_revenue_goods"
		val FINANCIAL_KEY_cost_of_revenue_services = "cost_of_revenue_services"
		val FINANCIAL_KEY_costs_and_expenses = "costs_and_expenses"
		val FINANCIAL_KEY_current_assets = "current_assets"
		val FINANCIAL_KEY_current_liabilities = "current_liabilities"
		val FINANCIAL_KEY_depreciation_and_amortization = "depreciation_and_amortization"
		val FINANCIAL_KEY_diluted_average_shares = "diluted_average_shares"
		val FINANCIAL_KEY_diluted_earnings_per_share = "diluted_earnings_per_share"
		val FINANCIAL_KEY_equity = "equity"
		val FINANCIAL_KEY_equity_attributable_to_noncontrolling_interest = "equity_attributable_to_noncontrolling_interest"
		val FINANCIAL_KEY_equity_attributable_to_parent = "equity_attributable_to_parent"
		val FINANCIAL_KEY_exchange_gains_losses = "exchange_gains_losses"
		val FINANCIAL_KEY_fixed_assets = "fixed_assets"
		val FINANCIAL_KEY_gain_loss_on_sale_properties_net_tax = "gain_loss_on_sale_properties_net_tax"
		val FINANCIAL_KEY_gross_profit = "gross_profit"
		val FINANCIAL_KEY_income_loss_before_equity_method_investments = "income_loss_before_equity_method_investments"
		val FINANCIAL_KEY_income_loss_from_continuing_operations_after_tax = "income_loss_from_continuing_operations_after_tax"
		val FINANCIAL_KEY_income_loss_from_continuing_operations_before_tax = "income_loss_from_continuing_operations_before_tax"
		val FINANCIAL_KEY_income_loss_from_discontinued_operations_net_of_tax = "income_loss_from_discontinued_operations_net_of_tax"
		val FINANCIAL_KEY_income_loss_from_discontinued_operations_net_of_tax_adjustment_to_prior_year_gain_loss_on_disposal = "income_loss_from_discontinued_operations_net_of_tax_adjustment_to_prior_year_gain_loss_on_disposal"
		val FINANCIAL_KEY_income_loss_from_discontinued_operations_net_of_tax_during_phase_out = "income_loss_from_discontinued_operations_net_of_tax_during_phase_out"
		val FINANCIAL_KEY_income_loss_from_discontinued_operations_net_of_tax_gain_loss_on_disposal = "income_loss_from_discontinued_operations_net_of_tax_gain_loss_on_disposal"
		val FINANCIAL_KEY_income_loss_from_discontinued_operations_net_of_tax_provision_for_gain_loss_on_disposal = "income_loss_from_discontinued_operations_net_of_tax_provision_for_gain_loss_on_disposal"
		val FINANCIAL_KEY_income_loss_from_equity_method_investments = "income_loss_from_equity_method_investments"
		val FINANCIAL_KEY_income_tax_expense_benefit = "income_tax_expense_benefit"
		val FINANCIAL_KEY_income_tax_expense_benefit_current = "income_tax_expense_benefit_current"
		val FINANCIAL_KEY_income_tax_expense_benefit_deferred = "income_tax_expense_benefit_deferred"
		val FINANCIAL_KEY_intangible_assets = "intangible_assets"
		val FINANCIAL_KEY_interest_and_debt_expense = "interest_and_debt_expense"
		val FINANCIAL_KEY_interest_and_dividend_income_operating = "interest_and_dividend_income_operating"
		val FINANCIAL_KEY_interest_expense_operating = "interest_expense_operating"
		val FINANCIAL_KEY_interest_income_expense_after_provision_for_losses = "interest_income_expense_after_provision_for_losses"
		val FINANCIAL_KEY_interest_income_expense_operating_net = "interest_income_expense_operating_net"
		val FINANCIAL_KEY_interest_payable = "interest_payable"
		val FINANCIAL_KEY_inventory = "inventory"
		val FINANCIAL_KEY_liabilities = "liabilities"
		val FINANCIAL_KEY_liabilities_and_equity = "liabilities_and_equity"
		val FINANCIAL_KEY_long_term_debt = "long_term_debt"
		val FINANCIAL_KEY_long_term_investments = "long_term_investments"
		val FINANCIAL_KEY_net_cash_flow = "net_cash_flow"
		val FINANCIAL_KEY_net_cash_flow_continuing = "net_cash_flow_continuing"
		val FINANCIAL_KEY_net_cash_flow_discontinued = "net_cash_flow_discontinued"
		val FINANCIAL_KEY_net_cash_flow_from_financing_activities = "net_cash_flow_from_financing_activities"
		val FINANCIAL_KEY_net_cash_flow_from_financing_activities_continuing = "net_cash_flow_from_financing_activities_continuing"
		val FINANCIAL_KEY_net_cash_flow_from_financing_activities_discontinued = "net_cash_flow_from_financing_activities_discontinued"
		val FINANCIAL_KEY_net_cash_flow_from_investing_activities = "net_cash_flow_from_investing_activities"
		val FINANCIAL_KEY_net_cash_flow_from_investing_activities_continuing = "net_cash_flow_from_investing_activities_continuing"
		val FINANCIAL_KEY_net_cash_flow_from_investing_activities_discontinued = "net_cash_flow_from_investing_activities_discontinued"
		val FINANCIAL_KEY_net_cash_flow_from_operating_activities = "net_cash_flow_from_operating_activities"
		val FINANCIAL_KEY_net_cash_flow_from_operating_activities_continuing = "net_cash_flow_from_operating_activities_continuing"
		val FINANCIAL_KEY_net_cash_flow_from_operating_activities_discontinued = "net_cash_flow_from_operating_activities_discontinued"
		val FINANCIAL_KEY_net_income_loss = "net_income_loss"
		val FINANCIAL_KEY_net_income_loss_attributable_to_noncontrolling_interest = "net_income_loss_attributable_to_noncontrolling_interest"
		val FINANCIAL_KEY_net_income_loss_attributable_to_nonredeemable_noncontrolling_interest = "net_income_loss_attributable_to_nonredeemable_noncontrolling_interest"
		val FINANCIAL_KEY_net_income_loss_attributable_to_parent = "net_income_loss_attributable_to_parent"
		val FINANCIAL_KEY_net_income_loss_attributable_to_redeemable_noncontrolling_interest = "net_income_loss_attributable_to_redeemable_noncontrolling_interest"
		val FINANCIAL_KEY_net_income_loss_available_to_common_stockholders_basic = "net_income_loss_available_to_common_stockholders_basic"
		val FINANCIAL_KEY_noncurrent_assets = "noncurrent_assets"
		val FINANCIAL_KEY_noncurrent_liabilities = "noncurrent_liabilities"
		val FINANCIAL_KEY_noncurrent_prepaid_expenses = "noncurrent_prepaid_expenses"
		val FINANCIAL_KEY_noninterest_expense = "noninterest_expense"
		val FINANCIAL_KEY_noninterest_income = "noninterest_income"
		val FINANCIAL_KEY_nonoperating_income_loss = "nonoperating_income_loss"
		val FINANCIAL_KEY_operating_expenses = "operating_expenses"
		val FINANCIAL_KEY_operating_income_loss = "operating_income_loss"
		val FINANCIAL_KEY_other_comprehensive_income_loss = "other_comprehensive_income_loss"
		val FINANCIAL_KEY_other_comprehensive_income_loss_attributable_to_noncontrolling_interest = "other_comprehensive_income_loss_attributable_to_noncontrolling_interest"
		val FINANCIAL_KEY_other_comprehensive_income_loss_attributable_to_parent = "other_comprehensive_income_loss_attributable_to_parent"
		val FINANCIAL_KEY_other_current_assets = "other_current_assets"
		val FINANCIAL_KEY_other_current_liabilities = "other_current_liabilities"
		val FINANCIAL_KEY_other_noncurrent_assets = "other_noncurrent_assets"
		val FINANCIAL_KEY_other_noncurrent_liabilities = "other_noncurrent_liabilities"
		val FINANCIAL_KEY_other_operating_expenses = "other_operating_expenses"
		val FINANCIAL_KEY_other_operating_income_expenses = "other_operating_income_expenses"
		val FINANCIAL_KEY_other_than_fixed_noncurrent_assets = "other_than_fixed_noncurrent_assets"
		val FINANCIAL_KEY_participating_securities_distributed_and_undistributed_earnings_loss_basic = "participating_securities_distributed_and_undistributed_earnings_loss_basic"
		val FINANCIAL_KEY_preferred_stock_dividends_and_other_adjustments = "preferred_stock_dividends_and_other_adjustments"
		val FINANCIAL_KEY_prepaid_expenses = "prepaid_expenses"
		val FINANCIAL_KEY_provision_for_loan_lease_and_other_losses = "provision_for_loan_lease_and_other_losses"
		val FINANCIAL_KEY_redeemable_noncontrolling_interest = "redeemable_noncontrolling_interest"
		val FINANCIAL_KEY_redeemable_noncontrolling_interest_common = "redeemable_noncontrolling_interest_common"
		val FINANCIAL_KEY_redeemable_noncontrolling_interest_other = "redeemable_noncontrolling_interest_other"
		val FINANCIAL_KEY_redeemable_noncontrolling_interest_preferred = "redeemable_noncontrolling_interest_preferred"
		val FINANCIAL_KEY_research_and_development = "research_and_development"
		val FINANCIAL_KEY_revenues = "revenues"
		val FINANCIAL_KEY_selling_general_and_administrative_expenses = "selling_general_and_administrative_expenses"
		val FINANCIAL_KEY_temporary_equity = "temporary_equity"
		val FINANCIAL_KEY_temporary_equity_attributable_to_parent = "temporary_equity_attributable_to_parent"
		val FINANCIAL_KEY_undistributed_earnings_loss_allocated_to_participating_securities_basic = "undistributed_earnings_loss_allocated_to_participating_securities_basic"
		val FINANCIAL_KEY_wages = "wages"

	}
}