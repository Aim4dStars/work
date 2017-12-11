package com.bt.nextgen.service.integration.termdeposit;

import java.math.BigDecimal;

import org.joda.time.DateTime;

public interface TermDeposit
{
	public String getAccountName();

	public void setAccountName(String accountName);

	public String getBrandLogoUrl();

	public void setBrandLogoUrl(String brandLogoUrl);

	public BigDecimal getPrincipalAmount();

	public void setPrincipalAmount(BigDecimal principalAmount);

	public DateTime getMaturityDate();

	public void setMaturityDate(DateTime maturityDate);

	public BigDecimal getDaysToMaturity();

	public void setDaysToMaturity(BigDecimal daysToMaturity);

	//TODO change this to be Account Key rather than a String
	public String getAccountId();

	//TODO change this to be Account Key rather than a String
	public void setAccountId(String accountId);

}
