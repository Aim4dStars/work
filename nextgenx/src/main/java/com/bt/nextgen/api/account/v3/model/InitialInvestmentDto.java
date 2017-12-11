package com.bt.nextgen.api.account.v3.model;

import com.bt.nextgen.api.asset.model.ManagedPortfolioAssetDto;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.integration.asset.Asset;

import java.math.BigDecimal;

public class InitialInvestmentDto extends BaseDto {

    private ManagedPortfolioAssetDto asset;
    private BigDecimal amount;

    /**
     * Constructs to set initial investment details
     *
     * @param asset    - Managed portfolio asset
     * @param quantity - Amount invested
     */
    public InitialInvestmentDto(Asset asset, BigDecimal quantity) {
        this.asset = new ManagedPortfolioAssetDto(asset);
        this.amount = quantity;
    }

    public ManagedPortfolioAssetDto getAsset() {
        return asset;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
