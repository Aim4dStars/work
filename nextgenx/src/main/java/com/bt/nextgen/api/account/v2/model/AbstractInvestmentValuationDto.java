package com.bt.nextgen.api.account.v2.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.math.BigDecimal;

@Deprecated
public abstract class AbstractInvestmentValuationDto extends BaseDto implements InvestmentValuationDto {
    private final BigDecimal balance;
    private final BigDecimal availableBalance;
    private final BigDecimal portfolioPercent;
    private final BigDecimal income;
    private final String subAccountId;
    private final String source;
    private final Boolean externalAsset;
    private final Boolean incomeOnly;

    // required to maintain immutablility
    @SuppressWarnings("squid:S00107")
    public AbstractInvestmentValuationDto(String subAccountId, BigDecimal balance, BigDecimal availableBalance,
            BigDecimal portfolioPercent, BigDecimal income, String source, Boolean externalAsset, Boolean incomeOnly) {
        super();
        this.balance = balance;
        this.availableBalance = availableBalance;
        this.portfolioPercent = portfolioPercent;
        this.income = income;
        this.subAccountId = subAccountId;
        this.externalAsset = externalAsset;
        this.incomeOnly = incomeOnly;
        this.source = source;
    }


    @Override
    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    @Override
    public BigDecimal getIncome() {
        return income;
    }

    @Override
    public BigDecimal getPortfolioPercent() {
        return portfolioPercent;
    }

    @Override
    public String getSubAccountId() {
        return subAccountId;
    }

    @Override
    public Boolean getExternalAsset() {
        return externalAsset;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public Boolean getIncomeOnly() {
        return incomeOnly;
    }
}