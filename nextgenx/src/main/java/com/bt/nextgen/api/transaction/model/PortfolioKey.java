package com.bt.nextgen.api.transaction.model;

public class PortfolioKey
{
	private String clientId;
	private String portfolioId;

	
	public PortfolioKey()
	{
        super();
	}
	
	public PortfolioKey(String clientId, String portfolioId)
	{
		super();
		this.clientId = clientId;
		this.portfolioId = portfolioId;
	}

	public String getClientId()
	{
		return clientId;
	}

	public String getPortfolioId()
	{
		return portfolioId;
	}

	public void setClientId(String clientId)
	{
		this.clientId = clientId;
	}

	public void setPortfolioId(String portfolioId)
	{
		this.portfolioId = portfolioId;
	}

}
