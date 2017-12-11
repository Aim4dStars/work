package com.bt.nextgen.api.portfolio.v3.model.performance;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.math.BigDecimal;

public class PerformanceSummaryDto<K> extends BaseDto implements KeyedDto<K> {
    private K key;

    // period return attributes
    private BigDecimal percentagePeriodReturn;
    private BigDecimal dollarPeriodReturn;
    private BigDecimal capitalPerformanceForPeriod;
    private BigDecimal incomePerformanceForPeriod;


    public PerformanceSummaryDto(K key, BigDecimal percentagePeriodReturn, BigDecimal dollarPeriodReturn,
            BigDecimal capitalPerformanceForPeriod, BigDecimal incomePerformanceForPeriod) {
        super();
        this.key = key;
        this.percentagePeriodReturn = percentagePeriodReturn;
        this.dollarPeriodReturn = dollarPeriodReturn;
        this.capitalPerformanceForPeriod = capitalPerformanceForPeriod;
        this.incomePerformanceForPeriod = incomePerformanceForPeriod;
    }

    public BigDecimal getPercentagePeriodReturn() {
        return percentagePeriodReturn;
    }

    public BigDecimal getDollarPeriodReturn() {
        return dollarPeriodReturn;
    }

    public BigDecimal getCapitalPerformanceForPeriod() {
        return capitalPerformanceForPeriod;
    }

    public BigDecimal getIncomePerformanceForPeriod() {
        return incomePerformanceForPeriod;
    }

    @Override
    public K getKey() {
        return key;
    }

}
