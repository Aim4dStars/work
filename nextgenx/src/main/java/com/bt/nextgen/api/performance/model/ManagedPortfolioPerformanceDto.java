package com.bt.nextgen.api.performance.model;

import java.math.BigDecimal;
import java.util.List;

public class ManagedPortfolioPerformanceDto extends PerformanceDto implements Comparable<PerformanceDto> {
    private String investmentId;
    private List<PerformanceDto> assetPerformance;

    public ManagedPortfolioPerformanceDto(String name, String investmentId, String assetCode, BigDecimal openingBalance,
            BigDecimal closingBalance, BigDecimal purchase, BigDecimal sales, BigDecimal movement, BigDecimal netIncome,
            BigDecimal performancePercentage, BigDecimal performanceDollar, Integer periodHeld, BigDecimal frankingCredit,
            BigDecimal benchmarkDiff, BigDecimal performanceIncomePercentage, BigDecimal performanceGrowthPercentage,
            List<PerformanceDto> assetPerformance, String containerType) {
        super(name, assetCode, openingBalance, closingBalance, purchase, sales, movement, netIncome, performancePercentage,
                performanceDollar, periodHeld, frankingCredit, benchmarkDiff, performanceIncomePercentage,
                performanceGrowthPercentage, containerType, null);

        this.assetPerformance = assetPerformance;
        this.investmentId = investmentId;
    }

    public List<PerformanceDto> getAssetPerformance() {
        return assetPerformance;
    }

    public String getInvestmentId() {
        return investmentId;
    }
}
