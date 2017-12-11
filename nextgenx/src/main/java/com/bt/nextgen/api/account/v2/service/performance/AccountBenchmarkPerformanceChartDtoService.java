package com.bt.nextgen.api.account.v2.service.performance;

import com.bt.nextgen.api.account.v2.model.performance.AccountBenchmarkPerformanceDto;
import com.bt.nextgen.api.account.v2.model.performance.AccountBenchmarkPerformanceKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

@Deprecated
public interface AccountBenchmarkPerformanceChartDtoService extends
        FindByKeyDtoService<AccountBenchmarkPerformanceKey, AccountBenchmarkPerformanceDto> {

}
