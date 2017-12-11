package com.bt.nextgen.api.trading.v1.model;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.ShareHolding;

import java.math.BigDecimal;

public class ShareTradeAssetDto extends TradeAssetDto {
    private final String dividendMethod;

    public ShareTradeAssetDto(AssetDto shareAsset, boolean buyable, boolean sellable, AccountHolding shareHolding) {
        this(shareAsset, buyable, sellable, shareHolding.getMarketValue(), shareHolding.getAvailableBalance(), shareHolding
                .getAvailableUnits(), ((ShareHolding) shareHolding).getDistributionMethod() == null ? null
                : ((ShareHolding) shareHolding).getDistributionMethod().getDisplayName());
    }

    public ShareTradeAssetDto(AssetDto shareAsset, boolean buyable, boolean sellable, BigDecimal balance,
            BigDecimal availableBalance, BigDecimal availableQuantity, String dividendMethod) {
        super(shareAsset, buyable, sellable, balance, availableBalance, availableQuantity, AssetType.SHARE.getDisplayName());
        this.dividendMethod = dividendMethod;
    }

    public String getDividendMethod() {
        return dividendMethod;
    }

}
