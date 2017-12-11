package com.bt.nextgen.reports.account.cgt.v2;

import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import org.apache.commons.lang3.StringUtils;
import javax.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class AssetParcelsData {
    private String assetCode;
    private String assetName;
    private List<CgtSecurityData> parcels;




    public AssetParcelsData(String assetCode, String assetName,  @NotNull List<CgtSecurityData> parcels) {
        super();
        this.assetCode = assetCode;
        this.assetName = assetName;
        this.parcels = parcels;
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

    public List<CgtSecurityData> getParcels() {
        return parcels;
    }

    protected BigDecimal getCalculatedGrossGain() {
        BigDecimal grossGain = null;
        if (parcels != null) {
            for (CgtSecurityData parcel : parcels) {
                if (parcel.getCgtSecurity().getGrossGain() != null) {
                    if (grossGain != null) {
                        grossGain = grossGain.add(parcel.getCgtSecurity().getGrossGain());
                    } else {
                        grossGain = parcel.getCgtSecurity().getGrossGain();
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
        if (parcels != null) {
            for (CgtSecurityData parcel : parcels) {
                if (parcel.getAmount() != null) {
                    amount = amount.add(parcel.getCgtSecurity().getAmount());
                }
            }
        }
        return amount;
    }

    public String getAmount() {
        BigDecimal calculatedAmount = getCalculatedAmount();
        return ReportFormatter.format(ReportFormat.CURRENCY, calculatedAmount);
    }

    protected BigDecimal getCalculatedTaxAmount() {
        BigDecimal taxAmount = BigDecimal.ZERO;
        if (parcels != null) {
            for (CgtSecurityData parcel : parcels) {
                if (parcel.getTaxAmount() != null) {
                    taxAmount = taxAmount.add(parcel.getCgtSecurity().getTaxAmount());
                }
            }
        }
        return taxAmount;
    }

    public String getTaxAmount() {
        BigDecimal calculatedTaxAmount = getCalculatedTaxAmount();
        return ReportFormatter.format(ReportFormat.CURRENCY, calculatedTaxAmount);
    }

    protected Integer getCalculatedQuantityAmount() {
        Integer totalQuantity= 0;

            for (CgtSecurityData parcel : parcels) {
                if (null!=parcel.getCgtSecurity().getQuantity()) {
                    totalQuantity=totalQuantity+parcel.getCgtSecurity().getQuantity();
                }
            }

        return totalQuantity;
    }

    protected BigDecimal getCalculatedCostBase() {
        BigDecimal costBase = BigDecimal.ZERO;

            for (CgtSecurityData parcel : parcels) {
                if (null!=parcel.getCgtSecurity().getCostBase()) {
                    costBase = costBase.add(parcel.getCgtSecurity().getCostBase());
                }
            }

        return costBase;
    }
    protected BigDecimal getCalculatedIndexedCostBase() {
        BigDecimal indexedCostBase = BigDecimal.ZERO;

            for (CgtSecurityData parcel : parcels) {
                if (null!=parcel.getCgtSecurity().getIndexedCostBase()) {
                    indexedCostBase = indexedCostBase.add(parcel.getCgtSecurity().getIndexedCostBase());
                }
            }

        return indexedCostBase;
    }

    protected BigDecimal getCalculatedReducedCostBase() {
        BigDecimal reducedCostBase = BigDecimal.ZERO;

            for (CgtSecurityData parcel : parcels) {
                if (null!=parcel.getCgtSecurity().getReducedCostBase()) {
                    reducedCostBase = reducedCostBase.add(parcel.getCgtSecurity().getReducedCostBase());
                }
            }

        return reducedCostBase;
    }

    public String getQuantity() {
        Integer totalQuantity= getCalculatedQuantityAmount();
        return ReportFormatter.format(ReportFormat.UNITS, totalQuantity);
    }

    public String getCostBase(){
        BigDecimal calculatedCostBase = getCalculatedCostBase();
        return ReportFormatter.format(ReportFormat.CURRENCY, calculatedCostBase);
    }

    public String getIndexedCostBase(){
        BigDecimal calculatedIndexedCostBase = getCalculatedIndexedCostBase() ;
        return ReportFormatter.format(ReportFormat.CURRENCY, calculatedIndexedCostBase);
    }


    public String getReducedCostBase(){
        BigDecimal calculatedReducedCostBase = getCalculatedReducedCostBase() ;
        return ReportFormatter.format(ReportFormat.CURRENCY, calculatedReducedCostBase);
    }

}
