package com.bt.nextgen.api.portfolio.v3.model.valuation;

import com.bt.nextgen.service.avaloq.PortfolioUtils;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.portfolio.valuation.CashHolding;

import java.math.BigDecimal;

public class CashManagementValuationDto extends AbstractInvestmentValuationDto {

    private final BigDecimal interestRate;
    private final String name;
    private final BigDecimal valueDateBalance;
    private final BigDecimal accountBalance;

    public CashManagementValuationDto(String subAccountId, CashHolding cashHolding, BigDecimal accountBalance,
            BigDecimal availableBalance, boolean externalAsset) {
        super(subAccountId, cashHolding.getMarketValue(), availableBalance, accountBalance, cashHolding.getAccruedIncome(),
                cashHolding.getSource(), externalAsset, false);
        this.interestRate = cashHolding.getInterestRate();
        this.name = cashHolding.getAccountName();
        this.valueDateBalance = cashHolding.getValueDateBalance();
        this.accountBalance = accountBalance;
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

    public BigDecimal getValueDateBalance() {
        return valueDateBalance;
    }

    public BigDecimal getValueDatePercent() {
        return PortfolioUtils.getValuationAsPercent(valueDateBalance, accountBalance);
    }

    public BigDecimal getOutstandingCash() {
        if (this.getExternalAsset()) {
            return BigDecimal.ZERO;
        }
        BigDecimal balance = getBalance();
        if (valueDateBalance == null) {
            return balance;
        } else if (balance == null) {
            return valueDateBalance.negate();
        } else {
            return getBalance().subtract(valueDateBalance);
        }
    }

    public BigDecimal getOutstandingCashPercent() {
        return PortfolioUtils.getValuationAsPercent(valueDateBalance, accountBalance);
    }
}
