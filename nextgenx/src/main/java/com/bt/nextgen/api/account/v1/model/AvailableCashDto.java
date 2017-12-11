package com.bt.nextgen.api.account.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.math.BigDecimal;

/**
 * @deprecated Use V2
 */
@Deprecated
public class AvailableCashDto extends BaseDto implements KeyedDto<AccountKey> {

    /** The key. */
    private AccountKey key;

    /** The available cash. */
    private BigDecimal availableCash;

    /** The pending sells. */
    private BigDecimal pendingSells;

    /** The queued buys. */
    private BigDecimal queuedBuys;

    /** The pending buys. */
    private BigDecimal pendingBuys;

    /**
     * Instantiates a new available cash dto.
     *
     * @param key
     *            the key
     * @param availableCash
     *            the available cash
     * @param pendingSells
     *            the pending sells
     * @param queuedBuys
     *            the queued buys
     * @param pendingBuys
     *            the pending buys
     */
    public AvailableCashDto(AccountKey key, BigDecimal availableCash, BigDecimal pendingSells, BigDecimal queuedBuys,
            BigDecimal pendingBuys) {

        super();
        this.key = key;
        this.availableCash = availableCash;
        this.pendingSells = pendingSells;
        this.queuedBuys = queuedBuys;
        this.pendingBuys = pendingBuys;
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
     * Gets the pending sells.
     *
     * @return the pending sells
     */
    public BigDecimal getPendingSells() {
        return pendingSells;
    }

    /**
     * Gets the queued buys.
     *
     * @return the queued buys
     */
    public BigDecimal getQueuedBuys() {
        return queuedBuys;
    }

    /**
     * Gets the pending buys.
     *
     * @return the pending buys
     */
    public BigDecimal getPendingBuys() {
        return pendingBuys;
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
