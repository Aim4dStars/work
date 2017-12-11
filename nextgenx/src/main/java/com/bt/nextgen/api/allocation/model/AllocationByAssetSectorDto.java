package com.bt.nextgen.api.allocation.model;

import com.bt.nextgen.api.account.v1.model.DatedAccountKey;
import com.bt.nextgen.core.api.model.BaseDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * @deprecated use account.v2.mode.allocation
 */
@Deprecated
public class AllocationByAssetSectorDto extends BaseDto implements AllocationDto
{
	private DatedAccountKey key;
	private BigDecimal balance;
	private List <HoldingAllocationDto> securities;

    public AllocationByAssetSectorDto(DatedAccountKey key, BigDecimal balance, List<HoldingAllocationDto> securities)
	{
		super();
		this.key = key;
		this.securities = securities;
		this.balance = balance;
	}

	@Override
	public DatedAccountKey getKey()
	{
		return key;
	}

	public BigDecimal getBalance()
	{
		return balance;
	}

	public List <HoldingAllocationDto> getSecurities()
	{
		return securities;
	}
}
