package com.bt.nextgen.api.tdmaturities.model;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import com.bt.nextgen.core.api.model.BaseDto;

public class TDMaturityItemDto extends BaseDto
{
	private String brandId;
	private String brandName;
	private DateTime closeDate;
	private int daysToMaturity;
	private String interestFrequency;
	private String interestRate;
	private DateTime maturityDate;
	private String maturityInstruction;
	private DateTime openDate;
	private BigDecimal principalValue;
	private String termMonth;
	private String termYear;
	private String status;

	public TDMaturityItemDto(String brandId, String brandName, DateTime closeDate, int daysToMaturity, String interestFrequency,
		String interestRate, DateTime maturityDate, String maturityInstruction, DateTime openDate, BigDecimal principalValue,
		String termMonth, String termYear, String status)
	{
		super();
		this.brandId = brandId;
		this.brandName = brandName;
		this.closeDate = closeDate;
		this.daysToMaturity = daysToMaturity;
		this.interestFrequency = interestFrequency;
		this.interestRate = interestRate;
		this.maturityDate = maturityDate;
		this.maturityInstruction = maturityInstruction;
		this.openDate = openDate;
		this.principalValue = principalValue;
		this.termMonth = termMonth;
		this.termYear = termYear;
		this.status = status;
	}

	public String getBrandId()
	{
		return brandId;
	}

	public void setBrandId(String brandId)
	{
		this.brandId = brandId;
	}

	public String getBrandName()
	{
		return brandName;
	}

	public void setBrandName(String brandName)
	{
		this.brandName = brandName;
	}

	public DateTime getCloseDate()
	{
		return closeDate;
	}

	public void setCloseDate(DateTime closeDate)
	{
		this.closeDate = closeDate;
	}

	public int getDaysToMaturity()
	{
		return daysToMaturity;
	}

	public void setDaysToMaturity(int daysToMaturity)
	{
		this.daysToMaturity = daysToMaturity;
	}

	public String getInterestFrequency()
	{
		return interestFrequency;
	}

	public void setInterestFrequency(String interestFrequency)
	{
		this.interestFrequency = interestFrequency;
	}

	public String getInterestRate()
	{
		return interestRate;
	}

	public void setInterestRate(String interestRate)
	{
		this.interestRate = interestRate;
	}

	public DateTime getMaturityDate()
	{
		return maturityDate;
	}

	public void setMaturityDate(DateTime maturityDate)
	{
		this.maturityDate = maturityDate;
	}

	public String getMaturityInstruction()
	{
		return maturityInstruction;
	}

	public void setMaturityInstruction(String maturityInstruction)
	{
		this.maturityInstruction = maturityInstruction;
	}

	public DateTime getOpenDate()
	{
		return openDate;
	}

	public void setOpenDate(DateTime openDate)
	{
		this.openDate = openDate;
	}

	public BigDecimal getPrincipalValue()
	{
		return principalValue;
	}

	public void setPrincipalValue(BigDecimal principalValue)
	{
		this.principalValue = principalValue;
	}

	public String getTermMonth()
	{
		return termMonth;
	}

	public void setTermMonth(String termMonth)
	{
		this.termMonth = termMonth;
	}

	public String getTermYear()
	{
		return termYear;
	}

	public void setTermYear(String termYear)
	{
		this.termYear = termYear;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}
}
