package com.coleji.stockwatcher.task

import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.neptune.IO.PreparedQueries.PreparedQueryForSelect
import com.coleji.neptune.Storable.ResultSetWrapper
import com.coleji.neptune.Util.StringUtil
import com.coleji.stockwatcher.StockWatcherTask
import com.coleji.stockwatcher.entity.entitydefinitions.{PolygonFinancial, PolygonFinancialEvent, PolygonFinancialEventTicker}
import com.coleji.stockwatcher.remoteapi.polygon.financials.{DtoFinancialsEvent, Financials}

import java.time.LocalDate

object FetchFinancialsTask extends StockWatcherTask {
	protected override def taskAction(rc: UnlockedRequestCache): Unit = {
		val latestFilingQ = new PreparedQueryForSelect[LocalDate](Set(rc.companion)) {

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): LocalDate = rsw.getLocalDate(1)

			override def getQuery: String = "select max(filing_date) from s_p_financials_events"
		}

		val maxDate = Option(rc.executePreparedQueryForSelect(latestFilingQ).head).getOrElse(LocalDate.MIN)

		println("max date is: " + maxDate)

		val events = Financials.getFinancials(maxDate, appendLog)
		println(events.length)
		val toInsert = events
			.filter(s => s.filing_date.nonEmpty && s.filing_date.get.isAfter(maxDate))

