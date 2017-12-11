package com.bt.nextgen.api.portfolio.v3.model.valuation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import ch.lambdaj.Lambda;

import com.bt.nextgen.api.portfolio.v3.model.InvestmentAssetDto;
import com.bt.nextgen.api.portfolio.v3.model.InvestmentAssetDtoInterface;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.HinType;
import com.bt.nextgen.service.integration.portfolio.valuation.ShareHolding;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.integration.asset.AssetType;

public class ShareValuationDto extends AbstractInvestmentValuationDto implements InvestmentAssetDtoInterface {

    private final InvestmentAssetDto investmentAsset;
    private final Boolean pendingSellDown;
    private final String dividendMethod;
    private final List<String> availableReinvestMethods;
    private final HinType hinType;
    private final String name;
    private final List<ShareValuationDto> childValuations = new ArrayList<>();

    public ShareValuationDto(AccountHolding shareHolding, BigDecimal accountBalance, InvestmentAssetDto investmentAsset,
            List<String> availableReinvestMethods, boolean externalAsset) {
        super(EncodedString.fromPlainText(shareHolding.getHoldingKey().getHid().getId()).toString(),
                shareHolding.getMarketValue(), shareHolding.getAvailableBalance(), accountBalance,
                shareHolding.getAccruedIncome(), shareHolding.getSource(), externalAsset, shareHolding.getIncomeOnly());
        this.investmentAsset = investmentAsset;
        this.hinType = ((ShareHolding) shareHolding).getHinType();
        this.dividendMethod = ((ShareHolding) shareHolding).getDistributionMethod() == null ? null
                : ((ShareHolding) shareHolding).getDistributionMethod().getDisplayName();
        this.pendingSellDown = shareHolding.getHasPendingSellDown();
        this.name = shareHolding.getHoldingKey().getName();
        this.availableReinvestMethods = availableReinvestMethods;
    }
    
    public ShareValuationDto(@NotNull List<AccountHolding> shareHoldings, BigDecimal accountBalance,
            List<InvestmentAssetDto> investmentAssets, List<String> availableReinvestMethods) {
        super(shareHoldings, accountBalance);
        for (int i = 0; i < shareHoldings.size() && shareHoldings.size() > 1; i++) {
            childValuations.add(new ShareValuationDto(shareHoldings.get(i), accountBalance, investmentAssets.get(i),
                    availableReinvestMethods, shareHoldings.get(0).getExternal()));
        }
        this.hinType = shareHoldings.size() > 1 ? HinType.CUSTODIAL : ((ShareHolding) shareHoldings.get(0)).getHinType();
        this.investmentAsset = new InvestmentAssetDto(investmentAssets, accountBalance);
        this.dividendMethod = ((ShareHolding) shareHoldings.get(0)).getDistributionMethod() == null ? null
                : ((ShareHolding) shareHoldings.get(0)).getDistributionMethod().getDisplayName();
        this.pendingSellDown = ((ShareHolding) shareHoldings.get(0)).getHasPendingSellDown();
        this.name = ((ShareHolding) shareHoldings.get(0)).getHoldingKey().getName();
        this.availableReinvestMethods = availableReinvestMethods;
    }

    public InvestmentAssetDto getInvestmentAsset() {
        return investmentAsset;
    }

    public List<ShareValuationDto> getChildValuations() {
        return Lambda.sort(this.childValuations, Lambda.on(ShareValuationDto.class).getHinType());
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

    public HinType getHinType() {
        return hinType;
    }

    public String getAssetCode() {
        return investmentAsset.getAssetCode();
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
