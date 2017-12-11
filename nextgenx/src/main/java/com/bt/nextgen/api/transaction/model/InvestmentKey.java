package com.bt.nextgen.api.transaction.model;

public class InvestmentKey extends PortfolioKey
{
	private String investmentId;

	public InvestmentKey()
	{
		super();
	}
	
	public InvestmentKey(String clientId, String portfolioId, String investmentId)
	{
		super(clientId, portfolioId);
		this.investmentId = investmentId;
	}

	public String getInvestmentId()
	{
		return investmentId;
	}

	public void setInvestmentId(String investmentId)
	{
		this.investmentId = investmentId;
	}

}
