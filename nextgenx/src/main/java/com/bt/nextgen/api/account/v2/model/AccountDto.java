package com.bt.nextgen.api.account.v2.model;

import com.bt.nextgen.api.account.model.BaseAccountDto;

import java.math.BigDecimal;

@Deprecated
public class AccountDto extends BaseAccountDto {

    private BigDecimal availableCash;
    private BigDecimal portfolioValue;

    public AccountDto(AccountKey key) {
        super(key);
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

  }
