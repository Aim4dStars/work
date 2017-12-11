package com.bt.nextgen.service.avaloq.modelportfolio.detail;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.modelportfolio.detail.TargetAllocation;

import java.math.BigDecimal;

@ServiceBean(xpath = "taa")
public class TargetAllocationImpl implements TargetAllocation {

    private static final String CODE_CATEGORY_ASSET_CLASS = "ASSET_CLASS";

    @ServiceElement(xpath = "asset_class_cat/val | asset_class_cat_id/val", staticCodeCategory = CODE_CATEGORY_ASSET_CLASS)
    private String assetClass;

    @ServiceElement(xpath = "min_wgt/val")
    private BigDecimal minimumWeight;

    @ServiceElement(xpath = "max_wgt/val")
    private BigDecimal maximumWeight;

    @ServiceElement(xpath = "neutral_pos/val")
    private BigDecimal neutralPos;

    @ServiceElement(xpath = "idx_asset/val | idx_asset_id/val")
    private String indexAssetId;

    public String getAssetClass() {
        return assetClass;
    }

    public void setAssetClass(String assetClass) {
        this.assetClass = assetClass;
    }

    public BigDecimal getMinimumWeight() {
        return minimumWeight;
    }

    public void setMinimumWeight(BigDecimal minimumWeight) {
        this.minimumWeight = minimumWeight;
    }

    public BigDecimal getMaximumWeight() {
        return maximumWeight;
    }

    public void setMaximumWeight(BigDecimal maximumWeight) {
        this.maximumWeight = maximumWeight;
    }

    public BigDecimal getNeutralPos() {
        return neutralPos;
    }

    public void setNeutralPos(BigDecimal neutralPos) {
        this.neutralPos = neutralPos;
    }

    public String getIndexAssetId() {
        return indexAssetId;
    }

    public void setIndexAssetId(String indexAssetId) {
        this.indexAssetId = indexAssetId;
    }
}
