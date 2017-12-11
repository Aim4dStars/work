package com.bt.nextgen.api.portfolio.v3.service.performance;

import com.bt.nextgen.api.portfolio.v3.model.performance.AccountBenchmarkPerformanceDto;
import com.bt.nextgen.api.portfolio.v3.model.performance.AccountBenchmarkPerformanceKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

public interface AccountBenchmarkPerformanceChartDtoService extends
        FindByKeyDtoService<AccountBenchmarkPerformanceKey, AccountBenchmarkPerformanceDto> {

}
