package com.bt.nextgen.service.avaloq.dashboard;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import com.bt.nextgen.service.integration.dashboard.PortfolioValueByBand;

public class PortfolioValueByBandImpl implements PortfolioValueByBand
{

	private DateTime periodSop;
	private DateTime periodEop;
	private BigDecimal portfolioValue;
	private BigDecimal averagePortfolioValue;
	private BigDecimal medianPortfolioValue;
	private BigDecimal bandOneAccounts;
	private BigDecimal bandOnePortfolioValue;
	private BigDecimal bandTwoAccounts;
	private BigDecimal bandTwoPortfolioValue;
	private BigDecimal bandThreeAccounts;
	private BigDecimal bandThreePortfolioValue;
	private BigDecimal bandFourAccounts;
	private BigDecimal bandFourPortfolioValue;
	private BigDecimal bandFiveAccounts;
	private BigDecimal bandFivePortfolioValue;
	private BigDecimal bandSixAccounts;
	private BigDecimal bandSixPortfolioValue;

	@Override
	public DateTime getPeriodSop()
	{
		return periodSop;
	}

	public void setPeriodSop(DateTime periodSop)
	{
		this.periodSop = periodSop;
	}

	@Override
	public DateTime getPeriodEop()
	{
		return periodEop;
	}

	public void setPeriodEop(DateTime periodEop)
	{
		this.periodEop = periodEop;
	}

	@Override
	public BigDecimal getPortfolioValue()
	{
		return portfolioValue;
	}

	public void setPortfolioValue(BigDecimal portfolioValue)
	{
		this.portfolioValue = portfolioValue;
	}

	@Override
	public BigDecimal getAveragePortfolioValue()
	{
		return averagePortfolioValue;
	}

	public void setAveragePortfolioValue(BigDecimal averagePortfolioValue)
	{
		this.averagePortfolioValue = averagePortfolioValue;
	}

	@Override
	public BigDecimal getMedianPortfolioValue()
	{
		return medianPortfolioValue;
	}

	public void setMedianPortfolioValue(BigDecimal medianPortfolioValue)
	{
		this.medianPortfolioValue = medianPortfolioValue;
	}

	@Override
	public BigDecimal getBandOneAccounts()
	{
		return bandOneAccounts;
	}

	public void setBandOneAccounts(BigDecimal bandOneAccounts)
	{
		this.bandOneAccounts = bandOneAccounts;
	}

	@Override
	public BigDecimal getBandOnePortfolioValue()
	{
		return bandOnePortfolioValue;
	}

	public void setBandOnePortfolioValue(BigDecimal bandOnePortfolioValue)
	{
		this.bandOnePortfolioValue = bandOnePortfolioValue;
	}

	@Override
	public BigDecimal getBandTwoAccounts()
	{
		return bandTwoAccounts;
	}

	public void setBandTwoAccounts(BigDecimal bandTwoAccounts)
	{
		this.bandTwoAccounts = bandTwoAccounts;
	}

	@Override
	public BigDecimal getBandTwoPortfolioValue()
	{
		return bandTwoPortfolioValue;
	}

	public void setBandTwoPortfolioValue(BigDecimal bandTwoPortfolioValue)
	{
		this.bandTwoPortfolioValue = bandTwoPortfolioValue;
	}

	@Override
	public BigDecimal getBandThreeAccounts()
	{
		return bandThreeAccounts;
	}

	public void setBandThreeAccounts(BigDecimal bandThreeAccounts)
	{
		this.bandThreeAccounts = bandThreeAccounts;
	}

	@Override
	public BigDecimal getBandThreePortfolioValue()
	{
		return bandThreePortfolioValue;
	}

	public void setBandThreePortfolioValue(BigDecimal bandThreePortfolioValue)
	{
		this.bandThreePortfolioValue = bandThreePortfolioValue;
	}

	@Override
	public BigDecimal getBandFourAccounts()
	{
		return bandFourAccounts;
	}

	public void setBandFourAccounts(BigDecimal bandFourAccounts)
	{
		this.bandFourAccounts = bandFourAccounts;
	}

	@Override
	public BigDecimal getBandFourPortfolioValue()
	{
		return bandFourPortfolioValue;
	}

	public void setBandFourPortfolioValue(BigDecimal bandFourPortfolioValue)
	{
		this.bandFourPortfolioValue = bandFourPortfolioValue;
	}

	@Override
	public BigDecimal getBandFiveAccounts()
	{
		return bandFiveAccounts;
	}

	public void setBandFiveAccounts(BigDecimal bandFiveAccounts)
	{
		this.bandFiveAccounts = bandFiveAccounts;
	}

	@Override
	public BigDecimal getBandFivePortfolioValue()
	{
		return bandFivePortfolioValue;
	}

	public void setBandFivePortfolioValue(BigDecimal bandFivePortfolioValue)
	{
		this.bandFivePortfolioValue = bandFivePortfolioValue;
	}

	@Override
	public BigDecimal getBandSixAccounts()
	{
		return bandSixAccounts;
	}

	public void setBandSixAccounts(BigDecimal bandSixAccounts)
	{
		this.bandSixAccounts = bandSixAccounts;
	}

	@Override
	public BigDecimal getBandSixPortfolioValue()
	{
		return bandSixPortfolioValue;
	}

	public void setBandSixPortfolioValue(BigDecimal bandSixPortfolioValue)
	{
		this.bandSixPortfolioValue = bandSixPortfolioValue;
	}

	@SuppressWarnings("null")
	public BigDecimal[] getPortfolioBand(int index)
	{
		BigDecimal[] accounts = new BigDecimal[2];
		switch (index)
		{
			case 1:
				accounts[0] = bandOneAccounts;
				accounts[1] = bandOnePortfolioValue;
				break;
			case 2:
				accounts[0] = bandTwoAccounts;
				accounts[1] = bandTwoPortfolioValue;
				break;
			case 3:
				accounts[0] = bandThreeAccounts;
				accounts[1] = bandThreePortfolioValue;
				break;
			case 4:
				accounts[0] = bandFourAccounts;
				accounts[1] = bandFourPortfolioValue;
				break;
			case 5:
				accounts[0] = bandFiveAccounts;
				accounts[1] = bandFivePortfolioValue;
				break;
			case 6:
				accounts[0] = bandSixAccounts;
				accounts[1] = bandSixPortfolioValue;
				break;
		}
		return accounts;
	}
}
