package com.bt.nextgen.reports.account.performance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.Renderable;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.bt.nextgen.api.performance.model.BenchmarkPerformanceDto;
import com.bt.nextgen.api.portfolio.v3.model.DatedAccountKey;
import com.bt.nextgen.api.portfolio.v3.model.performance.AccountPerformanceTotalDto;
import com.bt.nextgen.api.portfolio.v3.model.performance.PerformanceSummaryDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;

public class AccountAssetPerformanceSummaryData {
    private AccountPerformanceTotalDto total;
    private PerformanceSummaryDto<DatedAccountKey> sinceInception;
    private List<BenchmarkPerformanceDto> benchmarks;
    private Map<Enum<?>, Renderable> imageMap;

    public AccountAssetPerformanceSummaryData(AccountPerformanceTotalDto total,
            PerformanceSummaryDto<DatedAccountKey> sinceInception, List<BenchmarkPerformanceDto> benchmarks,
            Map<Enum<?>, Renderable> imageMap) {
        this.total = total;
        this.sinceInception = sinceInception;
        this.benchmarks = benchmarks;
        this.imageMap = imageMap;
    }

    public String getPerformanceDollarBeforeFees() {
        return ReportFormatter.format(ReportFormat.CURRENCY, total.getPerformanceBeforeFeesDollars());
    }

    public String getPerformancePercentBeforeFees() {
        return ReportFormatter.format(ReportFormat.PERCENTAGE, total.getPerformanceBeforeFeesPercent());
    }

    public String getPerformanceDollarAfterFees() {
        return ReportFormatter.format(ReportFormat.CURRENCY, total.getPerformanceAfterFeesDollars());
    }

    public String getPerformancePercentAfterFees() {
        return ReportFormatter.format(ReportFormat.PERCENTAGE, total.getPerformanceAfterFeesPercent());
    }

    public String getSinceInceptionPerformance() {
        return ReportFormatter.format(ReportFormat.PERCENTAGE, sinceInception.getPercentagePeriodReturn());
    }

    public Renderable getGrowthIndicatorForPerformanceAfterFees() {
        return imageMap.get(GrowthIndicator.fromValue(total.getPerformanceAfterFeesPercent()));
    }

    public Renderable getGrowthIndicatorForSinceInceptionPerformance() {
        return imageMap.get(GrowthIndicator.fromValue(sinceInception.getPercentagePeriodReturn()));
    }

    public List<Pair<String, String>> getBenchmarks() {
        List<Pair<String, String>> results = new ArrayList<>();
        for (BenchmarkPerformanceDto benchmark : benchmarks) {
            results.add(new ImmutablePair<>(benchmark.getName(),
                    ReportFormatter.format(ReportFormat.PERCENTAGE, benchmark.getPerformance())));
        }
        return results;
    }

}