package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.api.account.v1.model.AccountKey;
import com.bt.nextgen.api.account.v1.model.PerformanceDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

/**
 * @deprecated Use V2
 */
@Deprecated
public interface AccountPerformanceDtoService extends FindByKeyDtoService <AccountKey, PerformanceDto>
{

}
