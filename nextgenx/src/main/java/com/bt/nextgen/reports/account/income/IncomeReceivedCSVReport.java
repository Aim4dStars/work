package com.bt.nextgen.reports.account.income;

import java.util.Map;

import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;

@Report("incomeReceivedCSVReport")
public class IncomeReceivedCSVReport extends AbstractIncomeReport {

    @SuppressWarnings("squid:S1172")
    @ReportBean("reportType")
    public String getReportType(Map<String, String> params) {
        return "Income Received";
    }

}
