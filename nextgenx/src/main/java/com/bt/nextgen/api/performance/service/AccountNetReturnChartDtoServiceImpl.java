package com.bt.nextgen.api.performance.service;

import com.bt.nextgen.api.performance.model.AccountNetReturnChartDto;
import com.bt.nextgen.api.performance.model.AccountPerformanceKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.performance.PerformanceImpl;
import com.bt.nextgen.service.avaloq.portfolio.performance.WrapAccountPerformanceImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.performance.Performance;
import com.bt.nextgen.service.integration.performance.PerformancePeriodType;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceIntegrationService;
import com.bt.nextgen.service.integration.portfolio.performance.WrapAccountPerformance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AccountNetReturnChartDtoServiceImpl implements AccountNetReturnChartDtoService {

    @Autowired
    private AccountPerformanceIntegrationService accountService;

    @Override
    public AccountNetReturnChartDto find(AccountPerformanceKey key, ServiceErrors serviceErrors) {
        String accountId = EncodedString.toPlainText(key.getAccountId());

        WrapAccountPerformanceImpl perfSummary = (WrapAccountPerformanceImpl) accountService.loadAccountPerformanceReport(
                AccountKey.valueOf(accountId), null, key.getStartDate(), key.getEndDate(), serviceErrors);

        return buildChartDto(key, perfSummary);
    }

    protected AccountNetReturnChartDto buildChartDto(AccountPerformanceKey performanceKey,
            WrapAccountPerformance accountPerformance) {

        Map<String, PerformancePeriodType> periodTypes = PerformanceReportUtil.getPerformancePeriod(accountPerformance);
        PerformancePeriodType summaryPeriodType = periodTypes.get("summaryPeriodType");

        List<Performance> performanceData = getPerformanceData(summaryPeriodType, accountPerformance);
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
