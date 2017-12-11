package com.bt.nextgen.api.account.v2.service.allocation;

import com.bt.nextgen.api.account.v2.model.DatedValuationKey;
import com.bt.nextgen.api.account.v2.model.allocation.sector.KeyedAllocBySectorDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

@Deprecated
public interface AllocationBySectorDtoService extends FindByKeyDtoService<DatedValuationKey, KeyedAllocBySectorDto>
{

}
