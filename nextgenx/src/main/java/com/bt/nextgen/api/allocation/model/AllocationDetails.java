package com.bt.nextgen.api.allocation.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.math.BigDecimal;

/**
 * @deprecated use account.v2.mode.allocation
 */
@Deprecated
public class AllocationDetails extends BaseDto
{
	private BigDecimal quantity;
	private String assetSector;
	private String industrySector;
	private String industrySubSector;
	private BigDecimal marketValue;
	private BigDecimal allocationPercent;

	public AllocationDetails(BigDecimal quantity, String assetSector, String industrySector, String industrySubSector,
		BigDecimal marketValue, BigDecimal allocationPercent)
	{
		this.quantity = quantity;
		this.assetSector = assetSector;
		this.industrySector = industrySector;
		this.industrySubSector = industrySubSector;
		this.marketValue = marketValue;
		this.allocationPercent = allocationPercent;
	}

	public BigDecimal getQuantity()
	{
		return quantity;
	}

	public String getAssetSector()
	{
		return assetSector;
	}

	public String getIndustrySector()
	{
		return industrySector;
	}

	public String getIndustrySubSector()
	{
		return industrySubSector;
	}

	public BigDecimal getMarketValue()
	{
		return marketValue;
	}

	public BigDecimal getAllocationPercent()
	{
		return allocationPercent;
	}

	public AllocationDetails add(AllocationDetails other)
	{
		BigDecimal sumQuantity = add(this.quantity, other.getQuantity());
		BigDecimal sumMktValue = add(this.marketValue, other.getMarketValue());
		BigDecimal sumPercent = add(this.allocationPercent, other.getAllocationPercent());

		return new AllocationDetails(sumQuantity,
			this.assetSector,
			this.industrySector,
			this.industrySubSector,
			sumMktValue,
			sumPercent);
	}

	private BigDecimal add(BigDecimal a, BigDecimal b)
	{
		if (a == null && b == null)
		{
			return null;
		}

		BigDecimal sum = a;
		if (a == null)
		{
			sum = BigDecimal.ZERO;
		}
		else if (b == null)
		{
			return sum;
		}
		return sum.add(b);
	}
}
