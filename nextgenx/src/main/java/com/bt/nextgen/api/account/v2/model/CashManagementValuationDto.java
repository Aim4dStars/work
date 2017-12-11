package com.bt.nextgen.api.account.v2.model;

import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.portfolio.valuation.CashHolding;

import java.math.BigDecimal;

@Deprecated
public class CashManagementValuationDto extends AbstractInvestmentValuationDto {

    private final BigDecimal interestRate;
    private final String name;

    public CashManagementValuationDto(String subAccountId, CashHolding cashHolding, BigDecimal portfolioPercent,
            BigDecimal availableBalance, boolean externalAsset) {
        super(subAccountId, cashHolding.getMarketValue(), availableBalance, portfolioPercent, cashHolding.getAccruedIncome(),
                cashHolding.getSource(), externalAsset, false);
        this.interestRate = cashHolding.getInterestRate();
        this.name = cashHolding.getAccountName();
    }


    public BigDecimal getInterestRate() {
        return interestRate;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getCategoryName() {
        return AssetType.CASH.getGroupDescription();
    }

}
