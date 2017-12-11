package com.bt.nextgen.reports.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "IncomeExpenseReport")
@XmlAccessorType(XmlAccessType.FIELD)
public class IncomeExpenseReportDomain
{
	@XmlElement
	private String openingBalance;
	@XmlElement
	private String withdrawalBalance;
	@XmlElement
	private String depositBalance;
	@XmlElement
	private String interestReceived;
	@XmlElement
	private String DistributionsReceived;
	@XmlElement
	private String feesAndCharges;
	@XmlElement
	private String taxes;
	@XmlElement
	private String closingBalance;

	public String getOpeningBalance()
	{
		return openingBalance;
	}

	public void setOpeningBalance(String openingBalance)
	{
		this.openingBalance = openingBalance;
	}

	public String getWithdrawalBalance()
	{
		return withdrawalBalance;
	}

	public void setWithdrawalBalance(String withdrawalBalance)
	{
		this.withdrawalBalance = withdrawalBalance;
	}

	public String getDepositBalance()
	{
		return depositBalance;
	}

	public void setDepositBalance(String depositBalance)
	{
		this.depositBalance = depositBalance;
	}

	public String getInterestReceived()
	{
		return interestReceived;
	}

	public void setInterestReceived(String interestReceived)
	{
		this.interestReceived = interestReceived;
	}

	public String getDistributionsReceived()
	{
		return DistributionsReceived;
	}

	public void setDistributionsReceived(String distributionsReceived)
	{
		DistributionsReceived = distributionsReceived;
	}

	public String getFeesAndCharges()
	{
		return feesAndCharges;
	}

	public void setFeesAndCharges(String feesAndCharges)
	{
		this.feesAndCharges = feesAndCharges;
	}

	public String getTaxes()
	{
		return taxes;
	}

	public void setTaxes(String taxes)
	{
		this.taxes = taxes;
	}

	public String getClosingBalance()
	{
		return closingBalance;
	}

	public void setClosingBalance(String closingBalance)
	{
		this.closingBalance = closingBalance;
	}

}
