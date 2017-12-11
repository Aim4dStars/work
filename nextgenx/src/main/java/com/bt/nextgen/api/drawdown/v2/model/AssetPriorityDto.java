package com.bt.nextgen.api.drawdown.v2.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.integration.asset.Asset;

import java.math.BigDecimal;

public class AssetPriorityDto extends BaseDto {

    private String assetId;
    private String assetName;
    private String assetCode;
    private String status;
    private String assetType;
    private Integer drawdownPriority;
    private BigDecimal marketValue;

    public AssetPriorityDto() {
        super();
    }

    public AssetPriorityDto(Asset asset, Integer priority, BigDecimal marketValue) {
        this.assetId = asset.getAssetId();
        this.assetName = asset.getAssetName();
        this.assetCode = asset.getAssetCode();
        this.assetType = asset.getAssetType() == null ? null : asset.getAssetType().getDisplayName();
        this.status = asset.getStatus() == null ? null : asset.getStatus().getDisplayName();
        this.drawdownPriority = priority;
        this.marketValue = marketValue;
    }

    public String getAssetId() {
        return assetId;
    }

    public String getAssetName() {
        return assetName;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public String getAssetType() {
        return assetType;
    }

    public Integer getDrawdownPriority() {
        return drawdownPriority;
    }

    public BigDecimal getMarketValue() {
        return marketValue;
    }

    public String getStatus() {
        return status;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public void setDrawdownPriority(Integer drawdownPriority) {
        this.drawdownPriority = drawdownPriority;
    }

    public void setMarketValue(BigDecimal marketValue) {
        this.marketValue = marketValue;
    }

}
