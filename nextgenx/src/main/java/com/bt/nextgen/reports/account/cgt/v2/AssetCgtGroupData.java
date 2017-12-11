package com.bt.nextgen.reports.account.cgt.v2;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;

public class AssetCgtGroupData {
    private String assetCode;
    private String assetName;
    private List<AssetParcelsData> assetParcels;

    public AssetCgtGroupData(String assetCode, String assetName, List<AssetParcelsData> assetParcels) {
        super();
        this.assetCode = assetCode;
        this.assetName = assetName;
        this.assetParcels = assetParcels;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public String getAssetName() {
        return assetName;
    }

    public String getName() {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(assetCode)) {
            builder.append("<b>");
            builder.append(assetCode);
            builder.append(" &#183 ");
            builder.append("</b> ");
        }
        builder.append(assetName);
        return builder.toString();
    }

    public List<AssetParcelsData> getAssetParcels() {
        return assetParcels;
    }

    protected BigDecimal getCalculatedGrossGain() {
        BigDecimal grossGain = null;
        if (assetParcels != null) {
            for (AssetParcelsData parcel : assetParcels) {
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
        if (assetParcels != null) {
            for (AssetParcelsData parcel : assetParcels) {
                amount = amount.add(parcel.getCalculatedAmount());
            }
        }
        return amount;
    }

    public String getAmount() {
        BigDecimal calculatedAmount = getCalculatedAmount();
        return ReportFormatter.format(ReportFormat.CURRENCY, calculatedAmount);
    }

    protected BigDecimal getCalculatedCostBase() {
        BigDecimal costBase = BigDecimal.ZERO;
        for (AssetParcelsData parcel : assetParcels) {
            costBase = costBase.add(parcel.getCalculatedCostBase());
        }
        return costBase;
    }

    public String getCostBase() {
        BigDecimal calculatedCostBase = getCalculatedCostBase();
        return ReportFormatter.format(ReportFormat.CURRENCY, calculatedCostBase);
    }

    protected BigDecimal getCalculatedTaxAmount() {
        BigDecimal taxAmount = BigDecimal.ZERO;
        if (assetParcels != null) {
            for (AssetParcelsData parcel : assetParcels) {
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
