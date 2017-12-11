package com.bt.nextgen.api.portfolio.v3.model.allocation.sector;

import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.List;

public class KeyedAllocBySectorDto extends AggregatedAllocationBySectorDto implements KeyedDto<DatedValuationKey>
{
    private DatedValuationKey key;
    private Boolean hasExternal;

    public KeyedAllocBySectorDto(AggregatedAllocationBySectorDto allocation, DatedValuationKey key, Boolean hasExternal) {
        super(allocation);
        this.key = key;
        this.hasExternal = hasExternal;
    }

    public KeyedAllocBySectorDto(String name, List<AllocationBySectorDto> contents, DatedValuationKey key)
	{
        super(name, contents);
		this.key = key;
	}

    public DatedValuationKey getKey()
	{
		return key;
	}
    
    public Boolean getHasExternal(){
        return hasExternal;
    }

}
