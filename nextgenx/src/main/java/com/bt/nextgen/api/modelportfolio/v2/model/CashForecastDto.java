package com.bt.nextgen.api.modelportfolio.v2.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.math.BigDecimal;

public class CashForecastDto extends BaseDto
{
	private final BigDecimal amountToday;
	private final BigDecimal amountTodayPlus1;
	private final BigDecimal amountTodayPlus2;
	private final BigDecimal amountTodayPlus3;
	private final BigDecimal amountTodayPlusMax;

	public CashForecastDto(BigDecimal amountToday, BigDecimal amountTodayPlus1, BigDecimal amountTodayPlus2,
		BigDecimal amountTodayPlus3, BigDecimal amountTodayPlusMax)
	{
		super();
		this.amountToday = amountToday;
		this.amountTodayPlus1 = amountTodayPlus1;
		this.amountTodayPlus2 = amountTodayPlus2;
		this.amountTodayPlus3 = amountTodayPlus3;
		this.amountTodayPlusMax = amountTodayPlusMax;
	}

	public BigDecimal getAmountToday()
	{
		return amountToday;
	}

	public BigDecimal getAmountTodayPlus1()
	{
		return amountTodayPlus1;
	}

	public BigDecimal getAmountTodayPlus2()
	{
		return amountTodayPlus2;
	}

	public BigDecimal getAmountTodayPlus3()
	{
		return amountTodayPlus3;
	}

	public BigDecimal getAmountTodayPlusMax()
	{
		return amountTodayPlusMax;
	}
}
