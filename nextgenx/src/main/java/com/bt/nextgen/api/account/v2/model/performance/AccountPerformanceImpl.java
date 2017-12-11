package com.bt.nextgen.api.account.v2.model.performance;

import com.bt.nextgen.api.account.v2.model.DateRangeAccountKey;
import com.bt.nextgen.api.account.v2.model.DateValueDto;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.integration.performance.PerformancePeriodType;

import java.util.List;

@Deprecated
public class AccountPerformanceImpl extends BaseDto implements AccountPerformance {
    private DateRangeAccountKey key;

    // income, capital and total performance attributes
    private List<DateValueDto> capitalPerformance;
    private List<DateValueDto> incomePerformance;
    private List<DateValueDto> periodPerformance;
    private List<DateValueDto> cumulativePerformance;
    private List<DateValueDto> portfolioValue;
    private List<DateValueDto> periodDollar;

    private PerformanceSummaryDto performanceSummaryDto;

    private PerformancePeriodType detailedPeriodType;
    private PerformancePeriodType summaryPeriodType;

    private List<String> colHeaders;

    public AccountPerformanceImpl() {
    }

    public void setKey(DateRangeAccountKey key) {
        this.key = key;
    }

    public void setDetailedPeriodType(PerformancePeriodType detailedPeriodType) {
        this.detailedPeriodType = detailedPeriodType;
    }

    public void setSummaryPeriodType(PerformancePeriodType summaryPeriodType) {
        this.summaryPeriodType = summaryPeriodType;
    }

    public void setColHeaders(List<String> colHeaders) {
        this.colHeaders = colHeaders;
    }

    public void setCapitalPerformance(List<DateValueDto> capitalPerformance) {
        this.capitalPerformance = capitalPerformance;
    }

    public void setIncomePerformance(List<DateValueDto> incomePerformance) {
        this.incomePerformance = incomePerformance;
    }

    public void setPeriodPerformance(List<DateValueDto> periodPerformance) {
        this.periodPerformance = periodPerformance;
    }

    public void setCumulativePerformance(List<DateValueDto> cumulativePerformance) {
        this.cumulativePerformance = cumulativePerformance;
    }

    public void setPerformanceSummaryDto(PerformanceSummaryDto performanceSummaryDto) {
        this.performanceSummaryDto = performanceSummaryDto;
    }

    public void setPortfolioValue(List<DateValueDto> portfolioValue) {
        this.portfolioValue = portfolioValue;
    }

    public void setPeriodDollar(List<DateValueDto> periodDollar) {
        this.periodDollar = periodDollar;
    }

    @Override
    public DateRangeAccountKey getKey() {
        return key;
    }

    @Override
    public List<DateValueDto> getCapitalPerformance() {
        return capitalPerformance;
    }

    @Override
    public List<DateValueDto> getIncomePerformance() {
        return incomePerformance;
    }

    @Override
    public List<DateValueDto> getPeriodPerformance() {
        return periodPerformance;
    }

    @Override
    public List<DateValueDto> getCumulativePerformance() {
        return cumulativePerformance;
    }

    @Override
    public List<DateValueDto> getPortfolioValue() {
        return portfolioValue;
    }

    @Override
    public List<DateValueDto> getPeriodDollar() {
        return periodDollar;
    }

    @Override
    public PerformanceSummaryDto getPerformanceSummaryDto() {
        return performanceSummaryDto;
    }

    @Override
    public List<String> getColHeaders() {
        return colHeaders;
    }

    @Override
    public PerformancePeriodType getSummaryPeriodType() {
        return summaryPeriodType;
    }

    @Override
    public PerformancePeriodType getDetailedPeriodType() {
        return detailedPeriodType;
    }

}
