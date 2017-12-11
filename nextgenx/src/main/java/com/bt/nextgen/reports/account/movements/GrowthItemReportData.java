package com.bt.nextgen.reports.account.movements;

import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;

import java.math.BigDecimal;
import java.util.List;

public class GrowthItemReportData {

    private BigDecimal balance;
    private String displayName;
    private Boolean isNested;
    private List<GrowthItemReportData> growthItems;

    public GrowthItemReportData(BigDecimal balance, String displayName, Boolean isNested, List<GrowthItemReportData> growthItems) {
        super();
        this.balance = balance;
        this.displayName = displayName;
        this.isNested = isNested;
        this.growthItems = growthItems;
    }

    public String getBalance() {
        return ReportFormatter.format(ReportFormat.CURRENCY, balance);
    }

    public String getDisplayName() {
        return displayName;
    }

    public Boolean getIsNested() {
        return isNested;
    }

    public List<GrowthItemReportData> getGrowthItems() {
        return growthItems;
    }

}
