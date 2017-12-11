package com.bt.nextgen.reports.account.drawdownstrategy;

import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

public class AssetPriorityReportData {

    private static final String SEPARATOR = " &#183 ";
    private static final String SUSPENDED_STATUS = "SUSPENDED";

    private String assetCode;
    private String assetName;
    private String assetStatus;
    private BigDecimal marketValue;
    private Integer priority;

    public AssetPriorityReportData(String assetCode, String assetName, String assetStatus, BigDecimal marketValue,
            Integer priority) {
        this.assetCode = assetCode;
        this.assetName = assetName;
        this.assetStatus = assetStatus;
        this.marketValue = marketValue;
        this.priority = priority;
    }

    public String getAssetTitle() {
        StringBuilder title = new StringBuilder();

        if (StringUtils.isNotBlank(assetCode)) {
            title.append("<b>");
            title.append(assetCode);
            title.append("</b>");
            title.append(SEPARATOR);
        }

        title.append(assetName);

        return title.toString();
    }

    public String getAssetStatus() {
        if (assetStatus != null && SUSPENDED_STATUS.equalsIgnoreCase(assetStatus)) {
            return SUSPENDED_STATUS;
        }
        return null;
    }

    public String getMarketValue() {
        return ReportFormatter.format(ReportFormat.CURRENCY, true, marketValue);
    }

    public String getPriority() {
        return priority.toString();
    }
}
