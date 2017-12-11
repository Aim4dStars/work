package com.bt.nextgen.reports.account.cgt.v2;

import java.math.BigDecimal;
import java.util.List;

import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;

public class CgtGroupReportData {
    private List<AssetTypeCgtGroupData> cgtAssetGroups;
    private BigDecimal totalAmount;
    private BigDecimal totalTaxAmount;
    private BigDecimal totalGrossGain;
    private BigDecimal totalCostBase;
    private String reportTitle;

    public CgtGroupReportData(List<AssetTypeCgtGroupData> cgtAssetGroups, BigDecimal totalAmount, BigDecimal totalTaxAmount,
            BigDecimal totalGrossGain, BigDecimal totalCostBase, String reportTitle) {
        this.cgtAssetGroups = cgtAssetGroups;
        this.totalAmount = totalAmount;
        this.totalTaxAmount = totalTaxAmount;
        this.totalGrossGain = totalGrossGain;
        this.totalCostBase = totalCostBase;
        this.reportTitle = reportTitle;
    }

    public List<AssetTypeCgtGroupData> getCgtAssetGroups() {
        return cgtAssetGroups;
    }

    public String getTotalAmount() {
        return ReportFormatter.format(ReportFormat.CURRENCY, totalAmount);
    }

    public String getTotalTaxAmount() {
        return ReportFormatter.format(ReportFormat.CURRENCY, totalTaxAmount);
    }

    public String getTotalGrossGain() {
        return ReportFormatter.format(ReportFormat.CURRENCY, totalGrossGain);
    }

    public String getTotalCostBase() {
        return ReportFormatter.format(ReportFormat.CURRENCY, totalCostBase);
    }

    public Boolean getIsRealised() {
        return reportTitle.equals("Realised capital gains tax") ? true : false;
    }
}
