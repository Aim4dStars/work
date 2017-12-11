package com.bt.nextgen.api.dashboard.model;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;

import com.bt.nextgen.core.api.model.BaseDto;

public class FUAByPortfolioBandDto extends BaseDto
{

	private DateTime startDate;
	private DateTime endDate;
	private BigDecimal averagePortfolioValue;
	private BigDecimal medianPortfolioValue;
	private BigDecimal totalPortfolioValue;
	private List <PortfolioBandDto> portfolioBands;

	public FUAByPortfolioBandDto(DateTime startDate, DateTime endDate, BigDecimal averagePortfolioValue,
		BigDecimal medianPortfolioValue, BigDecimal totalPortfolioValue, List <PortfolioBandDto> portfolioBands)
	{
		this.startDate = startDate;
		this.endDate = endDate;
		this.averagePortfolioValue = averagePortfolioValue;
		this.medianPortfolioValue = medianPortfolioValue;
		this.totalPortfolioValue = totalPortfolioValue;
		this.portfolioBands = portfolioBands;
	}

	public DateTime getStartDate()
	{
		return startDate;
	}

	public DateTime getEndDate()
	{
		return endDate;
	}

	public BigDecimal getAveragePortfolioValue()
	{
		return averagePortfolioValue;
	}

	public BigDecimal getMedianPortfolioValue()
	{
		return medianPortfolioValue;
	}

	public BigDecimal getTotalPortfolioValue()
	{
		return totalPortfolioValue;
	}

	public List <PortfolioBandDto> getPortfolioBands()
	{
		return portfolioBands;
	}

}
