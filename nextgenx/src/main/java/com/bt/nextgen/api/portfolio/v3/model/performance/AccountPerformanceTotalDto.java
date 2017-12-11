package com.bt.nextgen.api.portfolio.v3.model.performance;

import com.bt.nextgen.api.portfolio.v3.model.DateRangeAccountKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.math.BigDecimal;

public class AccountPerformanceTotalDto extends BaseDto implements KeyedDto<DateRangeAccountKey> {

    private DateRangeAccountKey key;

    private BigDecimal performanceBeforeFeesDollars;
    private BigDecimal performanceBeforeFeesPercent;

    private BigDecimal performanceAfterFeesDollars;
    private BigDecimal performanceAfterFeesPercent;

    private BigDecimal performanceIncomePercent;
    private BigDecimal performanceGrowthPercent;

    public AccountPerformanceTotalDto(DateRangeAccountKey key, BigDecimal performanceBeforeFeesDollars,
            BigDecimal performanceBeforeFeesPercent, BigDecimal performanceAfterFeesDollars,
            BigDecimal performanceAfterFeesPercent, BigDecimal performanceIncomePercent, BigDecimal performanceGrowthPercent) {

        super();

        this.key = key;
        this.performanceBeforeFeesDollars = performanceBeforeFeesDollars;
        this.performanceBeforeFeesPercent = performanceBeforeFeesPercent;
        this.performanceAfterFeesDollars = performanceAfterFeesDollars;
        this.performanceAfterFeesPercent = performanceAfterFeesPercent;
        this.performanceIncomePercent = performanceIncomePercent;
        this.performanceGrowthPercent = performanceGrowthPercent;
    }

    @Override
    public DateRangeAccountKey getKey() {
        return key;
    }

    public BigDecimal getPerformanceBeforeFeesDollars() {
        return performanceBeforeFeesDollars;
    }

    public BigDecimal getPerformanceBeforeFeesPercent() {
        return performanceBeforeFeesPercent;
    }

    public BigDecimal getPerformanceAfterFeesDollars() {
        return performanceAfterFeesDollars;
    }

    public BigDecimal getPerformanceAfterFeesPercent() {
        return performanceAfterFeesPercent;
    }

    public BigDecimal getPerformanceIncomePercent() {
        return performanceIncomePercent;
    }

    public BigDecimal getPerformanceGrowthPercent() {
        return performanceGrowthPercent;
    }
}
