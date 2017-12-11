package com.bt.nextgen.api.holdingbreach.v1.model;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.core.api.model.BaseDto;

import java.math.BigDecimal;

public class HoldingBreachAssetDto extends BaseDto {
    private final AssetDto asset;
    private final BigDecimal marketValue;
    private final BigDecimal portfolioPercent;
    private final BigDecimal holdingLimitPercent;
    private final BigDecimal breachAmount;

    public HoldingBreachAssetDto(AssetDto asset, BigDecimal marketValue, BigDecimal portfolioPercent,
            BigDecimal holdingLimitPercent, BigDecimal breachAmount) {
        super();
        this.asset = asset;
        this.marketValue = marketValue;
        this.portfolioPercent = portfolioPercent;
        this.holdingLimitPercent = holdingLimitPercent;
        this.breachAmount = breachAmount;
    }

    public AssetDto getAsset() {
        return asset;
    }
    public BigDecimal getMarketValue() {
        return marketValue;
    }
    public BigDecimal getPortfolioPercent() {
        return portfolioPercent;
    }
    public BigDecimal getHoldingLimitPercent() {
        return holdingLimitPercent;
    }
    public BigDecimal getBreachAmount() {
        return breachAmount;
    }
}
