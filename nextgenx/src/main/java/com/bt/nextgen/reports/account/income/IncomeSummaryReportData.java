package com.bt.nextgen.reports.account.income;

import com.bt.nextgen.api.income.v2.model.IncomeValueTotals;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;

public class IncomeSummaryReportData {
    private IncomeValueTotals incomeValueTotals;

    public IncomeSummaryReportData(IncomeValueTotals incomeValueTotals) {
        this.incomeValueTotals = incomeValueTotals;
    }

    public String getIncomeTotal() {
        return ReportFormatter.format(ReportFormat.CURRENCY, incomeValueTotals.getIncomeTotal());
    }

    public String getInterestTotal() {
        return ReportFormatter.format(ReportFormat.CURRENCY, incomeValueTotals.getInterestTotal());
    }

    public String getDividendTotal() {
        return ReportFormatter.format(ReportFormat.CURRENCY, incomeValueTotals.getDividendTotal());
    }

    public String getFrankedDividendTotal() {
        return ReportFormatter.format(ReportFormat.CURRENCY, incomeValueTotals.getFrankedDividendTotal());
    }

    public String getUnfrankedDividendTotal() {
        return ReportFormatter.format(ReportFormat.CURRENCY, incomeValueTotals.getUnfrankedDividendTotal());
    }

    public String getDistributionTotal() {
        return ReportFormatter.format(ReportFormat.CURRENCY, incomeValueTotals.getDistributionTotal());
    }
}
