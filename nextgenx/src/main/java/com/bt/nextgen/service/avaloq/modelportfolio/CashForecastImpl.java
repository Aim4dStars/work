package com.bt.nextgen.service.avaloq.modelportfolio;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import com.bt.nextgen.service.integration.modelportfolio.CashForecast;

public class CashForecastImpl implements CashForecast
{
	@NotNull
	private BigDecimal amountToday;

	@NotNull
	private BigDecimal amountTodayPlus1;

	@NotNull
	private BigDecimal amountTodayPlus2;

	@NotNull
	private BigDecimal amountTodayPlus3;

	@NotNull
	private BigDecimal amountTodayPlusMax;

	@Override
	public BigDecimal getAmountToday()
	{
		return amountToday;
	}

	@Override
	public BigDecimal getAmountTodayPlus1()
	{
		return amountTodayPlus1;
	}

	@Override
	public BigDecimal getAmountTodayPlus2()
	{
		return amountTodayPlus2;
	}

	@Override
	public BigDecimal getAmountTodayPlus3()
	{
		return amountTodayPlus3;
	}

	@Override
	public BigDecimal getAmountTodayPlusMax()
	{
		return amountTodayPlusMax;
	}

	public void setAmountToday(BigDecimal amountToday)
	{
		this.amountToday = amountToday;
	}

	public void setAmountTodayPlus1(BigDecimal amountTodayPlus1)
	{
		this.amountTodayPlus1 = amountTodayPlus1;
	}

	public void setAmountTodayPlus2(BigDecimal amountTodayPlus2)
	{
		this.amountTodayPlus2 = amountTodayPlus2;
	}

	public void setAmountTodayPlus3(BigDecimal amountTodayPlus3)
	{
		this.amountTodayPlus3 = amountTodayPlus3;
	}

	public void setAmountTodayPlusMax(BigDecimal amountTodayPlusMax)
	{
		this.amountTodayPlusMax = amountTodayPlusMax;
	}
}
