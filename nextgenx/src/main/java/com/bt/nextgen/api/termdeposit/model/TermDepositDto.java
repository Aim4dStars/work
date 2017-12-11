package com.bt.nextgen.api.termdeposit.model;

import com.bt.nextgen.api.account.v1.model.AccountKey;

import java.math.BigDecimal;

import com.bt.nextgen.core.api.model.BaseDto;

public class TermDepositDto extends BaseDto
{
	private String accountName;
	private String brandLogoUrl;
	private BigDecimal principalAmount;
	private String maturityDate;
	private BigDecimal daysToMaturity;
	private AccountKey accountKey;

	public BigDecimal getDaysToMaturity()
	{
		return daysToMaturity;
	}

	public void setDaysToMaturity(BigDecimal daysToMaturity)
	{
		this.daysToMaturity = daysToMaturity;
	}

	public String getAccountName()
	{
		return accountName;
	}

	public void setAccountName(String accountName)
	{
		this.accountName = accountName;
	}

	public String getBrandLogoUrl()
	{
		return brandLogoUrl;
	}

	public void setBrandLogoUrl(String brandLogoUrl)
	{
		this.brandLogoUrl = brandLogoUrl;
	}

	public BigDecimal getPrincipalAmount()
	{
		return principalAmount;
	}

	public void setPrincipalAmount(BigDecimal principalAmount)
	{
		this.principalAmount = principalAmount;
	}

	public String getMaturityDate()
	{
		return maturityDate;
	}

	public void setMaturityDate(String maturityDate)
	{
		this.maturityDate = maturityDate;
	}

	public AccountKey getAccountKey()
	{
		return accountKey;
	}

	public void setAccountKey(AccountKey accountKey)
	{
		this.accountKey = accountKey;
	}
}
