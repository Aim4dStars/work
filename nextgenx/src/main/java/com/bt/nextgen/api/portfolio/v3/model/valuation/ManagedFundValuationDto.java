package com.bt.nextgen.api.portfolio.v3.model.valuation;

import com.bt.nextgen.api.portfolio.v3.model.InvestmentAssetDto;
import com.bt.nextgen.api.portfolio.v3.model.InvestmentAssetDtoInterface;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;

import java.math.BigDecimal;
import java.util.List;

public class ManagedFundValuationDto extends AbstractInvestmentValuationDto implements InvestmentAssetDtoInterface {
    private final InvestmentAssetDto investmentAsset;
    private final Boolean pendingSellDown;
    private final String distributionMethod;
    private final List<String> availableReinvestMethods;
    private final String name;

    public ManagedFundValuationDto(AccountHolding mfHolding, BigDecimal accountBalance, InvestmentAssetDto investmentAsset,
            String distributionMethod, List<String> availableReinvestMethods, boolean externalAsset) {
        super(EncodedString.fromPlainText(mfHolding.getHoldingKey().getHid().getId()).toString(), mfHolding.getMarketValue(),
                mfHolding.getAvailableBalance(), accountBalance, mfHolding.getAccruedIncome(), mfHolding.getSource(),
                externalAsset, mfHolding.getIncomeOnly());
        this.investmentAsset = investmentAsset;
        this.pendingSellDown = mfHolding.getHasPendingSellDown();
        this.distributionMethod = distributionMethod;
        this.name = mfHolding.getHoldingKey().getName();
        this.availableReinvestMethods = availableReinvestMethods;
    }

    public InvestmentAssetDto getInvestmentAsset() {
        return investmentAsset;
    }

    public Boolean getPendingSellDown() {
        return pendingSellDown;
    }

    public String getDistributionMethod() {
        return distributionMethod;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<String> getAvailableReinvestMethods() {
        return availableReinvestMethods;
    }

    @Override
    public String getCategoryName() {
        return AssetType.MANAGED_FUND.getGroupDescription();
    }
}
