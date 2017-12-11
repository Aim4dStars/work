package com.bt.nextgen.api.modelportfolio.v2.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.math.BigDecimal;

public class ShadowPortfolioDto extends BaseDto
{

	private String assetClass;
	private String assetCode;
	private String assetName;
	private BigDecimal lastUpdatedTargetPercent;
	private BigDecimal floatingTargetPercent;
	private BigDecimal units;
	private BigDecimal marketValue;
	private BigDecimal shadowPercent;
	private BigDecimal differencePercent;
	private Boolean isTotal;

    // TODO fix this
    @SuppressWarnings("squid:S00107")
	public ShadowPortfolioDto(String assetClass, String assetCode, String assetName, BigDecimal lastUpdatedTargetPercent,
		BigDecimal floatingTargetPercent, BigDecimal units, BigDecimal marketValue, BigDecimal shadowPercent,
		BigDecimal differencePercent, Boolean isTotal)
	{
		super();
		this.assetClass = assetClass;
		this.assetCode = assetCode;
		this.assetName = assetName;
		this.lastUpdatedTargetPercent = lastUpdatedTargetPercent;
		this.floatingTargetPercent = floatingTargetPercent;
		this.units = units;
		this.marketValue = marketValue;
		this.shadowPercent = shadowPercent;
		this.differencePercent = differencePercent;
		this.isTotal = isTotal;
	}

	public String getAssetClass()
	{
		return assetClass;
	}

	public void setAssetClass(String assetClass)
	{
		this.assetClass = assetClass;
	}

	public String getAssetCode()
	{
		return assetCode;
	}

	public void setAssetCode(String assetCode)
	{
		this.assetCode = assetCode;
	}

	public String getAssetName() {
		return assetName;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	public BigDecimal getLastUpdatedTargetPercent()
	{
		return lastUpdatedTargetPercent;
	}

	public void setLastUpdatedTargetPercent(BigDecimal lastUpdatedTargetPercent)
	{
		this.lastUpdatedTargetPercent = lastUpdatedTargetPercent;
	}

	public BigDecimal getFloatingTargetPercent()
	{
		return floatingTargetPercent;
	}

	public void setFloatingTargetPercent(BigDecimal floatingTargetPercent)
	{
		this.floatingTargetPercent = floatingTargetPercent;
	}

	public BigDecimal getUnits()
	{
		return units;
	}

	public void setUnits(BigDecimal units)
	{
		this.units = units;
	}

	public BigDecimal getMarketValue()
	{
		return marketValue;
	}

	public void setMarketValue(BigDecimal marketValue)
	{
		this.marketValue = marketValue;
	}

	public BigDecimal getShadowPercent()
	{
		return shadowPercent;
	}

	public void setShadowPercent(BigDecimal shadowPercent)
	{
		this.shadowPercent = shadowPercent;
	}

	public BigDecimal getDifferencePercent()
	{
		return differencePercent;
	}

	public void setDifferencePercent(BigDecimal differencePercent)
	{
		this.differencePercent = differencePercent;
	}

	public Boolean getIsTotal()
	{
		return isTotal;
	}

	public void setIsTotal(Boolean isTotal)
	{
		this.isTotal = isTotal;
	}
}
