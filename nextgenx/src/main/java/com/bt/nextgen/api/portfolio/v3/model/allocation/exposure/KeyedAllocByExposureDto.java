package com.bt.nextgen.api.portfolio.v3.model.allocation.exposure;

import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.List;

public class KeyedAllocByExposureDto extends AggregateAllocationByExposureDto implements KeyedDto<DatedValuationKey>
{
    private DatedValuationKey key;
    private Boolean hasExternal;
   
    public KeyedAllocByExposureDto(String name, List<AllocationByExposureDto> contents, DatedValuationKey key,
            Boolean hasExternal) {
        super(name, contents);
        this.key = key;
        this.hasExternal = hasExternal;
    }

    public DatedValuationKey getKey()
	{
		return key;
	}
    
    public Boolean getHasExternal() {
        return hasExternal;
    }

}
