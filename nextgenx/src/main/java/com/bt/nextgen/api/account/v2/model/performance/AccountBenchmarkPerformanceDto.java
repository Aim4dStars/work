package com.bt.nextgen.api.account.v2.model.performance;

import com.bt.nextgen.api.account.v2.model.DateValueDto;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.integration.performance.PerformancePeriodType;

import java.util.List;

@Deprecated
public class AccountBenchmarkPerformanceDto extends BaseDto implements KeyedDto<AccountBenchmarkPerformanceKey> {
    private final AccountBenchmarkPerformanceKey key;
    private List<DateValueDto> benchmarkData;
    private PerformancePeriodType periodType;

    @Override
    public AccountBenchmarkPerformanceKey getKey() {
        return key;
    }

    public AccountBenchmarkPerformanceDto(AccountBenchmarkPerformanceKey key, List<DateValueDto> benchmarkData,
            PerformancePeriodType periodType) {
        this.key = key;
        this.periodType = periodType;
        this.benchmarkData = benchmarkData;
    }

    public List<DateValueDto> getBenchmarkData() {
        return benchmarkData;
    }

    public PerformancePeriodType getPeriodType() {
        return periodType;
    }

}
