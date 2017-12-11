package com.bt.nextgen.api.inspecietransfer.v2.model;

import com.bt.nextgen.service.integration.transfer.TaxParcel;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * @deprecated Use V3
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
    private BigDecimal originalCostBase;

    public TaxParcelDto() {
        // Empty constructor for JSON object mapper
    }

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

    public TaxParcelDto(TaxParcel taxParcel) {
        super();
        this.assetCode = taxParcel.getAssetId();
        this.relevanceDate = taxParcel.getRelevanceDate();
        this.visibilityDate = taxParcel.getVisibilityDate();
        this.quantity = taxParcel.getQuantity();
        this.originalCostBase = taxParcel.getOriginalCostBase();
        this.costBase = taxParcel.getCostBase();
        this.reducedCostBase = taxParcel.getReducedCostBase();
        this.indexedCostBase = taxParcel.getIndexedCostBase();
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

    public BigDecimal getOriginalCostBase() {
        return originalCostBase;
    }

    public void setOriginalCostBase(BigDecimal originalCostBase) {
        this.originalCostBase = originalCostBase;
    }

}