		println("Events to process: " + toInsert.length)
		var financialCt = 0
		toInsert.foreach(e => {
			financialCt = financialCt + storeEvent(rc, e)
		})
		appendLog("TOTAL FINANCIALS INSERTED: " + financialCt)
	}

	private def storeEvent(rc: UnlockedRequestCache, dto: DtoFinancialsEvent): Int = {
		val e = new PolygonFinancialEvent
		e.values.startDate.update(dto.start_date)
		e.values.endDate.update(dto.end_date)
		e.values.filingDate.update(dto.filing_date)
		e.values.acceptanceDatetime.update(dto.acceptance_datetime)
		e.values.timeframe.update(dto.timeframe)
		e.values.fiscalPeriod.update(dto.fiscal_period)
		val fiscalYear = StringUtil.tryParseInt(dto.fiscal_year).toOption
		if (fiscalYear.isEmpty) appendLog("Found non-int fiscal year: " + dto.fiscal_year)
		e.values.fiscalYear.update(fiscalYear)
		e.values.cik.update(dto.cik)
		e.values.companyName.update(dto.company_name)
		e.values.sourceFilingUrl.update(dto.source_filing_url)
		e.values.sourceFilingFileUrl.update(dto.source_filing_file_url)
		rc.commitObjectToDatabase(e)

		val eventId = e.values.financialEventId.get

		dto.tickers.foreach(tt => {
			val toInsert = tt.map(t => PolygonFinancialEventTicker.apply(eventId, t))
			rc.batchInsertObjects(toInsert)
		})

		val f = PolygonFinancial.apply(eventId)

		val comprehensiveIncome = dto.financials.comprehensive_income.map(ff => List(
			ff.comprehensive_income_loss.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_comprehensive_income_loss)),
			ff.comprehensive_income_loss_attributable_to_noncontrolling_interest.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_comprehensive_income_loss_attributable_to_noncontrolling_interest)),
			ff.comprehensive_income_loss_attributable_to_parent.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_comprehensive_income_loss_attributable_to_parent)),
			ff.other_comprehensive_income_loss_attributable_to_parent.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_other_comprehensive_income_loss_attributable_to_parent)),
			ff.other_comprehensive_income_loss.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_other_comprehensive_income_loss)),
			ff.other_comprehensive_income_loss_attributable_to_noncontrolling_interest.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_other_comprehensive_income_loss_attributable_to_noncontrolling_interest)),
		))

		val incomeStatement1 = dto.financials.income_statement1.map(ff => List(
			ff.net_income_loss.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_net_income_loss)),
			ff.basic_average_shares.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_basic_average_shares)),
			ff.cost_of_revenue.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_cost_of_revenue)),
			ff.income_tax_expense_benefit_current.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_income_tax_expense_benefit_current)),
			ff.income_tax_expense_benefit.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_income_tax_expense_benefit)),
			ff.participating_securities_distributed_and_undistributed_earnings_loss_basic.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_participating_securities_distributed_and_undistributed_earnings_loss_basic)),
			ff.net_income_loss_attributable_to_noncontrolling_interest.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_net_income_loss_attributable_to_noncontrolling_interest)),
			ff.net_income_loss_available_to_common_stockholders_basic.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_net_income_loss_available_to_common_stockholders_basic)),
			ff.revenues.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_revenues)),
			ff.preferred_stock_dividends_and_other_adjustments.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_preferred_stock_dividends_and_other_adjustments)),
			ff.research_and_development.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_research_and_development)),
			ff.diluted_earnings_per_share.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_diluted_earnings_per_share)),
			ff.income_tax_expense_benefit_deferred.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_income_tax_expense_benefit_deferred)),
			ff.operating_income_loss.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_operating_income_loss)),
			ff.other_operating_expenses.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_other_operating_expenses)),
			ff.gross_profit.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_gross_profit)),
			ff.benefits_costs_expenses.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_benefits_costs_expenses)),
			ff.basic_earnings_per_share.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_basic_earnings_per_share)),
			ff.costs_and_expenses.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_costs_and_expenses)),
			ff.income_loss_from_continuing_operations_before_tax.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_income_loss_from_continuing_operations_before_tax)),
			ff.net_income_loss_attributable_to_parent.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_net_income_loss_attributable_to_parent)),
		))

		val incomeStatement2 = dto.financials.income_statement2.map(ff => List(
			ff.operating_expenses.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_operating_expenses)),
			ff.diluted_average_shares.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_diluted_average_shares)),
			ff.income_loss_from_continuing_operations_after_tax.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_income_loss_from_continuing_operations_after_tax)),
			ff.interest_and_dividend_income_operating.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_interest_and_dividend_income_operating)),
			ff.net_income_loss_attributable_to_redeemable_noncontrolling_interest.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_net_income_loss_attributable_to_redeemable_noncontrolling_interest)),
			ff.nonoperating_income_loss.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_nonoperating_income_loss)),
			ff.income_loss_before_equity_method_investments.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_income_loss_before_equity_method_investments)),
			ff.income_loss_from_equity_method_investments.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_income_loss_from_equity_method_investments)),
			ff.interest_expense_operating.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_interest_expense_operating)),
			ff.depreciation_and_amortization.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_depreciation_and_amortization)),
			ff.selling_general_and_administrative_expenses.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_selling_general_and_administrative_expenses)),
			ff.common_stock_dividends.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_common_stock_dividends)),
			ff.income_loss_from_discontinued_operations_net_of_tax.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_income_loss_from_discontinued_operations_net_of_tax)),
			ff.provision_for_loan_lease_and_other_losses.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_provision_for_loan_lease_and_other_losses)),
		))

		val incomeStatement3 = dto.financials.income_statement3.map(ff => List(
			ff.undistributed_earnings_loss_allocated_to_participating_securities_basic.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_undistributed_earnings_loss_allocated_to_participating_securities_basic)),
			ff.interest_income_expense_after_provision_for_losses.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_interest_income_expense_after_provision_for_losses)),
			ff.interest_income_expense_operating_net.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_interest_income_expense_operating_net)),
			ff.noninterest_expense.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_noninterest_expense)),
			ff.other_operating_income_expenses.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_other_operating_income_expenses)),
			ff.noninterest_income.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_noninterest_income)),
			ff.interest_and_debt_expense.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_interest_and_debt_expense)),
			ff.cost_of_revenue_goods.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_cost_of_revenue_goods)),
			ff.cost_of_revenue_services.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_cost_of_revenue_services)),
			ff.income_loss_from_discontinued_operations_net_of_tax_gain_loss_on_disposal.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_income_loss_from_discontinued_operations_net_of_tax_gain_loss_on_disposal)),
			ff.gain_loss_on_sale_properties_net_tax.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_gain_loss_on_sale_properties_net_tax)),
			ff.net_income_loss_attributable_to_nonredeemable_noncontrolling_interest.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_net_income_loss_attributable_to_nonredeemable_noncontrolling_interest)),
			ff.income_loss_from_discontinued_operations_net_of_tax_during_phase_out.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_income_loss_from_discontinued_operations_net_of_tax_during_phase_out)),
			ff.income_loss_from_discontinued_operations_net_of_tax_provision_for_gain_loss_on_disposal.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_income_loss_from_discontinued_operations_net_of_tax_provision_for_gain_loss_on_disposal)),
			ff.income_loss_from_discontinued_operations_net_of_tax_adjustment_to_prior_year_gain_loss_on_disposal.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_income_loss_from_discontinued_operations_net_of_tax_adjustment_to_prior_year_gain_loss_on_disposal)),
		))

		val balanceSheet1 = dto.financials.balance_sheet1.map(ff => List(
			ff.liabilities.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_liabilities)),
			ff.assets.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_assets)),
			ff.noncurrent_assets.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_noncurrent_assets)),
			ff.current_assets.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_current_assets)),
			ff.liabilities_and_equity.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_liabilities_and_equity)),
			ff.equity.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_equity)),
			ff.other_noncurrent_assets.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_other_noncurrent_assets)),
			ff.inventory.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_inventory)),
			ff.fixed_assets.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_fixed_assets)),
			ff.other_current_assets.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_other_current_assets)),
			ff.other_current_liabilities.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_other_current_liabilities)),
			ff.noncurrent_liabilities.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_noncurrent_liabilities)),
			ff.accounts_payable.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_accounts_payable)),
			ff.intangible_assets.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_intangible_assets)),
			ff.wages.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_wages)),
			ff.equity_attributable_to_noncontrolling_interest.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_equity_attributable_to_noncontrolling_interest)),
		))

		val balanceSheet2 = dto.financials.balance_sheet2.map(ff => List(
			ff.current_liabilities.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_current_liabilities)),
			ff.equity_attributable_to_parent.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_equity_attributable_to_parent)),
			ff.temporary_equity.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_temporary_equity)),
			ff.redeemable_noncontrolling_interest.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_redeemable_noncontrolling_interest)),
			ff.long_term_debt.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_long_term_debt)),
			ff.other_noncurrent_liabilities.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_other_noncurrent_liabilities)),
			ff.commitments_and_contingencies.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_commitments_and_contingencies)),
			ff.prepaid_expenses.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_prepaid_expenses)),
			ff.accounts_receivable.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_accounts_receivable)),
			ff.cash.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_cash)),
			ff.temporary_equity_attributable_to_parent.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_temporary_equity_attributable_to_parent)),
			ff.redeemable_noncontrolling_interest_preferred.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_redeemable_noncontrolling_interest_preferred)),
			ff.interest_payable.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_interest_payable)),
			ff.long_term_investments.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_long_term_investments)),
			ff.redeemable_noncontrolling_interest_common.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_redeemable_noncontrolling_interest_common)),
			ff.noncurrent_prepaid_expenses.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_noncurrent_prepaid_expenses)),
			ff.redeemable_noncontrolling_interest_other.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_redeemable_noncontrolling_interest_other)),
			ff.other_than_fixed_noncurrent_assets.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_other_than_fixed_noncurrent_assets)),
		))

		val cashFlowStatement = dto.financials.cash_flow_statement.map(ff => List(
			ff.net_cash_flow_from_financing_activities_continuing.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_net_cash_flow_from_financing_activities_continuing)),
			ff.net_cash_flow_from_financing_activities.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_net_cash_flow_from_financing_activities)),
			ff.net_cash_flow.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_net_cash_flow)),
			ff.net_cash_flow_continuing.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_net_cash_flow_continuing)),
			ff.net_cash_flow_from_operating_activities.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_net_cash_flow_from_operating_activities)),
			ff.net_cash_flow_from_operating_activities_continuing.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_net_cash_flow_from_operating_activities_continuing)),
			ff.exchange_gains_losses.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_exchange_gains_losses)),
			ff.net_cash_flow_from_investing_activities_continuing.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_net_cash_flow_from_investing_activities_continuing)),
			ff.net_cash_flow_from_investing_activities.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_net_cash_flow_from_investing_activities)),
			ff.net_cash_flow_from_operating_activities_discontinued.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_net_cash_flow_from_operating_activities_discontinued)),
			ff.net_cash_flow_discontinued.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_net_cash_flow_discontinued)),
			ff.net_cash_flow_from_investing_activities_discontinued.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_net_cash_flow_from_investing_activities_discontinued)),
			ff.net_cash_flow_from_financing_activities_discontinued.map(f(PolygonFinancial.FINANCIAL_KEYS.FINANCIAL_KEY_net_cash_flow_from_financing_activities_discontinued)),
		))

		val financialsToInsert = List(
			comprehensiveIncome,
			incomeStatement1,
			incomeStatement2,
			incomeStatement3,
			balanceSheet1,
			balanceSheet2,
			cashFlowStatement
		)
			.filter(_.nonEmpty)
			.flatMap(_.get)
			.filter(_.nonEmpty)
			.map(_.get)

		println("inserting financials for event, ct: " + financialsToInsert.length)

		rc.batchInsertObjects(financialsToInsert)
		financialsToInsert.length
	}
}
