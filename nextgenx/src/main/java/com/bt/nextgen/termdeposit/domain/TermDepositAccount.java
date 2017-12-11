package com.bt.nextgen.termdeposit.domain;

import com.bt.nextgen.reports.domain.IncomeTransaction;

import java.io.Serializable;
import java.util.List;

public class TermDepositAccount implements Serializable
{
	private String accountName;
	private String bsb;
	private String accountNumber;
	private String narrative;
	private String brandName;
	private String brandLogoUrl;
	private String brandClass;
	private String status;
	private String interestRate;
	private String termDuration;
	private String paymentSelection;
	private String cashDepositedDate;
	private String maturityDate;
	private String interestPaid;
	private String investmentAmount;
	private String setUpBy;
	private String setUpDate;
	private String intereseAccured;
	private String nextInterestPaymentDate;
	private String totalInterest;
	private String withHoldingTaxDeducted;
	private String maturityInstructionAmount;
	private String matrurityIntructionDate;
	private String lastUpdatedDate;
	private String lastUpdatedBy;
	private String withdrawnDate;
	private String totalAmount;
	private String withdrawnInterestRate;
	private String dayLeft;
	/**
	 * US-568. List of transactions for a TermDeposit.
	 */
	private List <IncomeTransaction> termDepositsTransactions;

	public String getDayLeft()
	{
		return dayLeft;
	}

	public void setDayLeft(String dayLeft)
	{
		this.dayLeft = dayLeft;
	}

	public TermDepositAccount()
	{

	}

	public TermDepositAccount(String accountName, String bsb, String accountNumber)
	{
		this.accountName = accountName;
		this.bsb = bsb;
		this.accountNumber = accountNumber;
	}

	public String getAccountName()
	{
		return accountName;
	}

	public void setAccountName(String accountName)
	{
		this.accountName = accountName;
	}

	public String getBsb()
	{
		return bsb;
	}

	public void setBsb(String bsb)
	{
		this.bsb = bsb;
	}

	public String getAccountNumber()
	{
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber)
	{
		this.accountNumber = accountNumber;
	}

	public String getNarrative()
	{
		return narrative;
	}

	public void setNarrative(String narrative)
	{
		this.narrative = narrative;
	}

	public String getBrandName()
	{
		return brandName;
	}

	public void setBrandName(String brandName)
	{
		this.brandName = brandName;
	}

	public String getBrandLogoUrl()
	{
		return brandLogoUrl;
	}

	public void setBrandLogoUrl(String brandLogoUrl)
	{
		this.brandLogoUrl = brandLogoUrl;
	}

	public String getBrandClass()
	{
		return brandClass;
	}

	public void setBrandClass(String brandClass)
	{
		this.brandClass = brandClass;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getInterestRate()
	{
		return interestRate;
	}

	public void setInterestRate(String interestRate)
	{
		this.interestRate = interestRate;
	}

	public String getTermDuration()
	{
		return termDuration;
	}

	public void setTermDuration(String termDuration)
	{
		this.termDuration = termDuration;
	}

	public String getPaymentSelection()
	{
		return paymentSelection;
	}

	public void setPaymentSelection(String paymentSelection)
	{
		this.paymentSelection = paymentSelection;
	}

	public String getCashDepositedDate()
	{
		return cashDepositedDate;
	}

	public void setCashDepositedDate(String cashDepositedDate)
	{
		this.cashDepositedDate = cashDepositedDate;
	}

	public String getMaturityDate()
	{
		return maturityDate;
	}

	public void setMaturityDate(String maturityDate)
	{
		this.maturityDate = maturityDate;
	}

	public String getInterestPaid()
	{
		return interestPaid;
	}

	public void setInterestPaid(String interestPaid)
	{
		this.interestPaid = interestPaid;
	}

	public String getInvestmentAmount()
	{
		return investmentAmount;
	}

	public void setInvestmentAmount(String investmentAmount)
	{
		this.investmentAmount = investmentAmount;
	}

	public String getSetUpBy()
	{
		return setUpBy;
	}

	public void setSetUpBy(String setUpBy)
	{
		this.setUpBy = setUpBy;
	}

	public String getSetUpDate()
	{
		return setUpDate;
	}

	public void setSetUpDate(String setUpDate)
	{
		this.setUpDate = setUpDate;
	}

	public String getIntereseAccured()
	{
		return intereseAccured;
	}

	public void setIntereseAccured(String intereseAccured)
	{
		this.intereseAccured = intereseAccured;
	}

	public String getNextInterestPaymentDate()
	{
		return nextInterestPaymentDate;
	}

	public void setNextInterestPaymentDate(String nextInterestPaymentDate)
	{
		this.nextInterestPaymentDate = nextInterestPaymentDate;
	}

	public String getTotalInterest()
	{
		return totalInterest;
	}

	public void setTotalInterest(String totalInterest)
	{
		this.totalInterest = totalInterest;
	}

	public String getWithHoldingTaxDeducted()
	{
		return withHoldingTaxDeducted;
	}

	public void setWithHoldingTaxDeducted(String withHoldingTaxDeducted)
	{
		this.withHoldingTaxDeducted = withHoldingTaxDeducted;
	}

	public String getMaturityInstructionAmount()
	{
		return maturityInstructionAmount;
	}

	public void setMaturityInstructionAmount(String maturityInstructionAmount)
	{
		this.maturityInstructionAmount = maturityInstructionAmount;
	}

	public String getMatrurityIntructionDate()
	{
		return matrurityIntructionDate;
	}

	public void setMatrurityIntructionDate(String matrurityIntructionDate)
	{
		this.matrurityIntructionDate = matrurityIntructionDate;
	}

	public String getLastUpdatedDate()
	{
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(String lastUpdatedDate)
	{
		this.lastUpdatedDate = lastUpdatedDate;
	}

	public String getLastUpdatedBy()
	{
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy)
	{
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public String getWithdrawnDate()
	{
		return withdrawnDate;
	}

	public void setWithdrawnDate(String withdrawnDate)
	{
		this.withdrawnDate = withdrawnDate;
	}

	public String getTotalAmount()
	{
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount)
	{
		this.totalAmount = totalAmount;
	}

	public String getWithdrawnInterestRate()
	{
		return withdrawnInterestRate;
	}

	public void setWithdrawnInterestRate(String withdrawnInterestRate)
	{
		this.withdrawnInterestRate = withdrawnInterestRate;
	}

	public List <IncomeTransaction> getTermDepositsTransactions()
	{
		return termDepositsTransactions;
	}

	public void setTermDepositsTransactions(List <IncomeTransaction> termDepositsTransactions)
	{
		this.termDepositsTransactions = termDepositsTransactions;
	}

}
