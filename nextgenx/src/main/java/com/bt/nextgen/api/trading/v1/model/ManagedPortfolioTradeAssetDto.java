package com.bt.nextgen.api.trading.v1.model;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedPortfolioAccountValuation;

public class ManagedPortfolioTradeAssetDto extends TradeAssetDto {
    private final String subAccountId;

    public ManagedPortfolioTradeAssetDto(AssetDto mpAsset, boolean buyable, boolean sellable,
            ManagedPortfolioAccountValuation mpAccount) {
        super(mpAsset, buyable, sellable, mpAccount.getBalance(), mpAccount.getAvailableBalance(), null,
                mpAsset.getAssetType());
        this.subAccountId = mpAccount.getSubAccountKey() == null ? null
                : EncodedString.fromPlainText(mpAccount.getSubAccountKey().getId()).toString();
    }

    public String getSubAccountId() {
        return subAccountId;
    }
}
