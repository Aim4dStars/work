package com.bt.nextgen.api.modelportfolio.v2.model.detail;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.fasterxml.jackson.annotation.JsonView;

import java.math.BigDecimal;

public class TargetAllocationDto extends BaseDto {
    @JsonView(JsonViews.Write.class)
    private String assetClass;

    @JsonView(JsonViews.Write.class)
    private BigDecimal minimumWeight;

    @JsonView(JsonViews.Write.class)
    private BigDecimal maximumWeight;

    @JsonView(JsonViews.Write.class)
    private BigDecimal neutralPos;

    @JsonView(JsonViews.Write.class)
    private AssetDto indexAsset;

    private String assetClassName;

    public TargetAllocationDto() {
        super();
    }

    public TargetAllocationDto(String assetClass, BigDecimal minimumWeight, BigDecimal maximumWeight, BigDecimal neutralPos,
            AssetDto indexAsset) {
        super();
        this.assetClass = assetClass;
        this.assetClassName = assetClass == null ? null : AssetClass.forIntlId(assetClass).getDescription();
        this.minimumWeight = minimumWeight;
        this.maximumWeight = maximumWeight;
        this.neutralPos = neutralPos;
        this.indexAsset = indexAsset;
    }

    public String getAssetClass() {
        return assetClass;
    }

    public BigDecimal getMinimumWeight() {
        return minimumWeight;
    }

    public BigDecimal getMaximumWeight() {
        return maximumWeight;
    }

    public BigDecimal getNeutralPos() {
        return neutralPos;
    }

    public AssetDto getIndexAsset() {
        return indexAsset;
    }

    public String getAssetClassName() {
        return assetClassName;
    }

}
