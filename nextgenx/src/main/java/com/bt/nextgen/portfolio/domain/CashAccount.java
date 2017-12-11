package com.bt.nextgen.portfolio.domain;

import com.bt.nextgen.reports.domain.IncomeTransaction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Deprecated
public class CashAccount implements Serializable
{
	private String accountName;
	private String bsb;
	private String accountNumber;
	private BigDecimal availableBalance = new BigDecimal(6000);
	private String billerCode;
	private String crn;
	private String interestRate;
	private BigDecimal totalBalance;
	private BigDecimal interestEarned;
	/**
	 * US-568. List of transactions for a cashAccount.
	 */
	private List <IncomeTransaction> cashTransactions;

	public CashAccount()
	{

	}

	public CashAccount(String accountName, String bsb, String accountNumber)
	{
		this.accountName = accountName;
		this.bsb = bsb;
		this.accountNumber = accountNumber;
	}

	public String getAccountName()
	{
		return accountName;
	}

	public String getBsb()
	{
		return bsb;
	}

	public String getAccountNumber()
	{
		return accountNumber;
	}

	public BigDecimal getAvailableBalance()
	{
		return availableBalance;
	}

	public void setAvailableBalance(BigDecimal availableBalance)
	{
		this.availableBalance = availableBalance;
	}

	public String getBillerCode()
	{
		return billerCode;
	}

	public void setBillerCode(String billerCode)
	{
		this.billerCode = billerCode;
	}

	public String getCrn()
	{
		return crn;
	}

	public void setCrn(String crn)
	{
		this.crn = crn;
	}

	public String getInterestRate()
	{
		return interestRate;
	}

	public void setInterestRate(String interestRate)
	{
		this.interestRate = interestRate;
	}

	public BigDecimal getTotalBalance()
	{
		return totalBalance;
	}

	public void setTotalBalance(BigDecimal totalBalance)
	{
		this.totalBalance = totalBalance;
	}

	public BigDecimal getInterestEarned()
	{
		return interestEarned;
	}

	public void setInterestEarned(BigDecimal interestEarned)
	{
		this.interestEarned = interestEarned;
	}

	public void setAccountName(String accountName)
	{
		this.accountName = accountName;
	}

	public void setBsb(String bsb)
	{
		this.bsb = bsb;
	}

	public void setAccountNumber(String accountNumber)
	{
		this.accountNumber = accountNumber;
	}

	public List <IncomeTransaction> getCashTransactions()
	{
		return cashTransactions;
	}

	public void setCashTransactions(List <IncomeTransaction> cashTransactions)
	{
		this.cashTransactions = cashTransactions;
	}
}
