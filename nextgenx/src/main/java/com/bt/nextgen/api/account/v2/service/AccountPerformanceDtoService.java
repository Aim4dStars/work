package com.bt.nextgen.api.account.v2.service;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.account.v2.model.PerformanceDto;

import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

@Deprecated
public interface AccountPerformanceDtoService extends FindByKeyDtoService <AccountKey, PerformanceDto>
{

}
