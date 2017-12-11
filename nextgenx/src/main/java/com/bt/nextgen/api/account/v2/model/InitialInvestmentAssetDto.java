package com.bt.nextgen.api.account.v2.model;

import com.bt.nextgen.api.asset.model.ManagedPortfolioAssetDto;
import com.bt.nextgen.service.integration.asset.Asset;

import java.math.BigDecimal;

@Deprecated
public class InitialInvestmentAssetDto extends ManagedPortfolioAssetDto {
    private BigDecimal amount;

    public InitialInvestmentAssetDto() {
        super();
    }

    public InitialInvestmentAssetDto(Asset asset, BigDecimal quantity) {
        super(asset);
        this.amount = quantity;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
