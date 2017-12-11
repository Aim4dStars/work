package com.bt.nextgen.reports.sample;

import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;

import java.math.BigDecimal;

public class ContributionReportDataItem {
    private String description;
    private BigDecimal currentFyAmount;
    private BigDecimal capAmount;
    private BigDecimal availableAmount;

    public ContributionReportDataItem(String description, BigDecimal currentFyAmount, BigDecimal capAmount,
            BigDecimal availableAmount) {
        super();
        this.description = description;
        this.currentFyAmount = currentFyAmount;
        this.capAmount = capAmount;
        this.availableAmount = availableAmount;
    }

    public ContributionReportDataItem(String description, BigDecimal currentFyAmount) {
        super();
        this.description = description;
        this.currentFyAmount = currentFyAmount;
        this.capAmount = null;
        this.availableAmount = null;
    }

    public String getDescription() {
        return description;
    }

    public String getCurrentFyAmount() {
        return ReportFormatter.format(ReportFormat.CURRENCY, currentFyAmount);
    }

    public String getAvailableAmount() {
        return ReportFormatter.format(ReportFormat.CURRENCY, availableAmount);
    }

    public String getCapAmount() {
        return ReportFormatter.format(ReportFormat.CURRENCY, capAmount);
    }
}
