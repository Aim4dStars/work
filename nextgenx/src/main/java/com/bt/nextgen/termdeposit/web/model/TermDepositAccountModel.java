package com.bt.nextgen.termdeposit.web.model;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.bt.nextgen.core.domain.BaseObject;

public class TermDepositAccountModel extends BaseObject
{
	private static final long serialVersionUID = 2922487292465960738L;
	//brand detailse

	//compact view of TD
	private String id;
	private String internalBrandId;
	private String internalBrandName;
	private String brandName;
	private String brandClass;
	private String interestRate;
	private String termDuration;
	private String paymentSelection;
	private String investmentAmount;
	private String daysLeft;
	private String maturityDate;
	private String status;
	private String interestPaid;
	private String withdrawnDate;
	private String setupBy;
	private String setupDate;
	private String lastUpdatedBy;
	private String lastUpdatedDate;
	private String lastPaymentDate;
	private boolean withHoldingTaxZero;
	private String withdrawalTotalAmount;
	private boolean interestPaidZero;
	private String tdPaymentSelection;

	public String getTdPaymentSelection()
	{
		return tdPaymentSelection;
	}

	public void setTdPaymentSelection(String tdPaymentSelection)
	{
		this.tdPaymentSelection = tdPaymentSelection;
	}

	//open,withdrawn,matured
	private String totalInterest;

	// open, matured
	private String withholdingTaxDeducted;

	// withdrawn, matured
	private String totalAmount;

	//Status = Open

	private String interestAccrued;
	private String nextInterestPaymentDate;
	private String termsAndConditionDescription;

	private String maturityInstructionDate;
	private String maturityInstruction;
	private String maturityInstructionAmount;

	//Status = Withdrawn,pending
	private String withdrawnInterestRate;

	//Status = Matured
	private String cashDepositedDate;

	//cash account details
	private String accountName;
	private String accountNumber;
	private String accountType;

	//term deposit account details
	private String tdAccountName;
	private String tdAccountNumber;
	private String narrative;
	private String principal;

	//adviser Details for TDReport
	private String adviserId;
	private String adviserName;
	private String adviserState;

	private String interestFrequency;
	private String percentageofTermCompleted;
	private String breakDate;
	private String clientId;
	private String portfolioId;
	private String investmentId;
	private String balanceOnMaturity;
	private boolean dateChangeable;

	private String descriptionLine1;
	private String descriptionLine2;

	public String getDescriptionLine1()
	{
		return descriptionLine1;
	}

	public void setDescriptionLine1(String descriptionLine1)
	{
		this.descriptionLine1 = descriptionLine1;
	}

	public String getDescriptionLine2()
	{
		return descriptionLine2;
	}

	public void setDescriptionLine2(String descriptionLine2)
	{
		this.descriptionLine2 = descriptionLine2;
	}

	public String getBreakDate()
	{
		return breakDate;
	}

	public void setBreakDate(String breakDate)
	{
		this.breakDate = breakDate;
	}

	public String getInterestFrequency()
	{
		return interestFrequency;
	}

	public void setInterestFrequency(String interestFrequency)
	{
		this.interestFrequency = interestFrequency;
	}

	public String getPercentageofTermCompleted()
	{
		return percentageofTermCompleted;
	}

	public void setPercentageofTermCompleted(String percentageofTermCompleted)
	{
		this.percentageofTermCompleted = percentageofTermCompleted;
	}

	public String getAccountType()
	{
		return accountType;
	}

	public void setAccountType(String accountType)
	{
		this.accountType = accountType;
	}

	public boolean isWithHoldingTaxZero()
	{
		return withHoldingTaxZero;
	}

	public void setWithHoldingTaxZero(boolean withHoldingTaxZero)
	{
		this.withHoldingTaxZero = withHoldingTaxZero;
	}

	public boolean isInterestPaidZero()
	{
		return interestPaidZero;
	}

	public void setInterestPaidZero(boolean interestPaidZero)
	{
		this.interestPaidZero = interestPaidZero;
	}


