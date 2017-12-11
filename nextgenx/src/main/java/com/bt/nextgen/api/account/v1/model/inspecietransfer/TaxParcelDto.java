package com.bt.nextgen.api.account.v1.model.inspecietransfer;

import java.math.BigDecimal;

import org.joda.time.DateTime;

/**
 * @deprecated Use V2
 */
@Deprecated
public class TaxParcelDto {

    private String assetId;
    private String assetCode;
    private DateTime relevanceDate;
    private DateTime visibilityDate;
    private BigDecimal quantity;
    private BigDecimal costBase;
    private BigDecimal reducedCostBase;
    private BigDecimal indexedCostBase;

    public TaxParcelDto(String assetCode, DateTime taxRelvDate, DateTime taxVisibDate, BigDecimal quantity, BigDecimal costBase,
            BigDecimal reducedCostBase, BigDecimal indexedCostBase) {
        super();
        this.assetCode = assetCode;
        this.relevanceDate = taxRelvDate;
        this.visibilityDate = taxVisibDate;
        this.quantity = quantity;
        this.costBase = costBase;
        this.reducedCostBase = reducedCostBase;
        this.indexedCostBase = indexedCostBase;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public DateTime getTaxRelvDate() {
        return relevanceDate;
    }

    public void setTaxRelvDate(DateTime taxRelvDate) {
        this.relevanceDate = taxRelvDate;
    }

    public DateTime getTaxVisibDate() {
        return visibilityDate;
    }

    public void setTaxVisibDate(DateTime taxVisibDate) {
        this.visibilityDate = taxVisibDate;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getCostBase() {
        return costBase;
    }

    public void setCostBase(BigDecimal costBase) {
        this.costBase = costBase;
    }

    public BigDecimal getReducedCostBase() {
        return reducedCostBase;
    }

    public void setReducedCostBase(BigDecimal reducedCostBase) {
        this.reducedCostBase = reducedCostBase;
    }

    public BigDecimal getIndexedCostBase() {
        return indexedCostBase;
    }

    public void setIndexedCostBase(BigDecimal indexedCostBase) {
        this.indexedCostBase = indexedCostBase;
    }

}
