package com.bt.nextgen.api.account.v2.model;

import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;

import java.math.BigDecimal;

@Deprecated
public class QuantisedAssetValuationDto extends OtherAssetValuationDto {
    private BigDecimal units;

    public QuantisedAssetValuationDto(AccountHolding holding, InvestmentAssetDto investmentAsset, BigDecimal balance,
            BigDecimal units, BigDecimal portfolioPercent) {
        super(holding, investmentAsset, balance, portfolioPercent);
        this.units = units;
    }

    public BigDecimal getUnits() {
        return units;
    }

}
