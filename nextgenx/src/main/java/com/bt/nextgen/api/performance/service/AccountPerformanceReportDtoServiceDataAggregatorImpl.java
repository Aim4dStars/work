package com.bt.nextgen.api.performance.service;

import com.bt.nextgen.api.performance.model.AccountPerformanceKey;
import com.bt.nextgen.api.performance.model.AccountPerformanceReportDto;
import com.bt.nextgen.api.performance.model.PerformanceReportDataDto;
import com.bt.nextgen.service.avaloq.performance.PerformanceImpl;
import com.bt.nextgen.service.integration.performance.Performance;
import com.bt.nextgen.service.integration.performance.PerformancePeriodType;
import com.bt.nextgen.service.integration.portfolio.performance.WrapAccountPerformance;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

@Component
class AccountPerformanceReportDtoServiceDataAggregatorImpl {

    public AccountPerformanceReportDto buildReportDto(AccountPerformanceKey performanceKey,
            WrapAccountPerformance accountPerformance, Performance incepPerf) {      
        
        Map<String, PerformancePeriodType> periodTypes = PerformanceReportUtil.getPerformancePeriod(accountPerformance);
        PerformancePeriodType summaryPeriodType = periodTypes.get("summaryPeriodType");

        List<Performance> tablePerformanceData = getPerformanceData(summaryPeriodType, accountPerformance, incepPerf);

        if (tablePerformanceData.isEmpty()) {
            return new AccountPerformanceReportDto(performanceKey, null, null, null);
        }
        
        Performance periodPerf = accountPerformance.getPeriodPerformanceData();
        periodPerf = periodPerf == null ? new PerformanceImpl() : periodPerf;
        Performance incepPerformance = incepPerf == null ? new PerformanceImpl() : incepPerf;
        
        // Create a list for the table data
        List<Performance> tablePerformanceDataForTableList = new ArrayList<Performance>(tablePerformanceData); 
        tablePerformanceDataForTableList.add(periodPerf);
        tablePerformanceDataForTableList.add(incepPerformance);
        
        List<PerformanceReportDataDto> dataList = constructTableData(performanceKey, tablePerformanceDataForTableList);
        List<PerformanceReportDataDto> netDataList = constructNetTableData(tablePerformanceDataForTableList);
                
        return new AccountPerformanceReportDto(performanceKey, dataList, netDataList, buildReportColumnHeaders(
                tablePerformanceData, summaryPeriodType));
    }

    private List<PerformanceReportDataDto> constructTableData(AccountPerformanceKey performanceKey,
            List<Performance> tablePerformanceDataForTableList) {

        // Add row data in order as specified in report-table.
        List<PerformanceReportDataDto> dataList = new ArrayList<>();
        dataList.add(new PerformanceReportDataDto("Total performance", extract(tablePerformanceDataForTableList, on(Performance.class).getPerformance())));
        dataList.add(new PerformanceReportDataDto("  Capital return", extract(tablePerformanceDataForTableList, on(Performance.class).getCapitalGrowth())));
        dataList.add(new PerformanceReportDataDto("  Income return", extract(tablePerformanceDataForTableList, on(Performance.class).getIncomeRtn())));

        if (!"-1".equals(performanceKey.getBenchmarkId())) {
            dataList.add(new PerformanceReportDataDto("Selected benchmark", extract(tablePerformanceDataForTableList, on(Performance.class).getBmrkRor())));
            dataList.add(new PerformanceReportDataDto("Active return", extract(tablePerformanceDataForTableList, on(Performance.class).getActiveRor())));
        }

        return dataList;
    }

    private List<PerformanceReportDataDto> constructNetTableData(List<Performance> tablePerformanceDataForTableList) {
         // Add row data in order as specified in report-table.
        List<PerformanceReportDataDto> dataList = new ArrayList<>();
        dataList.add(new PerformanceReportDataDto("Opening Balance", extract(tablePerformanceDataForTableList, on(Performance.class).getOpeningBalance())));
        dataList.add(new PerformanceReportDataDto("    Inflows", extract(tablePerformanceDataForTableList, on(Performance.class).getInflows())));
        dataList.add(new PerformanceReportDataDto("    Outflows", extract(tablePerformanceDataForTableList, on(Performance.class).getOutflows())));
        dataList.add(new PerformanceReportDataDto("    Income", extract(tablePerformanceDataForTableList, on(Performance.class).getIncome())));
        dataList.add(new PerformanceReportDataDto("    Expenses", extract(tablePerformanceDataForTableList, on(Performance.class).getExpenses())));
        dataList.add(new PerformanceReportDataDto("    Market Movement", extract(tablePerformanceDataForTableList, on(Performance.class).getMktMvt())));
        dataList.add(new PerformanceReportDataDto("Closing balance before fees", extract(tablePerformanceDataForTableList,
                on(Performance.class).getClosingBalanceBeforeFee())));
        dataList.add(new PerformanceReportDataDto("    Fees", extract(tablePerformanceDataForTableList, on(Performance.class).getFee())));
        dataList.add(new PerformanceReportDataDto("    Other Fees", extract(tablePerformanceDataForTableList, on(Performance.class).getOtherFee())));
        dataList.add(new PerformanceReportDataDto("Closing balance after fees", extract(tablePerformanceDataForTableList,
                on(Performance.class).getClosingBalanceAfterFee())));
        dataList.add(new PerformanceReportDataDto("Your account $ return", extract(tablePerformanceDataForTableList, on(Performance.class).getNetGainLoss())));

        return dataList;
    }

    private List<String> buildReportColumnHeaders(List<Performance> tablePerformanceData, PerformancePeriodType periodType) {
        List<String> columnHeaders = PerformanceReportUtil.buildReportColumnHeaders(tablePerformanceData, periodType);
        if (columnHeaders != null) {
            columnHeaders.add("Period<br/>return");
            columnHeaders.add("Since<br/>inception");
        }

        return columnHeaders;
    }

    private List<Performance> getPerformanceData(PerformancePeriodType periodType, WrapAccountPerformance performance,
            Performance incepPerf) {
        List<Performance> result = new ArrayList<>();

        if (incepPerf != null) {
            result.add(incepPerf);
        }

        if (periodType == null) {
            return result;
        }

        switch (periodType) {
            case DAILY:
                result = performance.getDailyPerformanceData();
                break;
            case MONTHLY:
                result = performance.getMonthlyPerformanceData();
                break;
            case QUARTERLY:
                result = performance.getQuarterlyPerformanceData();
                break;
            case WEEKLY:
                result = performance.getWeeklyPerformanceData();
                break;
            case YEARLY:
                result = performance.getYearlyPerformanceData();
                break;
            default:
                break;
        }

        return result;
    }

}
