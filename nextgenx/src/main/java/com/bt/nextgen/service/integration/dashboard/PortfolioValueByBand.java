package com.bt.nextgen.service.integration.dashboard;

import java.math.BigDecimal;

import org.joda.time.DateTime;

public interface PortfolioValueByBand
{

	public DateTime getPeriodSop();

	public DateTime getPeriodEop();

	public BigDecimal getPortfolioValue();

	public BigDecimal getAveragePortfolioValue();

	public BigDecimal getMedianPortfolioValue();

	public BigDecimal getBandOneAccounts();

	public BigDecimal getBandOnePortfolioValue();

	public BigDecimal getBandTwoAccounts();

	public BigDecimal getBandTwoPortfolioValue();

	public BigDecimal getBandThreeAccounts();

	public BigDecimal getBandThreePortfolioValue();

	public BigDecimal getBandFourAccounts();

	public BigDecimal getBandFourPortfolioValue();

	public BigDecimal getBandFiveAccounts();

	public BigDecimal getBandFivePortfolioValue();

	public BigDecimal getBandSixAccounts();

	public BigDecimal getBandSixPortfolioValue();
}
