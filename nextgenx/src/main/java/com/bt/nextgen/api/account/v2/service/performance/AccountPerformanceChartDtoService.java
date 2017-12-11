package com.bt.nextgen.api.account.v2.service.performance;

import com.bt.nextgen.api.account.v2.model.DateRangeAccountKey;
import com.bt.nextgen.api.account.v2.model.performance.AccountPerformance;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

@Deprecated
public interface AccountPerformanceChartDtoService extends FindByKeyDtoService<DateRangeAccountKey, AccountPerformance> {

}
