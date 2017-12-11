package com.bt.nextgen.api.portfolio.v3.service.performance;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.portfolio.v3.model.performance.PerformanceDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

public interface AccountPerformanceDtoService extends FindByKeyDtoService <AccountKey, PerformanceDto>
{

}
