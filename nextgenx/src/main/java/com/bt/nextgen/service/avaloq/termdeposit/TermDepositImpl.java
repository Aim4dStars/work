package com.bt.nextgen.service.avaloq.termdeposit;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import com.bt.nextgen.service.integration.termdeposit.TermDeposit;

public class TermDepositImpl implements TermDeposit
{
	private String accountName;
	private String brandLogoUrl;
	private BigDecimal principalAmount;
	private DateTime maturityDate;
	private BigDecimal daysToMaturity;
	private String accountId;

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

	public DateTime getMaturityDate()
	{
		return maturityDate;
	}

	public void setMaturityDate(DateTime maturityDate)
	{
		this.maturityDate = maturityDate;
	}

	public BigDecimal getDaysToMaturity()
	{
		return daysToMaturity;
	}

	public void setDaysToMaturity(BigDecimal daysToMaturity)
	{
		this.daysToMaturity = daysToMaturity;
	}

	public String getAccountId()
	{
		return accountId;
	}

	public void setAccountId(String accountId)
	{
		this.accountId = accountId;
	}

}
