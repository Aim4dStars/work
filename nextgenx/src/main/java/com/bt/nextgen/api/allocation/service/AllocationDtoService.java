package com.bt.nextgen.api.allocation.service;

import com.bt.nextgen.api.account.v1.model.DatedAccountKey;
import com.bt.nextgen.api.allocation.model.AllocationDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

/**
 * @deprecated use account.v2.service.allocation
 */
@Deprecated
public interface AllocationDtoService extends FindByKeyDtoService <DatedAccountKey, AllocationDto>
{

}
