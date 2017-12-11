package com.bt.nextgen.api.asset.model;

import com.bt.nextgen.service.integration.asset.Asset;

public class ManagedPortfolioAssetDto extends AssetDto {
    private String investmentStyle;
    private Boolean taxAssetDomicile;

    public ManagedPortfolioAssetDto() {
    }

    public ManagedPortfolioAssetDto(Asset asset) {
        this(asset, null);
    }

    public ManagedPortfolioAssetDto(Asset asset, Boolean taxAssetDomicile) {
        super(asset, asset.getAssetName(), asset.getAssetType().getDisplayName());
        this.taxAssetDomicile = taxAssetDomicile;
        this.investmentStyle = asset.getIpsInvestmentStyle();
    }

    public Boolean getTaxAssetDomicile() {
        return taxAssetDomicile;
    }

    public String getInvestmentStyle() {
        return investmentStyle;
    }

    public void setInvestmentStyle(String investmentStyle) {
        this.investmentStyle = investmentStyle;
    }
}
