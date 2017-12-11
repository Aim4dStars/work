package com.bt.nextgen.api.account.v3.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.math.BigDecimal;

public class AccountBalanceDto extends BaseDto implements KeyedDto<AccountKey> {

    private AccountKey key;
    private BigDecimal availableCash;
    private BigDecimal portfolioValue;

    public AccountBalanceDto() {
        this.key = null;
        this.availableCash = null;
        this.portfolioValue = null;
    }

    public AccountBalanceDto(AccountKey key, BigDecimal availableCash, BigDecimal portfolioValue) {
        this.key = key;
        this.availableCash = availableCash;
        this.portfolioValue = portfolioValue;
    }

    public BigDecimal getAvailableCash() {
        return availableCash;
    }

    public void setAvailableCash(BigDecimal availableCash) {
        this.availableCash = availableCash;
    }

    public BigDecimal getPortfolioValue() {
        return portfolioValue;
    }

    public void setPortfolioValue(BigDecimal portfolioValue) {
        this.portfolioValue = portfolioValue;
    }

    public void setKey(AccountKey key) {
        this.key = key;
    }

    @Override
    public AccountKey getKey() {
        return key;
    }
}