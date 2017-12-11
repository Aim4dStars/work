package com.bt.nextgen.api.account.v2.service;

import com.bt.nextgen.api.account.v2.model.DateRangeAccountKey;
import com.bt.nextgen.api.account.v2.model.PerformanceReportDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

@Deprecated
public interface AccountPeriodPerformanceDtoService extends FindByKeyDtoService<DateRangeAccountKey, PerformanceReportDto> {
}
