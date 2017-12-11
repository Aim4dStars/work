package com.bt.nextgen.api.portfolio.v3.model.performance;

import com.bt.nextgen.service.integration.asset.AssetPerformance;

import java.util.List;

public class ManagedPortfolioPerformanceDto extends PeriodPerformanceDto implements Comparable<PeriodPerformanceDto> {

    private String investmentId;
    private List<PeriodPerformanceDto> assetPerformance;

    public ManagedPortfolioPerformanceDto(AssetPerformance assetPerformance, String investmentId,
            List<PeriodPerformanceDto> performanceList) {
        super(assetPerformance, assetPerformance.getName(), null);

        this.assetPerformance = performanceList;
        this.investmentId = investmentId;
    }

    public List<PeriodPerformanceDto> getAssetPerformance() {
        return assetPerformance;
    }

    public String getInvestmentId() {
        return investmentId;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
