package com.bt.nextgen.api.account.v2.service.allocation;

import com.bt.nextgen.api.account.v2.model.DatedValuationKey;
import com.bt.nextgen.api.account.v2.model.allocation.exposure.KeyedAllocByExposureDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

@Deprecated
public interface AllocationByExposureDtoService extends FindByKeyDtoService<DatedValuationKey, KeyedAllocByExposureDto>
{

}
