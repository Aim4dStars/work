package com.bt.nextgen.api.account.v2.service.performance;

import com.bt.nextgen.api.account.v2.model.DateRangeAccountKey;
import com.bt.nextgen.api.account.v2.model.performance.AccountPerformanceOverallDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

@Deprecated
public interface AccountPerformanceOverallDtoService extends FindByKeyDtoService<DateRangeAccountKey, AccountPerformanceOverallDto> {
}
