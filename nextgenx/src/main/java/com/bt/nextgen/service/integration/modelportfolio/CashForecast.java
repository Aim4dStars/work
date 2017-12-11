package com.bt.nextgen.service.integration.modelportfolio;

import java.math.BigDecimal;

public interface CashForecast
{
	public BigDecimal getAmountToday();

	public BigDecimal getAmountTodayPlus1();

	public BigDecimal getAmountTodayPlus2();

	public BigDecimal getAmountTodayPlus3();

	public BigDecimal getAmountTodayPlusMax();
}