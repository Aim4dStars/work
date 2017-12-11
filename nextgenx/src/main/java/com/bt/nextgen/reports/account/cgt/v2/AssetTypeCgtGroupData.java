package com.bt.nextgen.reports.account.cgt.v2;

import java.math.BigDecimal;
import java.util.List;

import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.btfin.panorama.service.integration.asset.AssetType;

public class AssetTypeCgtGroupData {
    private AssetType assetType;
    private List<AssetCgtGroupData> assetGroups;

    public AssetTypeCgtGroupData(AssetType assetType, List<AssetCgtGroupData> assetGroups) {
        super();
        this.assetType = assetType;
        this.assetGroups = assetGroups;
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public String getGroupDescription() {
        return assetType.getGroupDescription();
    }

    public List<AssetCgtGroupData> getAssetGroups() {
        return assetGroups;
    }

    protected BigDecimal getCalculatedGrossGain() {
        BigDecimal grossGain = null;
        if (assetGroups != null) {
            for (AssetCgtGroupData parcel : assetGroups) {
                BigDecimal calculatedGrossGain = parcel.getCalculatedGrossGain();
                if (calculatedGrossGain != null) {
                    if (grossGain != null) {
                        grossGain = grossGain.add(calculatedGrossGain);
                    } else {
                        grossGain = calculatedGrossGain;
                    }
                }
            }
        }
        return grossGain;
    }

    public String getGrossGain() {
        BigDecimal calculatedGrossGain = getCalculatedGrossGain();
        return ReportFormatter.format(ReportFormat.CURRENCY, calculatedGrossGain);
    }

    protected BigDecimal getCalculatedAmount() {
        BigDecimal amount = BigDecimal.ZERO;
        if (assetGroups != null) {
            for (AssetCgtGroupData parcel : assetGroups) {
                amount = amount.add(parcel.getCalculatedAmount());
            }
        }
        return amount;
    }

    protected BigDecimal getCalculatedCostBase() {
        BigDecimal costBase = BigDecimal.ZERO;
        for (AssetCgtGroupData parcel : assetGroups) {
            costBase = costBase.add(parcel.getCalculatedCostBase());
        }
        return costBase;
    }

    public String getCostBase() {
        String costBase = "";
        BigDecimal calculatedCostBase = getCalculatedCostBase();
        costBase = ReportFormatter.format(ReportFormat.CURRENCY, calculatedCostBase);
        return costBase;
    }

    public String getAmount() {
        BigDecimal calculatedAmount = getCalculatedAmount();
        return ReportFormatter.format(ReportFormat.CURRENCY, calculatedAmount);
    }

    protected BigDecimal getCalculatedTaxAmount() {
        BigDecimal taxAmount = BigDecimal.ZERO;
        if (assetGroups != null) {
            for (AssetCgtGroupData parcel : assetGroups) {
                taxAmount = taxAmount.add(parcel.getCalculatedTaxAmount());
            }
        }
        return taxAmount;
    }

    public String getTaxAmount() {
        BigDecimal calculatedTaxAmount = getCalculatedTaxAmount();
        return ReportFormatter.format(ReportFormat.CURRENCY, calculatedTaxAmount);
    }

}
