package com.bt.nextgen.reports.web.model;

public class IncomeTransactionModel
{

	private String acccountNumber;
	private String transactionDate;
	private String transactionDesc;
	private String transactionAmount;
	private String transactionType;
	
	private String assetIssurer;
	private String tenure;
	private String interestRate;
	private String maturityDate;

	public String getTransactionDate()
	{
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) 
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

	public String getTransactionAmount()
	{
		return transactionAmount;
	}

	public void setTransactionAmount(String transactionAmount)
	{
		this.transactionAmount = transactionAmount;
	}

	public String getAcccountNumber()
	{
		return acccountNumber;
	}

	public void setAcccountNumber(String acccountNumber)
	{
		this.acccountNumber = acccountNumber;
	}

	public String getTransactionType()
	{
		return transactionType;
	}

	public void setTransactionType(String transactionType)
	{
		this.transactionType = transactionType;
	}

	public String getAssetIssurer()
	{
		return assetIssurer;
	}

	public void setAssetIssurer(String assetIssurer)
	{
		this.assetIssurer = assetIssurer;
	}

	public String getTenure()
	{
		return tenure;
	}

	public void setTenure(String tenure)
	{
		this.tenure = tenure;
	}

	public String getInterestRate()
	{
		return interestRate;
	}

	public void setInterestRate(String interestRate)
	{
		this.interestRate = interestRate;
	}

	public String getMaturityDate()
	{
		return maturityDate;
	}

	public void setMaturityDate(String maturityDate)
	{
		this.maturityDate = maturityDate;
	}

}
