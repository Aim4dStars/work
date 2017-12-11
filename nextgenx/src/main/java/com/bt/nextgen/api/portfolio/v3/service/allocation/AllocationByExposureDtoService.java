package com.bt.nextgen.api.portfolio.v3.service.allocation;


import com.bt.nextgen.api.portfolio.v3.model.allocation.exposure.KeyedAllocByExposureDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

public interface AllocationByExposureDtoService extends FindByKeyDtoService<DatedValuationKey, KeyedAllocByExposureDto>
{

}
