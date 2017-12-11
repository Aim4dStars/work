package com.bt.nextgen.reports.sample;

import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

public class ContributionReportData {
    private String type;
    private BigDecimal totalContributions;
    private BigDecimal lastContributionAmount;
    private DateTime lastContributionDate;
    private List<ContributionReportDataItem> dataItems;


    public ContributionReportData(String type, BigDecimal totalContributions, BigDecimal lastContributionAmount,
            DateTime lastContributionDate, List<ContributionReportDataItem> dataItems) {
        this.type = type;
        this.totalContributions = totalContributions;
        this.lastContributionAmount = lastContributionAmount;
        this.lastContributionDate = lastContributionDate;
        this.dataItems = dataItems;
    }

    public ContributionReportData(String type, BigDecimal totalContributions, List<ContributionReportDataItem> dataItems) {
        this.type = type;
        this.totalContributions = totalContributions;
        this.lastContributionAmount = null;
        this.lastContributionDate = null;
        this.dataItems = dataItems;
    }

    public List<ContributionReportDataItem> getDataItems() {
        return dataItems;
    }

    public String getTotalContributions() {
        return ReportFormatter.format(ReportFormat.CURRENCY, totalContributions);
    }

    public String getType() {
        return type;
    }

    public String getLastContributionAmount() {
        return ReportFormatter.format(ReportFormat.CURRENCY, lastContributionAmount);
    }

    public String getLastContributionDate() {
        return ReportFormatter.format(ReportFormat.SHORT_DATE, lastContributionDate);
    }

}
