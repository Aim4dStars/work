package com.bt.nextgen.reports.account.income;

import java.util.Map;

import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;

@Report("incomeAccruedCSVReport")
public class IncomeAccruedCSVReport extends AbstractIncomeReport {

    @ReportBean("reportType")
    @SuppressWarnings("squid:S1172")
    public String getReportType(Map<String, String> params) {
        return "Income Accrued";

    }
}
