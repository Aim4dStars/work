package com.bt.nextgen.api.account.v3.model;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.integration.asset.Asset;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * Instance for the cash sweep investment details
 */
@ApiModel(value = "CashSweepInvestment")
public class CashSweepInvestmentDto extends BaseDto {

    @ApiModelProperty(value = "Investment asset")
    @JsonView(JsonViews.Write.class)
    private AssetDto asset;

    @ApiModelProperty(value = "percentage allocation for the asset")
    @JsonView(JsonViews.Write.class)
    private BigDecimal allocationPercent;

    public CashSweepInvestmentDto() {
        // Required for object mapper
    }

    /**
     * @param asset             - Investment Asset for the cash sweep
     * @param allocationPercent - Percentage of cash to be invested in the asset
     */
    public CashSweepInvestmentDto(Asset asset, BigDecimal allocationPercent) {
        this.asset = new AssetDto(asset, asset.getAssetName(), asset.getAssetType().getDisplayName());
        this.allocationPercent = allocationPercent;
    }

    public AssetDto getAsset() {
        return asset;
    }

    public BigDecimal getAllocationPercent() {
        return allocationPercent;
    }
}
