package com.bt.nextgen.api.portfolio.v3.model.performance;

import com.bt.nextgen.api.portfolio.v3.model.DateValueDto;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.integration.performance.PerformancePeriodType;

import java.util.List;

public class AccountBenchmarkPerformanceDto extends BaseDto implements KeyedDto<AccountBenchmarkPerformanceKey> {
    private final AccountBenchmarkPerformanceKey key;
    private List<DateValueDto> benchmarkData;
    private PerformancePeriodType periodType;
    private String benchmarkName;

    @Override
    public AccountBenchmarkPerformanceKey getKey() {
        return key;
    }

    public AccountBenchmarkPerformanceDto(AccountBenchmarkPerformanceKey key, List<DateValueDto> benchmarkData,
            String benchmarkName, PerformancePeriodType periodType) {
        this.key = key;
        this.periodType = periodType;
        this.benchmarkData = benchmarkData;
        this.benchmarkName = benchmarkName;
    }

    public List<DateValueDto> getBenchmarkData() {
        return benchmarkData;
    }

    public PerformancePeriodType getPeriodType() {
        return periodType;
    }

    public String getBenchmarkName() {
        return benchmarkName;
    }

}
