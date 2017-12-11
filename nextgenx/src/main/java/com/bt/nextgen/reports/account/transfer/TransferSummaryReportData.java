package com.bt.nextgen.reports.account.transfer;

import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;

import java.math.BigDecimal;
import java.util.List;

public class TransferSummaryReportData {

    private BigDecimal totalTransferAmount;

    private BigDecimal nonCashTransferAmount;

    private BigDecimal cashTransferAmount;

    public TransferSummaryReportData(List<TransferAssetReportData> assetData) {
        super();
        summariseTransferData(assetData);
    }

    public TransferSummaryReportData(BigDecimal totalTransferAmount, BigDecimal nonCashTransferAmount) {
        this.totalTransferAmount = totalTransferAmount;
        this.nonCashTransferAmount = nonCashTransferAmount;
        this.cashTransferAmount = totalTransferAmount.subtract(nonCashTransferAmount);
    }

    private void summariseTransferData(List<TransferAssetReportData> assetData) {
        totalTransferAmount = BigDecimal.ZERO;
        nonCashTransferAmount = BigDecimal.ZERO;
        for (TransferAssetReportData data : assetData) {
            totalTransferAmount = totalTransferAmount.add(data.getQuantity());
            if (!data.getIsCashTransfer()) {
                nonCashTransferAmount = nonCashTransferAmount.add(data.getQuantity());
            }
        }

        cashTransferAmount = totalTransferAmount.subtract(nonCashTransferAmount);
    }

    public String getTotalTransferAmount() {
        return ReportFormatter.format(ReportFormat.CURRENCY, totalTransferAmount);
    }

    public String getNonCashTransferAmount() {
        return ReportFormatter.format(ReportFormat.CURRENCY, nonCashTransferAmount);
    }

    public String getCashTransferAmount() {
        return ReportFormatter.format(ReportFormat.CURRENCY, cashTransferAmount);
    }

}
