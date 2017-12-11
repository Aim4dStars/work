package com.bt.nextgen.api.account.v2.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.math.BigDecimal;

@Deprecated
public class AvailableCashDto extends BaseDto implements KeyedDto<AccountKey> {

    private AccountKey key;
    private BigDecimal availableCash;
    private BigDecimal totalPendingSells;
    private BigDecimal pendingSells;
    private BigDecimal queuedBuys;
    private BigDecimal pendingBuys;
    private BigDecimal pendingSellsListedSecurities;
    private BigDecimal queuedBuysListedSecurities;

    public AvailableCashDto(AccountKey key, BigDecimal availableCash, BigDecimal pendingSells, BigDecimal queuedBuys,
            BigDecimal pendingBuys, BigDecimal pendingSellsListedSecurities, BigDecimal queuedBuysListedSecurities) {

        super();
        this.key = key;
        this.availableCash = availableCash;
        this.pendingSells = pendingSells;
        this.queuedBuys = queuedBuys;
        this.pendingBuys = pendingBuys;
        this.pendingSellsListedSecurities = pendingSellsListedSecurities;
        this.queuedBuysListedSecurities = queuedBuysListedSecurities;
    }

    public BigDecimal getAvailableCash() {
        return availableCash;
    }

    public BigDecimal getTotalPendingSells() {
        return totalPendingSells;
    }

    public void setTotalPendingSells(BigDecimal totalPendingSells) {
        this.totalPendingSells = totalPendingSells;
    }

    public BigDecimal getPendingSells() {
        return pendingSells;
    }

    public BigDecimal getQueuedBuys() {
        return queuedBuys;
    }

    public BigDecimal getPendingBuys() {
        return pendingBuys;
    }

    public BigDecimal getPendingSellsListedSecurities() {
        return pendingSellsListedSecurities;
    }

    public BigDecimal getQueuedBuysListedSecurities() {
        return queuedBuysListedSecurities;
    }

    @Override
    public AccountKey getKey() {
        return key;
    }

}
