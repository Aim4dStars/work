package com.bt.nextgen.api.account.v2.model;

import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;

import java.math.BigDecimal;

@Deprecated
public class OtherAssetValuationDto extends AbstractInvestmentValuationDto {

    private final InvestmentAssetDto investmentAsset;
    private final String name;
    private final String categoryName;

    public OtherAssetValuationDto(AccountHolding holding, InvestmentAssetDto investmentAsset, BigDecimal balance,
            BigDecimal portfolioPercent) {
        super(holding.getHoldingKey().getHid().getId(), balance, balance, portfolioPercent, BigDecimal.ZERO, holding.getSource(),
                holding.getExternal(), false);
        this.investmentAsset = investmentAsset;
        this.name = holding.getAsset().getAssetName();
        this.categoryName = holding.getAsset().getAssetType().getGroupDescription();        
    }

    public InvestmentAssetDto getInvestmentAsset() {
        return investmentAsset;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getCategoryName() {
        return this.categoryName;
    }
}
