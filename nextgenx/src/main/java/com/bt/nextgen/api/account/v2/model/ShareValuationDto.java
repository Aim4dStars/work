package com.bt.nextgen.api.account.v2.model;

import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.ShareHolding;

import java.math.BigDecimal;
import java.util.List;

@Deprecated
public class ShareValuationDto extends AbstractInvestmentValuationDto {

    private final InvestmentAssetDto investmentAsset;
    private final Boolean pendingSellDown;
    private final String dividendMethod;
    private final List<String> availableReinvestMethods;
    private final String name;

    public ShareValuationDto(AccountHolding shareHolding, BigDecimal portfolioPercent, InvestmentAssetDto investmentAsset,
            List<String> availableReinvestMethods, boolean externalAsset) {
        super(EncodedString.fromPlainText(shareHolding.getHoldingKey().getHid().getId()).toString(), shareHolding
                .getMarketValue(), shareHolding.getAvailableBalance(), portfolioPercent, shareHolding.getAccruedIncome(),
                shareHolding.getSource(), externalAsset, shareHolding.getIncomeOnly());
        this.investmentAsset = investmentAsset;
        this.dividendMethod = ((ShareHolding) shareHolding).getDistributionMethod() == null ? null
                : ((ShareHolding) shareHolding).getDistributionMethod().getDisplayName();
        this.pendingSellDown = shareHolding.getHasPendingSellDown();
        this.name = shareHolding.getHoldingKey().getName();
        this.availableReinvestMethods = availableReinvestMethods;
    }

    public InvestmentAssetDto getInvestmentAsset() {
        return investmentAsset;
    }

    public Boolean getPendingSellDown() {
        return pendingSellDown;
    }

    public String getDividendMethod() {
        return dividendMethod;
    }

    public List<String> getAvailableReinvestMethods() {
        return availableReinvestMethods;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getCategoryName() {
        return AssetType.SHARE.getGroupDescription();
    }
}
