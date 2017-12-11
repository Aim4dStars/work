package com.bt.nextgen.api.account.v2.model;

import java.math.BigDecimal;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

@Deprecated
public class AccountBalanceDto extends BaseDto implements KeyedDto<AccountKey> {

    private AccountKey key;
    private BigDecimal availableCash;
    private BigDecimal portfolioValue;

    public AccountBalanceDto() {
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