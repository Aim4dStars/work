package com.bt.nextgen.account.api.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.NamedDto;

import java.math.BigDecimal;

public abstract class InvestmentValuationDto extends BaseDto implements NamedDto {
    private final String name;
    private final BigDecimal balance;
    private final BigDecimal availableBalance;
    private final BigDecimal portfolioPercent;
    private final BigDecimal income;
    private final String subAccountId;

    public InvestmentValuationDto(String subAccountId, String name, BigDecimal balance, BigDecimal availableBalance,
            BigDecimal portfolioPercent, BigDecimal income) {
        super();
        this.name = name;
        this.balance = balance;
        this.availableBalance = availableBalance;
        this.portfolioPercent = portfolioPercent;
        this.income = income;
        this.subAccountId = subAccountId;
    }

    @Override
    public String getName() {
        return name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public BigDecimal getPortfolioPercent() {
        return portfolioPercent;
    }

    public String getSubAccountId() {
        return subAccountId;
    }
}