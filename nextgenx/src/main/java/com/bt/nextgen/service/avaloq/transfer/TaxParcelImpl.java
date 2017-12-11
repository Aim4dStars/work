package com.bt.nextgen.service.avaloq.transfer;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import com.bt.nextgen.service.integration.transfer.TaxParcel;
import org.joda.time.DateTime;

import java.math.BigDecimal;

@ServiceBean(xpath = "tax_parcel")
public class TaxParcelImpl implements TaxParcel {

    /**
     * @deprecated As of version 3.
     */
    @Deprecated
    @ServiceElement(xpath = "asset/val", converter = DateTimeTypeConverter.class)
    private String assetId;

    @ServiceElement(xpath = "tax_relv_date/val", converter = DateTimeTypeConverter.class)
    private DateTime relevanceDate;

    @ServiceElement(xpath = "tax_visib_date/val", converter = DateTimeTypeConverter.class)
    private DateTime visibilityDate;

    @ServiceElement(xpath = "tax_parcel_qty/val", converter = BigDecimalConverter.class)
    private BigDecimal quantity;

    @ServiceElement(xpath = "orig_cost_base/val", converter = BigDecimalConverter.class)
    private BigDecimal originalCostBase;

    @ServiceElement(xpath = "import_cost_base/val", converter = BigDecimalConverter.class)
    private BigDecimal costBase;

    @ServiceElement(xpath = "import_redu_cost_base/val", converter = BigDecimalConverter.class)
    private BigDecimal reducedCostBase;

    @ServiceElement(xpath = "import_idx_cost_base/val", converter = BigDecimalConverter.class)
    private BigDecimal indexedCostBase;

    public TaxParcelImpl() {
    }

    public TaxParcelImpl(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public TaxParcelImpl(String assetId, DateTime relevanceDate, DateTime visibilityDate, BigDecimal quantity,
            BigDecimal costBase, BigDecimal reducedCostBase, BigDecimal indexedCostBase) {
        this.assetId = assetId;
        this.relevanceDate = relevanceDate;
        this.visibilityDate = visibilityDate;
        this.quantity = quantity;
        this.costBase = costBase;
        this.reducedCostBase = reducedCostBase;
        this.indexedCostBase = indexedCostBase;
    }

    public TaxParcelImpl(String assetId) {
        this.assetId = assetId;
    }

    public void setRelevanceDate(DateTime relevanceDate) {
        this.relevanceDate = relevanceDate;
    }

    @Override
    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public void setVisibilityDate(DateTime visibilityDate) {
        this.visibilityDate = visibilityDate;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public void setCostBase(BigDecimal costBase) {
        this.costBase = costBase;
    }

    public void setReducedCostBase(BigDecimal reducedCostBase) {
        this.reducedCostBase = reducedCostBase;
    }

    public void setIndexedCostBase(BigDecimal indexedCostBase) {
        this.indexedCostBase = indexedCostBase;
    }

    @Override
    public DateTime getRelevanceDate() {
        return relevanceDate;
    }

    @Override
    public DateTime getVisibilityDate() {
        return visibilityDate;
    }

    @Override
    public BigDecimal getQuantity() {
        return quantity;
    }

    @Override
    public BigDecimal getCostBase() {
        return costBase;
    }

    @Override
    public BigDecimal getReducedCostBase() {
        return reducedCostBase;
    }

    @Override
    public BigDecimal getIndexedCostBase() {
        return indexedCostBase;
    }

    @Override
    public BigDecimal getOriginalCostBase() {
        return originalCostBase;
    }

    public void setOriginalCostBase(BigDecimal originalCostBase) {
        this.originalCostBase = originalCostBase;
    }

}
