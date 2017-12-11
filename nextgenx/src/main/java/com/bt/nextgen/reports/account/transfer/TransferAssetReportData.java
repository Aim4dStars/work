package com.bt.nextgen.reports.account.transfer;

import com.bt.nextgen.api.inspecietransfer.v3.model.TransferAssetDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;

import java.math.BigDecimal;
import java.util.List;

public class TransferAssetReportData {

    private String assetCode;

    private String assetName;

    private String assetType;

    private String amount;

    private BigDecimal quantity;

    private boolean isCashTransfer;

    private List<String> warnings;

    public TransferAssetReportData(TransferAssetDto assetDto) {
        this.assetCode = assetDto.getAsset().getAssetCode();
        this.assetName = assetDto.getAsset().getAssetName();
        this.assetType = assetDto.getAsset().getAssetType();

        this.quantity = assetDto.getQuantity();
        this.amount = assetDto.getAmount();
        this.isCashTransfer = assetDto.getIsCashTransfer();
        this.warnings = assetDto.getVettWarnings();
    }

    public TransferAssetReportData(String assetName, String amount) {
        this.assetName = assetName;
        this.amount = amount;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public String getAssetName() {
        return assetName;
    }

    public String getAssetType() {
        return assetType;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public boolean getIsCashTransfer() {
        return isCashTransfer;
    }

    public String getAmount() {
        if (amount == null)
            return ReportFormatter.format(ReportFormat.CURRENCY, quantity);
        return amount;
    }

    public List<String> getWarnings() {
        return warnings;
    }

}
