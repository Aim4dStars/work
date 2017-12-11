package com.bt.nextgen.reports.advisermodel;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioAssetAllocationDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;

import java.math.BigDecimal;

public class AdviserModelAllocationReportData {

    private String assetCode;
    private String assetName;
    private BigDecimal allocationPercent;
    private BigDecimal tolerancePercent;

    public AdviserModelAllocationReportData(ModelPortfolioAssetAllocationDto allocation) {
        this.assetCode = allocation.getAssetCode();
        this.assetName = allocation.getAssetName();
        this.allocationPercent = allocation.getAssetAllocation();
        this.tolerancePercent = allocation.getToleranceLimit();
    }

    public AdviserModelAllocationReportData(String assetCode, String assetName, BigDecimal allocationPercent,
            BigDecimal tolerancePercent) {
        this.assetCode = assetCode;
        this.assetName = assetName;
        this.allocationPercent = allocationPercent;
        this.tolerancePercent = tolerancePercent;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public String getAssetName() {
        return assetName;
    }

    public String getAllocationPercent() {
        return ReportFormatter.format(ReportFormat.PERCENTAGE, false, allocationPercent);
    }

    public String getTolerancePercent() {
        return ReportFormatter.format(ReportFormat.PERCENTAGE, false, tolerancePercent);
    }

}
