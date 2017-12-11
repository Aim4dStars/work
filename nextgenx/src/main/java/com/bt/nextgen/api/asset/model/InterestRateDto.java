package com.bt.nextgen.api.asset.model;

import java.math.BigDecimal;

public class InterestRateDto
{
	private BigDecimal rate;
	private BigDecimal lowerLimit;
	private BigDecimal upperLimit;

	public InterestRateDto(BigDecimal rate, BigDecimal lowerLimit, BigDecimal upperLimit)
	{
		super();
		this.rate = rate;
		this.lowerLimit = lowerLimit;
		this.upperLimit = upperLimit;
	}

	public BigDecimal getRate()
	{
		return rate;
	}

	public BigDecimal getLowerLimit()
	{
		return lowerLimit;
	}

	public BigDecimal getUpperLimit()
	{
		return upperLimit;
	}
}
