package com.bt.nextgen.api.account.v2.model.allocation.exposure;

import com.bt.nextgen.api.account.v2.model.DatedValuationKey;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.List;

@Deprecated
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
