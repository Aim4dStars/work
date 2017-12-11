package com.bt.nextgen.api.account.v1.model;

import com.bt.nextgen.account.api.model.InvestmentValuationDto;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * @deprecated Use V2
 */
@Deprecated
public class ValuationSummaryDto
{
	private final BigDecimal balance;
	private final BigDecimal portfolioPercent;
	private final BigDecimal income;
	private final BigDecimal incomePercent;
	private final List <InvestmentValuationDto> investments;

	public ValuationSummaryDto(BigDecimal balance, BigDecimal portfolioPercent, BigDecimal income, BigDecimal incomePercent,
		List <InvestmentValuationDto> investments)
	{
		super();
		this.balance = balance;
		this.portfolioPercent = portfolioPercent;
		this.income = income;
		this.incomePercent = incomePercent;
		this.investments = investments == null ? null : Collections.unmodifiableList(investments);
	}

	public BigDecimal getBalance()
	{
		return balance;
	}

	public BigDecimal getPortfolioPercent()
	{
		return portfolioPercent;
	}

	public BigDecimal getIncome()
	{
		return income;
	}

	public BigDecimal getIncomePercent()
	{
		return incomePercent;
	}

	public List <InvestmentValuationDto> getInvestments()
	{
		return investments;
	}

}
