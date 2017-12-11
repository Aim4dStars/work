package com.bt.nextgen.service.integration.transfer;

import java.math.BigDecimal;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.btfin.panorama.service.integration.asset.AssetType;

@ServiceBean(xpath = "settle_rec")
public class InspecieAsset {

    @ServiceElement(xpath = "asset/val")
    private String assetId;

    @ServiceElement(xpath = "qty/val")
    private BigDecimal quantity;

    @ServiceElement(xpath = "pos/val")
    private String holdingId;

    private String name;
    private AssetType type;

    public InspecieAsset() {
    }

    public InspecieAsset(String assetId, BigDecimal quantity) {
        this.assetId = assetId;
        this.quantity = quantity;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getHoldingId() {
        return holdingId;
    }

    public void setHoldingId(String holdingId) {
        this.holdingId = holdingId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AssetType getType() {
        return type;
    }

    public void setType(AssetType type) {
        this.type = type;
    }

}
