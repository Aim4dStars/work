package com.bt.nextgen.api.portfolio.v3.service.allocation;

import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.KeyedAllocBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

public interface AllocationBySectorDtoService extends FindByKeyDtoService<DatedValuationKey, KeyedAllocBySectorDto>
{

}
