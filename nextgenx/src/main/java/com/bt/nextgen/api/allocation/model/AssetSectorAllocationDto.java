package com.bt.nextgen.api.allocation.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * @deprecated use account.v2.mode.allocation
 */
@Deprecated
public class AssetSectorAllocationDto extends AllocationSummaryDto
{
    private BigDecimal quantity;

    public AssetSectorAllocationDto(String name, BigDecimal quantity, BigDecimal marketValue, BigDecimal allocationPercent,
		List <HoldingAllocationDto> securities)
	{
		super(name, marketValue, allocationPercent, securities);
		this.quantity = quantity;
	}

    public BigDecimal getQuantity()
	{
		return quantity;
	}

	@Override
	public int compareTo(AllocationSummaryDto o)
	{
		return 0;
	}

}
