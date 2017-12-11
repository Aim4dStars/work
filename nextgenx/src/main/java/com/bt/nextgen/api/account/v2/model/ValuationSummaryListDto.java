package com.bt.nextgen.api.account.v2.model;

import com.bt.nextgen.core.api.model.BaseDto;

@Deprecated
public class ValuationSummaryListDto extends BaseDto
{
	private final ValuationSummaryDto cashValuation;
	private final ValuationSummaryDto termDepositValuation;
	private final ValuationSummaryDto managedPortfolioValuation;
	private final ValuationSummaryDto managedFundValuation;

	public ValuationSummaryListDto()
	{
		super();
		this.cashValuation = null;
		this.termDepositValuation = null;
		this.managedPortfolioValuation = null;
		this.managedFundValuation = null;
	}

	public ValuationSummaryListDto(ValuationSummaryDto cashValuation, ValuationSummaryDto termDepositValuation,
		ValuationSummaryDto managedPortfolioValuation, ValuationSummaryDto managedFundValuation)
	{
		super();
		this.cashValuation = cashValuation;
		this.termDepositValuation = termDepositValuation;
		this.managedPortfolioValuation = managedPortfolioValuation;
		this.managedFundValuation = managedFundValuation;
	}

	public ValuationSummaryDto getCashValuation()
	{
		return cashValuation;
	}

	public ValuationSummaryDto getTermDepositValuation()
	{
		return termDepositValuation;
	}

	public ValuationSummaryDto getManagedPortfolioValuation()
	{
		return managedPortfolioValuation;
	}

	public ValuationSummaryDto getManagedFundValuation()
	{
		return managedFundValuation;
	}

}
