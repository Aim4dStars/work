package com.bt.nextgen.api.account.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.math.BigDecimal;

/**
 * @deprecated Use V2
 */
@Deprecated
public class AccountBalanceDto extends BaseDto implements KeyedDto<AccountKey> {

    /** The key. */
    private AccountKey key;

    /** The available cash. */
    private BigDecimal availableCash;

    /** The portfolio value. */
    private BigDecimal portfolioValue;

    /**
     * Instantiates a new account balance dto.
     */
    public AccountBalanceDto() {
    }

    /**
     * Instantiates a new account balance dto.
     *
     * @param key
     *            the key
     * @param availableCash
     *            the available cash
     * @param portfolioValue
     *            the portfolio value
     */
    public AccountBalanceDto(AccountKey key, BigDecimal availableCash, BigDecimal portfolioValue) {
        this.key = key;
        this.availableCash = availableCash;
        this.portfolioValue = portfolioValue;
    }

    /**
     * Gets the available cash.
     *
     * @return the available cash
     */
    public BigDecimal getAvailableCash() {
        return availableCash;
    }

    /**
     * Sets the available cash.
     *
     * @param availableCash
     *            the new available cash
     */
    public void setAvailableCash(BigDecimal availableCash) {
        this.availableCash = availableCash;
    }

    /**
     * Gets the portfolio value.
     *
     * @return the portfolio value
     */
    public BigDecimal getPortfolioValue() {
        return portfolioValue;
    }

    /**
     * Sets the portfolio value.
     *
     * @param portfolioValue
     *            the new portfolio value
     */
    public void setPortfolioValue(BigDecimal portfolioValue) {
        this.portfolioValue = portfolioValue;
    }

    /**
     * Sets the key.
     *
     * @param key
     *            the new key
     */
    public void setKey(AccountKey key) {
        this.key = key;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.bt.nextgen.core.api.model.KeyedDto#getKey()
     */
    @Override
    public AccountKey getKey() {
        return key;
    }
}