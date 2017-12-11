package com.bt.nextgen.api.portfolio.v3.service.performance;

import com.bt.nextgen.api.portfolio.v3.model.DateRangeAccountKey;
import com.bt.nextgen.api.portfolio.v3.model.performance.AccountPerformanceDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

public interface AccountPerformanceChartDtoService extends FindByKeyDtoService<DateRangeAccountKey, AccountPerformanceDto> {

}
