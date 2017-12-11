package com.bt.nextgen.api.fees.model;

import java.math.BigDecimal;

import com.bt.nextgen.core.api.model.BaseDto;

public class SlidingScaleFeeTierDto extends BaseDto
{
	private BigDecimal lowerBound;
	private BigDecimal upperBound;
	private BigDecimal percentage;

	public BigDecimal getLowerBound()
	{
		return lowerBound;
	}

	public void setLowerBound(BigDecimal lowerBound)
	{
		this.lowerBound = lowerBound;
	}

	public BigDecimal getUpperBound()
	{
		return upperBound;
	}

	public void setUpperBound(BigDecimal upperBound)
	{
		this.upperBound = upperBound;
	}

	public BigDecimal getPercentage()
	{
		return percentage;
	}

	public void setPercentage(BigDecimal percentage)
	{
		this.percentage = percentage;
	}

}
