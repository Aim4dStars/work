package com.bt.nextgen.service.avaloq.dashboard;

import java.math.BigDecimal;

import com.bt.nextgen.service.integration.dashboard.TopAccountsByValue;

public class TopAccountsByValueImpl implements TopAccountsByValue
{
	private String accountId;

	private BigDecimal cashValue;
	private BigDecimal portfolioValue;
	private String orderBy;

	public String getAccountId()
	{
		return accountId;
	}

	public void setAccountId(String accountId)
	{
		this.accountId = accountId;
	}

	public BigDecimal getCashValue()
	{
		return cashValue;
	}

	public void setCashValue(BigDecimal cashValue)
	{
		this.cashValue = cashValue;
	}

	public BigDecimal getPortfolioValue()
	{
		return portfolioValue;
	}

	public void setPortfolioValue(BigDecimal portfolioValue)
	{
		this.portfolioValue = portfolioValue;
	}

	public String getOrderBy()
	{
		return orderBy;
	}

	public void setOrderBy(String orderBy)
	{
		this.orderBy = orderBy;
	}

}
