package com.bt.nextgen.termdeposit.web.model;

import java.math.BigDecimal;

public class TermDepositModel
{

	private String investmentAmount;
	private String daysLeft;
	private String maturityDate;
	private String count;
	private BigDecimal totalTermDeposit;

	public String getInvestmentAmount()
	{
		return investmentAmount;
	}

	public void setInvestmentAmount(String investmentAmount)
	{
		this.investmentAmount = investmentAmount;
	}

	public String getDaysLeft()
	{
		return daysLeft;
	}

	public void setDaysLeft(String daysLeft)
	{
		this.daysLeft = daysLeft;
	}

	public String getMaturityDate()
	{
		return maturityDate;
	}

	public void setMaturityDate(String maturityDate)
	{
		this.maturityDate = maturityDate;
	}

	public String getCount()
	{
		return count;
	}

	public void setCount(String count)
	{
		this.count = count;
	}

	public BigDecimal getTotalTermDeposit()
	{
		return totalTermDeposit;
	}

	public void setTotalTermDeposit(BigDecimal totalTermDeposit)
	{
		this.totalTermDeposit = totalTermDeposit;
	}

	@Override
	public String toString()
	{
		return "TermDepositModel [investmentAmount=" + investmentAmount + ", daysLeft=" + daysLeft + ", maturityDate="
			+ maturityDate + ", count=" + count + ", portfolioAmount=" + totalTermDeposit + "]";
	}

}
