package com.bt.nextgen.reports.domain;

import java.math.BigDecimal;
import java.util.Date;

public class IncomeTransaction 
{

	private String acccountId;
	private Date transactionDate;
	private String transactionDesc;
	private BigDecimal transactionAmount;
	private String transactionType;

	public String getAcccountId()
	{
		return acccountId;
	}

	public void setAcccountId(String acccountId)
	{
		this.acccountId = acccountId;
	}

	public Date getTransactionDate()
	{
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate)
	{
		this.transactionDate = transactionDate;
	}

	public String getTransactionDesc()
	{
		return transactionDesc;
	}

	public void setTransactionDesc(String transactionDesc)
	{
		this.transactionDesc = transactionDesc;
	}

	public BigDecimal getTransactionAmount()
	{
		return transactionAmount;
	}

	public void setTransactionAmount(BigDecimal transactionAmount)
	{
		this.transactionAmount = transactionAmount;
	}

	public String getTransactionType()
	{
		return transactionType;
	}

	public void setTransactionType(String transactionType)
	{
		this.transactionType = transactionType;
	}

/*	@Override
	public int compareTo(IncomeTransaction incomeTransaction)
	{
		//return transactionDate.compareTo(incomeTransaction.transactionDate);
		return this.getTransactionDate().compareTo(incomeTransaction.getTransactionDate());
	}*/
}
