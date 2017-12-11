package com.bt.nextgen.api.performance.service;

import com.bt.nextgen.api.performance.model.AccountNetReturnChartDto;
import com.bt.nextgen.api.performance.model.AccountPerformanceKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.performance.PerformanceImpl;
import com.bt.nextgen.service.avaloq.performance.PeriodicPerformanceImpl;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.performance.Performance;
import com.bt.nextgen.service.integration.performance.PerformancePeriodType;
import com.bt.nextgen.service.integration.performance.PeriodicPerformance;
import com.bt.nextgen.service.integration.portfolio.performance.SubAccountPerformanceIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SubAccountNetReturnChartDtoServiceImpl implements SubAccountNetReturnChartDtoService {

    @Autowired
    private SubAccountPerformanceIntegrationService accountService;

    @Override
    public AccountNetReturnChartDto find(AccountPerformanceKey key, ServiceErrors serviceErrors) {
        String accountId = EncodedString.toPlainText(key.getAccountId());

        PeriodicPerformanceImpl perfSummary = (PeriodicPerformanceImpl) accountService
                .loadPerformanceData(SubAccountKey.valueOf(accountId), key.getStartDate(), key.getEndDate(), serviceErrors);

        return buildChartDto(key, perfSummary);
    }

    protected AccountNetReturnChartDto buildChartDto(AccountPerformanceKey performanceKey,
            PeriodicPerformance accountPerformance) {

        Map<String, PerformancePeriodType> periodTypes = PerformanceReportUtil.getPerformancePeriod(accountPerformance);
        PerformancePeriodType summaryPeriodType = periodTypes.get("summaryPeriodType");

        List<Performance> performanceData = PerformanceReportUtil.getPerformanceData(summaryPeriodType, accountPerformance);
        Performance periodPerf = accountPerformance.getPeriodPerformanceData();

        if (periodPerf == null) {
            periodPerf = new PerformanceImpl();
        }

        List<BigDecimal> totalClosingBalanceAftFees = new ArrayList<>();
        List<BigDecimal> totalAccountReturn = new ArrayList<>();

        // Setup corresponding data in row-wise from the response.
        for (int i = 0; i < performanceData.size(); i++) {
            Performance p = performanceData.get(i);

            totalClosingBalanceAftFees.add(p.getClosingBalanceAfterFee());
            totalAccountReturn.add(p.getNetGainLoss());
        }

        AccountNetReturnChartDto dto = new AccountNetReturnChartDto(performanceKey, totalClosingBalanceAftFees,
                totalAccountReturn, PerformanceReportUtil.buildReportColumnHeaders(performanceData, summaryPeriodType));

        return dto;
    }
}
