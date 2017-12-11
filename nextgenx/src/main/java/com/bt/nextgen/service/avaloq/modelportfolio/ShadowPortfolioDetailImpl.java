package com.bt.nextgen.service.avaloq.modelportfolio;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolioDetail;

public class ShadowPortfolioDetailImpl implements ShadowPortfolioDetail
{
	@NotNull
	private BigDecimal lastUpdatedTargetPercent;

	@NotNull
	private BigDecimal floatingTargetPercent;

	private BigDecimal units;

	private BigDecimal marketValue;

	private BigDecimal shadowPercent;

	private BigDecimal differencePercent;

	@Override
	public BigDecimal getLastUpdatedTargetPercent()
	{
		return lastUpdatedTargetPercent;
	}

	@Override
	public BigDecimal getFloatingTargetPercent()
	{
		return floatingTargetPercent;
	}

	@Override
	public BigDecimal getUnits()
	{
		return units;
	}

	@Override
	public BigDecimal getMarketValue()
	{
		return marketValue;
	}

	@Override
	public BigDecimal getShadowPercent()
	{
		return shadowPercent;
	}

	@Override
	public BigDecimal getDifferencePercent()
	{
		return differencePercent;
	}

	public void setLastUpdatedTargetPercent(BigDecimal lastUpdatedTargetPercent)
	{
		this.lastUpdatedTargetPercent = lastUpdatedTargetPercent;
	}

	public void setFloatingTargetPercent(BigDecimal floatingTargetPercent)
	{
		this.floatingTargetPercent = floatingTargetPercent;
	}

	public void setUnits(BigDecimal units)
	{
		this.units = units;
	}

	public void setMarketValue(BigDecimal marketValue)
	{
		this.marketValue = marketValue;
	}

	public void setShadowPercent(BigDecimal shadowPercent)
	{
		this.shadowPercent = shadowPercent;
	}

	public void setDifferencePercent(BigDecimal differencePercent)
	{
		this.differencePercent = differencePercent;
	}
}
