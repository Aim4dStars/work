package com.bt.nextgen.reports.account.income;

import java.util.List;

public class IncomeReportData {
    private IncomeValueReportData incomeValueReportData = null;
    private IncomeSummaryReportData incomeSummaryReportData = null;
    private static final String TOTAL = "Total income ";

    public IncomeReportData(IncomeValueReportData incomeValueReportData, IncomeSummaryReportData incomeSummaryReportData) {
        this.incomeValueReportData = incomeValueReportData;
        this.incomeSummaryReportData = incomeSummaryReportData;
    }

    public List<IncomeValueReportData> getIncomeCategories() {
        return incomeValueReportData.getIncomeValuesReportData();
    }

    public IncomeSummaryReportData getIncomeSummaryReportData() {
        return incomeSummaryReportData;
    }

    public String getIncomeTotal() {
        return incomeValueReportData.getAmount();
    }

    public String getSummaryDescription() {
        return new StringBuilder(TOTAL).append(incomeValueReportData.getReportType().toLowerCase()).toString();
    }
}
