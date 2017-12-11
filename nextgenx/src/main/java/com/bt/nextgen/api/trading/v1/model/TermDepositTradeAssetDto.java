package com.bt.nextgen.api.trading.v1.model;

import com.bt.nextgen.api.asset.model.InterestRateDto;
import com.bt.nextgen.api.asset.model.TermDepositAssetDto;
import com.btfin.panorama.service.integration.asset.AssetType;

import java.math.BigDecimal;

public class TermDepositTradeAssetDto extends TradeAssetDto {
    private static final double COMPARISON_AMOUNT = 10000d;

    private final String termDisplay;
    private final String description;

    public TermDepositTradeAssetDto(TermDepositAssetDto tdAsset, boolean buyable, boolean sellable) {
        super(tdAsset, buyable, sellable, null, null, null, AssetType.TERM_DEPOSIT.getDisplayName());
        this.termDisplay = tdAsset.getTerm() + " months";
        this.description = tdAsset.getInterestPaymentFrequency();
    }

    public String getTermDisplay() {
        return termDisplay;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getIndicativeRate() {
        if (this.getAsset() instanceof TermDepositAssetDto) {
            TermDepositAssetDto tdAsset = (TermDepositAssetDto) this.getAsset();
            for (InterestRateDto rate : tdAsset.getInterestBands()) {
                if (rate.getLowerLimit().doubleValue() <= COMPARISON_AMOUNT
                        && rate.getUpperLimit().doubleValue() >= COMPARISON_AMOUNT) {
                    return rate.getRate();
                }
            }
        }
        return null;
    }
}
