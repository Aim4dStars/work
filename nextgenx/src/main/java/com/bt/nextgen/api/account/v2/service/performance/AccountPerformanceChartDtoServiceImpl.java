package com.bt.nextgen.api.account.v2.service.performance;

import com.bt.nextgen.api.account.v2.model.DateRangeAccountKey;
import com.bt.nextgen.api.account.v2.model.DateValueDto;
import com.bt.nextgen.api.account.v2.model.performance.AccountPerformance;
import com.bt.nextgen.api.account.v2.model.performance.AccountPerformanceImpl;
import com.bt.nextgen.api.account.v2.model.performance.PerformanceSummaryDto;
import com.bt.nextgen.api.account.v2.util.PerformanceReportUtil;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Deprecated
@Service("AccountPerformanceChartDtoServiceImplV2")
public class AccountPerformanceChartDtoServiceImpl implements AccountPerformanceChartDtoService {

    @Autowired
    private AccountPerformanceIntegrationService accountService;

    public AccountPerformance find(DateRangeAccountKey key, ServiceErrors serviceErrors) {
        String accountId = EncodedString.toPlainText(key.getAccountId());

        WrapAccountPerformanceImpl perfSummary = (WrapAccountPerformanceImpl) accountService.loadAccountPerformanceReport(
                AccountKey.valueOf(accountId), null, key.getStartDate(), key.getEndDate(), serviceErrors);

        return buildAccountPerformance(key, perfSummary);
    }

    protected AccountPerformance buildAccountPerformance(DateRangeAccountKey key, WrapAccountPerformance accountPerformance) {
        List<Performance> barGraphPerformanceData = null;
        List<Performance> lineGraphPerformanceData = null;

        PerformancePeriodType barGraphPeriodType = PerformanceReportUtil
                .getAccountPerformancePeriodForBarGraph(accountPerformance);
        PerformancePeriodType lineGraphPeriodType = PerformanceReportUtil
                .getAccountPerformancePeriodForLineGraph(accountPerformance);

        barGraphPerformanceData = PerformanceReportUtil.getPerformanceData(barGraphPeriodType, accountPerformance);
        lineGraphPerformanceData = PerformanceReportUtil.getPerformanceData(lineGraphPeriodType, accountPerformance);

        PerformanceSummaryDto<DateRangeAccountKey> periodPerformance = getPeriodPerformanceData(key, accountPerformance);

        AccountPerformance performance = createAccountPerformance(key, barGraphPerformanceData, lineGraphPerformanceData,
                periodPerformance, lineGraphPeriodType, barGraphPeriodType,
                PerformanceReportUtil.buildReportColumnHeaders(barGraphPerformanceData, barGraphPeriodType));

        return performance;
    }

    private List<DateValueDto> createTotalPerformanceChartData(List<Performance> performanceData) {
        List<DateValueDto> totalPerfChartData = new ArrayList<>();
        if (performanceData != null) {
            for (Performance data : performanceData) {
                BigDecimal performance = data.getPerformance() != null ? data.getPerformance().divide(BigDecimal.valueOf(100))
                        : null;
                totalPerfChartData.add(new DateValueDto(data.getPeriodSop(), performance));
            }
        }
        return totalPerfChartData;
    }

    private List<DateValueDto> createCapitalPerformanceChartData(List<Performance> performanceData) {
        List<DateValueDto> capitalPerfChartData = new ArrayList<>();
        if (performanceData != null) {
            for (Performance data : performanceData) {
                BigDecimal capitalGrowth = data.getCapitalGrowth() != null ? data.getCapitalGrowth().divide(
                        BigDecimal.valueOf(100)) : null;
                capitalPerfChartData.add(new DateValueDto(data.getPeriodSop(), capitalGrowth));
            }
        }
        return capitalPerfChartData;
    }

    private List<DateValueDto> createIncomePerformanceChartData(List<Performance> performanceData) {
        List<DateValueDto> incomePerfChartData = new ArrayList<>();
        if (performanceData != null) {
            for (Performance data : performanceData) {
                BigDecimal incomeReturn = data.getIncomeRtn() != null ? data.getIncomeRtn().divide(BigDecimal.valueOf(100))
                        : null;
                incomePerfChartData.add(new DateValueDto(data.getPeriodSop(), incomeReturn));
            }
        }
        return incomePerfChartData;
    }

