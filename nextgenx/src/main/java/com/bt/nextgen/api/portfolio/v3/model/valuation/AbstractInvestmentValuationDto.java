package com.bt.nextgen.api.portfolio.v3.model.valuation;

import ch.lambdaj.Lambda;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.avaloq.PortfolioUtils;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;

import java.math.BigDecimal;
import java.util.List;

public abstract class AbstractInvestmentValuationDto extends BaseDto implements InvestmentValuationDto {
    private final BigDecimal balance;
    private final BigDecimal availableBalance;
    private BigDecimal accountBalance;
    private final BigDecimal income;
    private final String subAccountId;
    private final String source;
    private final Boolean externalAsset;
    private final Boolean incomeOnly;

    // required to maintain immutablility
    @SuppressWarnings("squid:S00107")
    public AbstractInvestmentValuationDto(String subAccountId, BigDecimal balance, BigDecimal availableBalance,
            BigDecimal accountBalance, BigDecimal income, String source, Boolean externalAsset, Boolean incomeOnly) {
        super();
        this.balance = balance;
        this.availableBalance = availableBalance;
        this.accountBalance = accountBalance;
        this.income = income;
        this.subAccountId = subAccountId;
        this.externalAsset = externalAsset;
        this.incomeOnly = incomeOnly;
        this.source = source;
    }

    public AbstractInvestmentValuationDto(List<AccountHolding> accountHoldings, BigDecimal accountBalance) {
        super();
        this.balance = Lambda.sumFrom(accountHoldings).getMarketValue();
        this.availableBalance = Lambda.sumFrom(accountHoldings).getAvailableBalance();
        this.income = Lambda.sumFrom(accountHoldings).getAccruedIncome();
        this.subAccountId = null;
        this.source = accountHoldings.get(0).getSource();
        this.incomeOnly = accountHoldings.get(0).getIncomeOnly();
        this.accountBalance = accountBalance;
        this.externalAsset = accountHoldings.get(0).getExternal();
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
        return PortfolioUtils.getValuationAsPercent(getBalance(), accountBalance);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void overwriteAccountBalance(BigDecimal accountBalance) {
        this.accountBalance = accountBalance;
    }
}