	public String getInternalBrandName()
	{
		return internalBrandName;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public void setInternalBrandName(String internalBrandName)
	{
		this.internalBrandName = internalBrandName;
	}

	public String getBrandName()
	{
		return brandName;
	}

	public void setBrandName(String brandName)
	{
		this.brandName = brandName;
	}

	public String getBrandClass()
	{
		return brandClass;
	}

	public String getLastPaymentDate()
	{
		return lastPaymentDate;
	}

	public void setLastPaymentDate(String lastPaymentDate)
	{
		this.lastPaymentDate = lastPaymentDate;
	}

	public void setBrandClass(String brandClass)
	{
		this.brandClass = brandClass;
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

	public String getInterestAccrued()
	{
		return interestAccrued;
	}

	public void setInterestAccrued(String interestAccrued)
	{
		this.interestAccrued = interestAccrued;
	}

	public void setPaymentSelection(String paymentSelection)
	{
		this.paymentSelection = paymentSelection;
	}

	public String getInvestmentAmount()
	{
		return this.investmentAmount;
	}

	public void setInvestmentAmount(String investmentAmount)
	{
		this.investmentAmount = investmentAmount;
	}

	public String getTermsAndConditionDescription()
	{
		return termsAndConditionDescription;
	}

	public void setTermsAndConditionDescription(String termsAndConditionDescription)
	{
		this.termsAndConditionDescription = termsAndConditionDescription;
	}

	public String getAccountName()
	{
		return accountName;
	}

	public void setAccountName(String accountName)
	{
		this.accountName = accountName;
	}

	public String getAccountNumber()
	{
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber)
	{
		this.accountNumber = accountNumber;
	}

	public String getMaturityDate()
	{
		return maturityDate;
	}

	public void setMaturityDate(String maturityDate)
	{
		this.maturityDate = maturityDate;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getInterestPaid()
	{
		return this.interestPaid;
	}

	public void setInterestPaid(String interestPaid)
	{
		this.interestPaid = interestPaid;
	}

	public String getTotalInterest()
	{
		return this.totalInterest;
	}

	public void setTotalInterest(String totalInterest)
	{
		this.totalInterest = totalInterest;
	}

	public String getNextInterestPaymentDate()
	{
		return nextInterestPaymentDate;
	}

	public void setNextInterestPaymentDate(String nextInterestPaymentDate)
	{
		this.nextInterestPaymentDate = nextInterestPaymentDate;
	}

	public String getWithholdingTaxDeducted()
	{
		return this.withholdingTaxDeducted;
	}

	public void setWithholdingTaxDeducted(String withholdingTaxDeducted)
	{
		this.withholdingTaxDeducted = withholdingTaxDeducted;
	}

	public String getMaturityInstruction()
	{
		return maturityInstruction;
	}

	public void setMaturityInstruction(String maturityInstruction)
	{
		this.maturityInstruction = maturityInstruction;
	}

	public String getLastUpdatedDate()
	{
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(String lastUpdatedDate)
	{
		this.lastUpdatedDate = lastUpdatedDate;
	}

	public String getCashDepositedDate()
	{
		return cashDepositedDate;
	}

	public void setCashDepositedDate(String cashDepositedDate)
	{
		this.cashDepositedDate = cashDepositedDate;
	}

	public String getDaysLeft()
	{
		return daysLeft;
	}

	public void setDaysLeft(String daysLeft)
	{
		this.daysLeft = daysLeft;
	}

	public String getTotalAmount()
	{
		return this.totalAmount;
	}

	public void setTotalAmount(String totalAmount)
	{
		this.totalAmount = totalAmount;
	}

	public String getLastUpdatedBy()
	{
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy)
	{
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public String getTdAccountName()
	{
		return tdAccountName;
	}

	public void setTdAccountName(String tdAccountName)
	{
		this.tdAccountName = tdAccountName;
	}

	public String getTdAccountNumber()
	{
		return tdAccountNumber;
	}

	public void setTdAccountNumber(String tdAccountNumber)
	{
		this.tdAccountNumber = tdAccountNumber;
	}

	public String getNarrative()
	{
		return narrative;
	}

	public void setNarrative(String narrative)
	{
		this.narrative = narrative;
	}

	public String getWithdrawnDate()
	{
		return withdrawnDate;
	}

	public void setWithdrawnDate(String withdrawnDate)
	{
		this.withdrawnDate = withdrawnDate;
	}

	public String getSetupBy()
	{
		return setupBy;
	}

	public void setSetupBy(String setupBy)
	{
		this.setupBy = setupBy;
	}

	public String getSetupDate()
	{
		return setupDate;
	}

	public void setSetupDate(String setupDate)
	{
		this.setupDate = setupDate;
	}

	public String getMaturityInstructionDate()
	{
		return maturityInstructionDate;
	}

	public void setMaturityInstructionDate(String maturityInstructionDate)
	{
		this.maturityInstructionDate = maturityInstructionDate;
	}

	public String getMaturityInstructionAmount()
	{
		return this.maturityInstructionAmount;
	}

	public void setMaturityInstructionAmount(String maturityInstructionAmount)
	{
		this.maturityInstructionAmount = maturityInstructionAmount;
	}

	public String getWithdrawnInterestRate()
	{
		return withdrawnInterestRate;
	}

	public void setWithdrawnInterestRate(String withdrawnInterestRate)
	{
		this.withdrawnInterestRate = withdrawnInterestRate;
	}

	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this);
	}

	public String getInternalBrandId()
	{
		return internalBrandId;
	}

	public void setInternalBrandId(String internalBrandId)
	{
		this.internalBrandId = internalBrandId;
	}

	public String getAdviserId()
	{
		return adviserId;
	}

	public void setAdviserId(String adviserId)
	{
		this.adviserId = adviserId;
	}

	public String getAdviserName()
	{
		return adviserName;
	}

	public void setAdviserName(String adviserName)
	{
		this.adviserName = adviserName;
	}

	public String getAdviserState()
	{
		return adviserState;
	}

	public void setAdviserState(String adviserState)
	{
		this.adviserState = adviserState;
	}

	public String getClientId()
	{
		return clientId;
	}

	public String getInvestmentId()
	{
		return investmentId;
	}

	public void setInvestmentId(String investmentId)
	{
		this.investmentId = investmentId;
	}

	public String getBalanceOnMaturity()
	{
		return balanceOnMaturity;
	}

	public void setBalanceOnMaturity(String balanceOnMaturity)
	{
		this.balanceOnMaturity = balanceOnMaturity;
	}

	public void setClientId(String clientId)
	{
		this.clientId = clientId;
	}

	public String getPortfolioId()
	{
		return portfolioId;
	}

	public void setPortfolioId(String portfolioId)
	{
		this.portfolioId = portfolioId;
	}

	public String getPrincipal()
	{
		return principal;
	}

	public void setPrincipal(String principal)
	{
		this.principal = principal;
	}

	public String getWithdrawalTotalAmount()
	{
		return withdrawalTotalAmount;
	}

	public void setWithdrawalTotalAmount(String withdrawalTotalAmount)
	{
		this.withdrawalTotalAmount = withdrawalTotalAmount;
	}

	public boolean isDateChangeable()
	{
		return dateChangeable;
	}

	public void setDateChangeable(boolean dateChangeable)
	{
		this.dateChangeable = dateChangeable;
	}
}