    private List<DateValueDto> createPeriodDollarChartData(List<Performance> performanceData) {
        List<DateValueDto> periodDollarChartData = new ArrayList<>();
        if (performanceData != null) {
            for (Performance data : performanceData) {
                periodDollarChartData.add(new DateValueDto(data.getPeriodSop(), data.getNetGainLoss()));
            }
        }
        return periodDollarChartData;
    }

    private List<DateValueDto> createCumulativePerformanceChartData(List<Performance> performanceData) {
        List<DateValueDto> cumulativePerfChartData = new ArrayList<>();
        if (performanceData != null) {
            for (Performance data : performanceData) {
                BigDecimal cumulativePerformance = data.getTwrrAccum() != null ? data.getTwrrAccum().divide(
                        BigDecimal.valueOf(100)) : null;
                cumulativePerfChartData.add(new DateValueDto(data.getPeriodSop(), cumulativePerformance));
            }
        }
        return cumulativePerfChartData;
    }

    private List<DateValueDto> createPortfolioValueChartData(List<Performance> performanceData) {
        List<DateValueDto> portfolioValueChartData = new ArrayList<>();
        if (performanceData != null) {
            for (Performance data : performanceData) {
                portfolioValueChartData.add(new DateValueDto(data.getPeriodSop(), data.getClosingBalanceAfterFee()));
            }
        }
        return portfolioValueChartData;
    }

    private PerformanceSummaryDto<DateRangeAccountKey> getPeriodPerformanceData(DateRangeAccountKey key,
            WrapAccountPerformance accountPerformance) {
        BigDecimal percentagePeriodReturn = null;
        BigDecimal dollarPeriodReturn = null;
        BigDecimal capitalPerformanceForPeriod = null;
        BigDecimal incomePerformanceForPeriod = null;
        Performance periodPerformanceData = accountPerformance.getPeriodPerformanceData();
        if (periodPerformanceData != null) {
            percentagePeriodReturn = periodPerformanceData.getPerformance() != null ? periodPerformanceData.getPerformance()
                    .divide(BigDecimal.valueOf(100)) : null;
            dollarPeriodReturn = accountPerformance.getPeriodPerformanceData().getNetGainLoss();
            capitalPerformanceForPeriod = periodPerformanceData.getCapitalGrowth() != null ? periodPerformanceData
                    .getCapitalGrowth().divide(BigDecimal.valueOf(100)) : null;
            incomePerformanceForPeriod = periodPerformanceData.getIncomeRtn() != null ? periodPerformanceData.getIncomeRtn()
                    .divide(BigDecimal.valueOf(100)) : null;
        }

        PerformanceSummaryDto<DateRangeAccountKey> summary = new PerformanceSummaryDto<>(key, percentagePeriodReturn,
                dollarPeriodReturn, capitalPerformanceForPeriod, incomePerformanceForPeriod);

        return summary;
    }

    private AccountPerformance createAccountPerformance(DateRangeAccountKey key, List<Performance> tablePerformanceData,
            List<Performance> chartPerformanceData, PerformanceSummaryDto<DateRangeAccountKey> periodPerformance,
            PerformancePeriodType detailedPeriodType, PerformancePeriodType summaryPeriodType, List<String> columnHeaders) {
        AccountPerformanceImpl performance = new AccountPerformanceImpl();
        performance.setKey(key);
        performance.setPeriodPerformance(createTotalPerformanceChartData(tablePerformanceData));
        performance.setCapitalPerformance(createCapitalPerformanceChartData(tablePerformanceData));
        performance.setIncomePerformance(createIncomePerformanceChartData(tablePerformanceData));
        performance.setPeriodDollar(createPeriodDollarChartData(tablePerformanceData));
        performance.setCumulativePerformance(createCumulativePerformanceChartData(chartPerformanceData));
        performance.setPortfolioValue(createPortfolioValueChartData(chartPerformanceData));
        performance.setDetailedPeriodType(detailedPeriodType);
        performance.setSummaryPeriodType(summaryPeriodType);
        performance.setPerformanceSummaryDto(periodPerformance);
        performance.setColHeaders(columnHeaders);
        return performance;
    }
}
