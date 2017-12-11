package com.bt.nextgen.termdeposit.web.model;

public class TermDepositRateModel
{
	private String interestPerTerm;
	private boolean bestRateFlag = false;
	private boolean highestRateFlag = false;
	private String termDepositMonthlyIterest;
	private String interestRatePerMonth;
	private String interestRatePerYear;
	private String totalInterestEarnedMonthly;
	private String totalInterestEarnedYearly;
	private String maturityValueYearly;
	private String maturityValueMonthly;
	private String productInformation;
	private String maturityId;
	private String yearlyId;
	private String monthlyId;

	public void setInterestPerTerm(String interestPerTerm)
	{
		this.interestPerTerm = interestPerTerm;
	}

	public void setTermDepositMonthlyIterest(String termDepositMonthlyIterest)
	{
		this.termDepositMonthlyIterest = termDepositMonthlyIterest;
	}

	public String getInterestRatePerMonth()
	{
		return interestRatePerMonth;
	}

	public String getMaturityId()
	{
		return maturityId;
	}

	public void setMaturityId(String maturityId)
	{
		this.maturityId = maturityId;
	}

	public String getYearlyId()
	{
		return yearlyId;
	}

	public void setYearlyId(String yearlyId)
	{
		this.yearlyId = yearlyId;
	}

	public String getMonthlyId()
	{
		return monthlyId;
	}

	public void setMonthlyId(String monthlyId)
	{
		this.monthlyId = monthlyId;
	}

	public void setInterestRatePerMonth(String interestRatePerMonth)
	{
		this.interestRatePerMonth = interestRatePerMonth;
	}

	public String getInterestRatePerYear()
	{
		return interestRatePerYear;
	}

	public void setInterestRatePerYear(String interestRatePerYear)
	{
		this.interestRatePerYear = interestRatePerYear;
	}

	public String getTotalInterestEarnedMonthly()
	{
		return totalInterestEarnedMonthly;
	}

	public void setTotalInterestEarnedMonthly(String totalInterestEarnedMonthly)
	{
		this.totalInterestEarnedMonthly = totalInterestEarnedMonthly;
	}

	public String getTotalInterestEarnedYearly()
	{
		return totalInterestEarnedYearly;
	}

	public void setTotalInterestEarnedYearly(String totalInterestEarnedYearly)
	{
		this.totalInterestEarnedYearly = totalInterestEarnedYearly;
	}

	public String getMaturityValueYearly()
	{
		return maturityValueYearly;
	}

	public void setMaturityValueYearly(String maturityValueYearly)
	{
		this.maturityValueYearly = maturityValueYearly;
	}

	public String getMaturityValueMonthly()
	{
		return maturityValueMonthly;
	}

	public void setMaturityValueMonthly(String maturityValueMonthly)
	{
		this.maturityValueMonthly = maturityValueMonthly;
	}

	public String getProductInformation()
	{
		return productInformation;
	}

	public void setProductInformation(String productInformation)
	{
		this.productInformation = productInformation;
	}

	public String getInterestPerTerm()
	{
		return interestPerTerm;
	}

	public boolean isBestRateFlag()
	{
		return bestRateFlag;
	}

	public void setBestRateFlag(boolean bestRateFlag)
	{
		this.bestRateFlag = bestRateFlag;
	}

	public boolean isHighestRateFlag()
	{
		return highestRateFlag;
	}

	public void setHighestRateFlag(boolean highestRateFlag)
	{
		this.highestRateFlag = highestRateFlag;
	}

	public String getTermDepositMonthlyIterest()
	{
		return termDepositMonthlyIterest;
	}
}
