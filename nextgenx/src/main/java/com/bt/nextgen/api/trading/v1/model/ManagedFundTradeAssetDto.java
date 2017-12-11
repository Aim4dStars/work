package com.bt.nextgen.api.trading.v1.model;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedFundHolding;

import java.math.BigDecimal;

public class ManagedFundTradeAssetDto extends TradeAssetDto {
    private final String distributionMethod;

    public ManagedFundTradeAssetDto(AssetDto mfAsset, boolean buyable, boolean sellable, AccountHolding mfHolding) {
        this(mfAsset, buyable, sellable, mfHolding.getMarketValue(), mfHolding.getAvailableBalance(), mfHolding
                .getAvailableUnits(), ((ManagedFundHolding) mfHolding).getDistributionMethod() == null ? null
                : ((ManagedFundHolding) mfHolding).getDistributionMethod().getDisplayName());
    }

    public ManagedFundTradeAssetDto(AssetDto mfAsset, boolean buyable, boolean sellable, BigDecimal balance,
            BigDecimal availableBalance, BigDecimal availableQuantity, String distributionMethod) {
        super(mfAsset, buyable, sellable, balance, availableBalance, availableQuantity, AssetType.MANAGED_FUND.getDisplayName());
        this.distributionMethod = distributionMethod;
    }

    public String getDistributionMethod() {
        return distributionMethod;
    }
}
