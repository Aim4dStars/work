package com.bt.nextgen.api.inspecietransfer.v3.model;

import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.service.integration.transfer.TaxParcel;
import com.fasterxml.jackson.annotation.JsonView;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public class TaxParcelDto {

    @JsonView(JsonViews.Write.class)
    private String assetId;

    @JsonView(JsonViews.Write.class)
    private String assetCode;

    @JsonView(JsonViews.Write.class)
    private DateTime taxRelevanceDate;

    @JsonView(JsonViews.Write.class)
    private DateTime taxVisibilityDate;

    @JsonView(JsonViews.Write.class)
    private BigDecimal quantity;

    @JsonView(JsonViews.Write.class)
    private BigDecimal costBase;

    @JsonView(JsonViews.Write.class)
    private BigDecimal reducedCostBase;

    @JsonView(JsonViews.Write.class)
    private BigDecimal indexedCostBase;

    @JsonView(JsonViews.Write.class)
    private BigDecimal originalCostBase;

    public TaxParcelDto() {
        super();
    }

    public TaxParcelDto(TaxParcel taxParcel, String assetCode) {
        this.assetId = taxParcel.getAssetId();
        this.assetCode = assetCode;
        this.taxRelevanceDate = taxParcel.getRelevanceDate();
        this.taxVisibilityDate = taxParcel.getVisibilityDate();
        this.quantity = taxParcel.getQuantity();
        this.costBase = taxParcel.getCostBase();
        this.reducedCostBase = taxParcel.getReducedCostBase();
        this.indexedCostBase = taxParcel.getIndexedCostBase();
        this.originalCostBase = taxParcel.getOriginalCostBase();
    }

    public String getAssetId() {
        return assetId;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public DateTime getTaxRelevanceDate() {
        return taxRelevanceDate;
    }

    public DateTime getTaxVisibilityDate() {
        return taxVisibilityDate;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getCostBase() {
        return costBase;
    }

    public BigDecimal getReducedCostBase() {
        return reducedCostBase;
    }

    public BigDecimal getIndexedCostBase() {
        return indexedCostBase;
    }

    public BigDecimal getOriginalCostBase() {
        return originalCostBase;
    }
}
