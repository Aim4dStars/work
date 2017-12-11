package com.bt.nextgen.api.performance.service;

import com.bt.nextgen.api.performance.model.AccountPerformanceChartDto;
import com.bt.nextgen.api.performance.model.AccountPerformanceKey;
import com.bt.nextgen.api.performance.model.ReportDataPointDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.portfolio.performance.WrapAccountPerformanceImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.performance.Performance;
import com.bt.nextgen.service.integration.performance.PerformancePeriodType;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceIntegrationService;
import com.bt.nextgen.service.integration.portfolio.performance.WrapAccountPerformance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AccountPerformanceChartDtoServiceImpl implements AccountPerformanceChartDtoService {

    @Autowired
    private AccountPerformanceIntegrationService accountService;

    public AccountPerformanceChartDto find(AccountPerformanceKey key, ServiceErrors serviceErrors) {
        String accountId = EncodedString.toPlainText(key.getAccountId());

        String bmrk = key.getBenchmarkId();
        int bmrkId = key.getBenchmarkId() == null ? null : Integer.parseInt(bmrk);
        if (bmrkId <= 0) {
            bmrk = null;
        }

        WrapAccountPerformanceImpl perfSummary = (WrapAccountPerformanceImpl) accountService.loadAccountPerformanceReport(
                AccountKey.valueOf(accountId), bmrk, key.getStartDate(), key.getEndDate(), serviceErrors);

        if (perfSummary.getAccountKey() == null) {
            return null;
        }

        return buildChartDto(key, perfSummary);
    }

    protected AccountPerformanceChartDto buildChartDto(AccountPerformanceKey performanceKey,
            WrapAccountPerformance accountPerformance) {

        Map<String, PerformancePeriodType> periodTypes = PerformanceReportUtil.getPerformancePeriod(accountPerformance);
        PerformancePeriodType summaryPeriodType = periodTypes.get("summaryPeriodType");
        PerformancePeriodType detailedPeriodType = periodTypes.get("detailedPeriodType");

        List<Performance> tablePerformanceData = getPerformanceData(summaryPeriodType, accountPerformance);
        List<Performance> chartPerformanceData = getPerformanceData(detailedPeriodType, accountPerformance);

        List<ReportDataPointDto> totalPerfChartData = new ArrayList<>();
        List<ReportDataPointDto> benchmarkChartData = new ArrayList<>();
        for (Performance data : chartPerformanceData) {
            totalPerfChartData.add(new ReportDataPointDto(data.getPeriodSop(), data.getTwrrAccum()));
            benchmarkChartData.add(new ReportDataPointDto(data.getPeriodSop(), data.getBmrkRor()));
        }

        List<ReportDataPointDto> activeReturnChartData = new ArrayList<>();
        for (Performance data : tablePerformanceData) {
            activeReturnChartData.add(new ReportDataPointDto(data.getPeriodSop(), data.getActiveRor()));
        }

        AccountPerformanceChartDto dto = new AccountPerformanceChartDto(performanceKey, activeReturnChartData,
                totalPerfChartData, benchmarkChartData, detailedPeriodType, summaryPeriodType,
                PerformanceReportUtil.buildReportColumnHeaders(tablePerformanceData, summaryPeriodType));

        return dto;
    }

    protected List<Performance> getPerformanceData(PerformancePeriodType periodType, WrapAccountPerformance performance) {
        List<Performance> result = new ArrayList<>();

        if (periodType == null) {
            return result;
        }

        switch (periodType) {
        case DAILY:
            return performance.getDailyPerformanceData();
        case MONTHLY:
            return performance.getMonthlyPerformanceData();
        case QUARTERLY:
            return performance.getQuarterlyPerformanceData();
        case WEEKLY:
            return performance.getWeeklyPerformanceData();
        case YEARLY:
            return performance.getYearlyPerformanceData();
        default:
            return result;
        }
    }
}
