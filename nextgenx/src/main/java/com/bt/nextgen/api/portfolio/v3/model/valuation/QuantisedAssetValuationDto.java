package com.bt.nextgen.api.portfolio.v3.model.valuation;

import com.bt.nextgen.api.portfolio.v3.model.InvestmentAssetDto;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;

import java.math.BigDecimal;

public class QuantisedAssetValuationDto extends OtherAssetValuationDto {
    private BigDecimal units;

    public QuantisedAssetValuationDto(AccountHolding holding, InvestmentAssetDto investmentAsset, BigDecimal balance,
            BigDecimal units, BigDecimal accountBalance) {
        super(holding, investmentAsset, balance, accountBalance);
        this.units = units;
    }

    public BigDecimal getUnits() {
        return units;
    }

}
