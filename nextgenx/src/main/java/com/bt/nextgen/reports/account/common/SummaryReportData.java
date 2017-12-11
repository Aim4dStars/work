package com.bt.nextgen.reports.account.common;

public class SummaryReportData {

    private String summaryText;
    private String summaryBalance;
    private String summaryPercentage;
    private Boolean displayPercentage;

    public SummaryReportData(String summaryText, String summaryBalance, String summaryPercentage, Boolean displayPercentage) {
        this.summaryText = summaryText;
        this.summaryBalance = summaryBalance;
        this.summaryPercentage = summaryPercentage;
        this.displayPercentage = displayPercentage;
    }

    public String getSummaryText() {
        return summaryText;
    }

    public String getSummaryBalance() {
        return summaryBalance;
    }

    public String getSummaryPercentage() {
        return summaryPercentage;
    }
    
    public Boolean getDisplayPercentage() {
        return displayPercentage;
    }
}
