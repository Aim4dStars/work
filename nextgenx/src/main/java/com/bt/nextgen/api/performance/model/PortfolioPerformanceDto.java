package com.bt.nextgen.api.performance.model;

import com.bt.nextgen.api.account.v2.model.DateRangeAccountKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.math.BigDecimal;
import java.util.List;

public class PortfolioPerformanceDto extends BaseDto implements KeyedDto<DateRangeAccountKey> {
    private DateRangeAccountKey key;
    private BigDecimal performanceBeforeFeesDollars;
    private BigDecimal performanceBeforeFeesPercent;
    private BigDecimal performanceAfterFeesDollars;
    private BigDecimal performanceAfterFeesPercent;
    private BigDecimal performancePercent;
    private BigDecimal performanceIncomePercent;
    private BigDecimal performanceGrowthPercent;
    private List<PerformanceDto> investmentPerformances;

    public PortfolioPerformanceDto(DateRangeAccountKey key, BigDecimal performanceBeforeFeesDollars,
            BigDecimal performanceBeforeFeesPercent, BigDecimal performanceAfterFeesDollars,
            BigDecimal performanceAfterFeesPercent, BigDecimal performancePercent, BigDecimal performanceIncomePercent,
            BigDecimal performanceGrowthPercent, List<PerformanceDto> investmentPerformances) {
        super();

        this.key = key;
        this.performanceBeforeFeesDollars = performanceBeforeFeesDollars;
        this.performanceBeforeFeesPercent = performanceBeforeFeesPercent;
        this.performanceAfterFeesDollars = performanceAfterFeesDollars;
        this.performanceAfterFeesPercent = performanceAfterFeesPercent;
        this.performancePercent = performancePercent;
        this.investmentPerformances = investmentPerformances;
        this.performanceIncomePercent = performanceIncomePercent;
        this.performanceGrowthPercent = performanceGrowthPercent;
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

    public BigDecimal getPerformancePercent() {
        return performancePercent;
    }

    public List<PerformanceDto> getInvestmentPerformances() {
        return investmentPerformances;
    }

    public BigDecimal getPerformanceIncomePercent() {
        return performanceIncomePercent;
    }

    public BigDecimal getPerformanceGrowthPercent() {
        return performanceGrowthPercent;
    }

    @Override
    public DateRangeAccountKey getKey() {
        return key;
    }

}
