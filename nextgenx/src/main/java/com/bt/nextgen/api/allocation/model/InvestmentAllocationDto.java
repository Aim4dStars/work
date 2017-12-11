package com.bt.nextgen.api.allocation.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.math.BigDecimal;

/**
 * @deprecated use account.v2.mode.allocation
 */
@Deprecated
public class InvestmentAllocationDto extends BaseDto implements Comparable <InvestmentAllocationDto>
{
	private String investmentName;
	private BigDecimal quantity;
	private String assetId;
	private String assetCode;
	private String assetType;
	private String assetName;

	// Allocation details
	private String assetSector;
	private String industrySector;
	private String industrySubSector;
	private BigDecimal marketValue;
	private BigDecimal allocationPercent;

	public InvestmentAllocationDto(String investmentName, String assetId, String assetCode, String assetType, String assetName,
		AllocationDetails details)
	{
		super();
		this.investmentName = investmentName;
		this.assetId = assetId;
		this.assetCode = assetCode;
		this.assetType = assetType;
		this.assetName = assetName;

		this.assetSector = details.getAssetSector();
		this.allocationPercent = details.getAllocationPercent();
		this.industrySector = details.getIndustrySector();
		this.industrySubSector = details.getIndustrySubSector();
		this.marketValue = details.getMarketValue();
		this.quantity = details.getQuantity();
	}

	public String getAssetSector()
	{
		return assetSector;
	}

	public BigDecimal getQuantity()
	{
		return quantity;
	}

	public String getAssetCode()
	{
		return assetCode;
	}

	public String getAssetName()
	{
		return assetName;
	}

	public BigDecimal getMarketValue()
	{
		return marketValue;
	}

	public BigDecimal getAllocationPercent()
	{
		return allocationPercent;
	}

	public String getIndustrySector()
	{
		return industrySector;
	}

	public String getIndustrySubSector()
	{
		return industrySubSector;
	}

	public String getAssetType()
	{
		return assetType;
	}

	@Override
	public int compareTo(InvestmentAllocationDto o)
	{
		return assetName.compareTo(o.assetName);
	}

	public String getInvestmentName()
	{
		return investmentName;
	}

	public String getAssetId()
	{
		return assetId;
	}

}
