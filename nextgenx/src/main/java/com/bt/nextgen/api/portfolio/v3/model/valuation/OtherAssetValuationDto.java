package com.bt.nextgen.api.portfolio.v3.model.valuation;

import com.bt.nextgen.api.portfolio.v3.model.InvestmentAssetDto;
import com.bt.nextgen.api.portfolio.v3.model.InvestmentAssetDtoInterface;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;

import java.math.BigDecimal;

public class OtherAssetValuationDto extends AbstractInvestmentValuationDto implements InvestmentAssetDtoInterface {

    private final InvestmentAssetDto investmentAsset;
    private final String name;
    private final String categoryName;

    public OtherAssetValuationDto(AccountHolding holding, InvestmentAssetDto investmentAsset, BigDecimal balance,
            BigDecimal accountBalance) {
        super(holding.getHoldingKey().getHid().getId(), balance, balance, accountBalance, BigDecimal.ZERO, holding.getSource(),
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
