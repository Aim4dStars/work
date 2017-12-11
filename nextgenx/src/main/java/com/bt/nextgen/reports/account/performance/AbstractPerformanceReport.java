package com.bt.nextgen.reports.account.performance;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.performance.model.AccountPerformanceChartDto;
import com.bt.nextgen.api.performance.model.ReportDataPointDto;
import com.bt.nextgen.api.performance.service.PerformanceReportUtil;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.reports.account.common.AccountReportV2;
import com.bt.nextgen.service.integration.performance.Performance;
import com.bt.nextgen.service.integration.performance.PerformancePeriodType;
import com.bt.nextgen.service.integration.performance.PeriodicPerformance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.*;

public abstract class AbstractPerformanceReport extends AccountReportV2 {

    public AbstractPerformanceReport() {
        super();
    }

    protected abstract void initPerformanceData(Map<String, Object> params, Map<String, Object> dataCollections);

    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        initPerformanceData(params, dataCollections);
        PeriodicPerformance performance = getPerformanceSummary(params, dataCollections);
        Performance sinceIncept = getInceptionPerformance(params, dataCollections);

        Map<String, PerformancePeriodType> periodTypes = PerformanceReportUtil.getPerformancePeriod(performance);
        PerformancePeriodType summaryPeriodType = periodTypes.get("summaryPeriodType");

        List<AccountPerformanceTypeData> data = new ArrayList<>();

        List<Performance> performances = new ArrayList<>(getPeriodPerformance(performance, summaryPeriodType));
        performances.add(performance.getPeriodPerformanceData());
        performances.add(sinceIncept);

        data.add(new PerformanceData(performances, summaryPeriodType, getPerformanceDisplayMode(getChartData(params,
                dataCollections)), getBenchmarkName(params, dataCollections) != null));
        data.add(new NetReturnData(performances, summaryPeriodType, hasOtherFees(params, dataCollections), getReturnMode(),
                getDisplayMode(performances)));
        return data;
    }

    protected boolean hasOtherFees(Map<String, Object> params, Map<String, Object> dataCollections) {
        PeriodicPerformance performance = getPerformanceSummary(params, dataCollections);
        PerformancePeriodType periodType = PerformanceReportUtil.getPerformancePeriod(performance).get("summaryPeriodType");
        List<Performance> data = getPeriodPerformance(performance, periodType);
        data = Lambda.select(data, new LambdaMatcher<Performance>() {
            @Override
            protected boolean matchesSafely(Performance performance) {
                return performance.getOtherFee() != null && performance.getOtherFee().compareTo(BigDecimal.ZERO) != 0;
            }
        });

        return !data.isEmpty();
    }

    protected NetReturnData.DisplayMode getDisplayMode(List<Performance> performances) {
        BigDecimal max = max(performances, on(Performance.class).getOpeningBalance());
        max = max.max(max(performances, on(Performance.class).getInflows()));
        max = max.max(max(performances, on(Performance.class).getOutflows()));
        max = max.max(max(performances, on(Performance.class).getExpenses()));
        // max = max.max(max(performances, on(Performance.class).getClosingBalanceBeforeFee()));
        max = max.max(max(performances, on(Performance.class).getClosingBalanceAfterFee()));
        max = max.max(max(performances, on(Performance.class).getNetGainLoss()));
        NetReturnData.DisplayMode mode;
        if (max.compareTo(BigDecimal.valueOf(1000000)) >= 0 && performances.size() > 12) {
            mode = NetReturnData.DisplayMode.COMPACT;
        } else if (max.compareTo(BigDecimal.valueOf(100000)) >= 0) {
            mode = NetReturnData.DisplayMode.TRUNCATED;
        } else {
            mode = NetReturnData.DisplayMode.FULL;
        }
        return mode;
    }

    protected PerformanceData.DisplayMode getPerformanceDisplayMode(AccountPerformanceChartDto chartDto) {
        BigDecimal max = BigDecimal.ZERO;
        for (ReportDataPointDto point : chartDto.getTotalPerformanceData()) {
            max = max.max(point.getValue().abs());
        }

        PerformanceData.DisplayMode mode = PerformanceData.DisplayMode.NORMAL;
        if (max.abs().compareTo(BigDecimal.valueOf(1)) < 0) {
            mode = PerformanceData.DisplayMode.PRECISE;
        }
        return mode;
    }

    protected List<Performance> getPeriodPerformance(PeriodicPerformance accountPerformance, PerformancePeriodType type) {
        List<Performance> result;
        switch (type) {
            case DAILY:
                result = accountPerformance.getDailyPerformanceData();
                break;
            case MONTHLY:
                result = accountPerformance.getMonthlyPerformanceData();
                break;
            case QUARTERLY:
                result = accountPerformance.getQuarterlyPerformanceData();
                break;
            case WEEKLY:
                result = accountPerformance.getWeeklyPerformanceData();
                break;
            case YEARLY:
                result = accountPerformance.getYearlyPerformanceData();
                break;
            default:
                result = new ArrayList<>();
                break;
        }

        return result;
    }

    protected PerformancePeriodType getPerformancePeriodType(Map<String, Object> params, Map<String, Object> dataCollections) {
        PeriodicPerformance performance = getPerformanceSummary(params, dataCollections);
        return PerformanceReportUtil.getPerformancePeriod(performance).get("summaryPeriodType");
    }

    protected abstract NetReturnData.ReturnMode getReturnMode();

    protected abstract Performance getInceptionPerformance(Map<String, Object> params, Map<String, Object> dataCollections);

    protected abstract AccountPerformanceChartDto getChartData(Map<String, Object> params, Map<String, Object> dataCollections);

    protected abstract PeriodicPerformance getPerformanceSummary(Map<String, Object> params,
            Map<String, Object> dataCollections);

    protected abstract String getBenchmarkName(Map<String, Object> params, Map<String, Object> dataCollections);
}