package com.bt.nextgen.api.performance.service;

import com.bt.nextgen.api.account.v2.model.DateRangeAccountKey;
import com.bt.nextgen.api.performance.model.PortfolioPerformanceDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

public interface PerformanceDtoService extends FindByKeyDtoService<DateRangeAccountKey, PortfolioPerformanceDto> {

}
