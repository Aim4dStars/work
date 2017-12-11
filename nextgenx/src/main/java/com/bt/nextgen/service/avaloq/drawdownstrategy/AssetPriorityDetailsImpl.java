package com.bt.nextgen.service.avaloq.drawdownstrategy;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.drawdownstrategy.AssetPriorityDetails;

import java.math.BigDecimal;

@ServiceBean(xpath = "draw_dwn_pref_item | dd_pref")
public class AssetPriorityDetailsImpl implements AssetPriorityDetails {

    @ServiceElement(xpath = "asset_id/val")
    private String assetId;

    @ServiceElement(xpath = "prio/val")
    private Integer drawdownPriority;

    @ServiceElement(xpath = "perc/val")
    private BigDecimal drawdownPercentage;

    public AssetPriorityDetailsImpl() {
        super();
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public Integer getDrawdownPriority() {
        return drawdownPriority;
    }

    public void setDrawdownPriority(Integer drawdownPriority) {
        this.drawdownPriority = drawdownPriority;
    }

    public BigDecimal getDrawdownPercentage() {
        return drawdownPercentage;
    }

    public void setDrawdownPercentage(BigDecimal drawdownPercentage) {
        this.drawdownPercentage = drawdownPercentage;
    }
}
