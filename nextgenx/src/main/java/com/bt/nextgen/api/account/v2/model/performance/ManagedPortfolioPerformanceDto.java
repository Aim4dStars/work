package com.bt.nextgen.api.account.v2.model.performance;

import com.bt.nextgen.service.integration.asset.AssetPerformance;

import java.util.List;

@Deprecated
public class ManagedPortfolioPerformanceDto extends PerformanceDto implements Comparable<PerformanceDto> {

    private String investmentId;
    private List<PerformanceDto> assetPerformance;

    public ManagedPortfolioPerformanceDto(AssetPerformance assetPerformance, String investmentId,
            List<PerformanceDto> performanceList) {
        super(assetPerformance, assetPerformance.getName(), null);

        this.assetPerformance = performanceList;
        this.investmentId = investmentId;
    }

    public List<PerformanceDto> getAssetPerformance() {
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
