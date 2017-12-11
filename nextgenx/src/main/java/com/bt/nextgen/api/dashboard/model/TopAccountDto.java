package com.bt.nextgen.api.dashboard.model;

import java.math.BigDecimal;

import com.bt.nextgen.service.integration.account.AccountKey;

public class TopAccountDto
{
	private AccountKey encodedAccountKey;
	private String accountName;
	private BigDecimal cashBalance;
	private BigDecimal portfolioValue;

	public TopAccountDto(AccountKey encodedAccountKey, String accountName, BigDecimal cashBalance, BigDecimal portfolioValue)
	{
		this.encodedAccountKey = encodedAccountKey;
		this.accountName = accountName;
		this.cashBalance = cashBalance;
		this.portfolioValue = portfolioValue;
	}

	public AccountKey getEncodedAccountKey()
	{
		return encodedAccountKey;
	}

	public String getAccountName()
	{
		return accountName;
	}

	public BigDecimal getCashBalance()
	{
		return cashBalance;
	}

	public BigDecimal getPortfolioValue()
	{
		return portfolioValue;
	}
}
