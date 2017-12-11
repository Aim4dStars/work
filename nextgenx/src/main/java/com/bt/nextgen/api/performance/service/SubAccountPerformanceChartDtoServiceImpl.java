package com.bt.nextgen.api.performance.service;

import com.bt.nextgen.api.performance.model.AccountPerformanceChartDto;
import com.bt.nextgen.api.performance.model.AccountPerformanceKey;
import com.bt.nextgen.api.performance.model.ReportDataPointDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.portfolio.performance.ManagedPortfolioPerformance;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.performance.Performance;
import com.bt.nextgen.service.integration.performance.PerformancePeriodType;
import com.bt.nextgen.service.integration.performance.PeriodicPerformance;
import com.bt.nextgen.service.integration.portfolio.performance.SubAccountPerformanceIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SubAccountPerformanceChartDtoServiceImpl implements SubAccountPerformanceChartDtoService {

    private static final Logger logger = LoggerFactory.getLogger(SubAccountPerformanceChartDtoServiceImpl.class);

    @Autowired
    private SubAccountPerformanceIntegrationService accountService;

    public AccountPerformanceChartDto find(AccountPerformanceKey key, ServiceErrors serviceErrors) {
        String accountId = EncodedString.toPlainText(key.getAccountId());

        ManagedPortfolioPerformance perfSummary = (ManagedPortfolioPerformance) accountService
                .loadPerformanceData(SubAccountKey.valueOf(accountId), key.getStartDate(), key.getEndDate(), serviceErrors);

        return buildChartDto(key, perfSummary);
    }

    protected AccountPerformanceChartDto buildChartDto(AccountPerformanceKey performanceKey,
            PeriodicPerformance accountPerformance) {

        Map<String, PerformancePeriodType> periodTypes = PerformanceReportUtil.getPerformancePeriod(accountPerformance);
        PerformancePeriodType detailedPeriodType = periodTypes.get("detailedPeriodType");
        PerformancePeriodType summaryPeriodType = periodTypes.get("summaryPeriodType");

        List<Performance> tablePerformanceData = PerformanceReportUtil.getPerformanceData(summaryPeriodType, accountPerformance);
        if (tablePerformanceData.isEmpty()) {
            logger.warn("No table-performance data available for period " + summaryPeriodType.getCode());
        }
        List<Performance> chartPerformanceData = PerformanceReportUtil.getPerformanceData(detailedPeriodType, accountPerformance);
        if (tablePerformanceData.isEmpty()) {
            logger.warn("No chart-performance data available for period " + detailedPeriodType.getCode());
        }

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
}
