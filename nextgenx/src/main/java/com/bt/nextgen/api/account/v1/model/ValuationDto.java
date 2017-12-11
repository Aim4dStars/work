package com.bt.nextgen.api.account.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.math.BigDecimal;

/**
 * @deprecated Use V2
 */
@Deprecated
public class ValuationDto extends BaseDto implements KeyedDto <DatedAccountKey>
{
	private final DatedAccountKey key;
	private final BigDecimal balance;
	private final BigDecimal income;
	private final String accountType;
	private final ValuationSummaryDto cashManagement;
	private final ValuationSummaryDto termDeposits;
	private final ValuationSummaryDto managedPortfolios;
	private final ValuationSummaryDto managedFunds;

	public ValuationDto(DatedAccountKey key, BigDecimal balance, BigDecimal income, String accountType,
		ValuationSummaryListDto valuationSummaryList)
	{
		super();
		this.key = key;
		this.balance = balance;
		this.income = income;
		this.accountType = accountType;
		this.cashManagement = valuationSummaryList.getCashValuation();
		this.termDeposits = valuationSummaryList.getTermDepositValuation();
		this.managedPortfolios = valuationSummaryList.getManagedPortfolioValuation();
		this.managedFunds = valuationSummaryList.getManagedFundValuation();
	}

	@Override
	public DatedAccountKey getKey()
	{
		return key;
	}

	public BigDecimal getBalance()
	{
		return balance;
	}

	public String getAccountType()
	{
		return accountType;
	}

	public ValuationSummaryDto getCashManagement()
	{
		return cashManagement;
	}

	public ValuationSummaryDto getTermDeposits()
	{
		return termDeposits;
	}

	public ValuationSummaryDto getManagedPortfolios()
	{
		return managedPortfolios;
	}

	public ValuationSummaryDto getManagedFunds()
	{
		return managedFunds;
	}

	public BigDecimal getIncome()
	{
		return income;
	}

}
