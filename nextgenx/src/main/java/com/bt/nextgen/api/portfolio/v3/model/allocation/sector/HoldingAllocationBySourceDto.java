package com.bt.nextgen.api.portfolio.v3.model.allocation.sector;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.bt.nextgen.api.portfolio.v3.service.allocation.HoldingSource;

public class HoldingAllocationBySourceDto {
    private BigDecimal balance;
    private BigDecimal units;
    private BigDecimal portfolioBalance;
    private final boolean pending;
    private final Boolean external;
    private final String source;

    public HoldingAllocationBySourceDto(HoldingSource holdingSource, BigDecimal portfolioBalance, boolean isIncome) {
        this.balance = isIncome ? holdingSource.getIncome() : holdingSource.getMarketValue();
        this.units = isIncome ? null : holdingSource.getUnits();
        this.pending = isIncome ? false : holdingSource.isPending();
        this.external = holdingSource.isExternal();
        this.source = holdingSource.getExternalSource();
        this.portfolioBalance = portfolioBalance;
    }

    public BigDecimal getBalance() {
        return this.balance;
    }

    public BigDecimal getUnits() {
        BigDecimal holdingUnits = null;
        if (!pending) {
            holdingUnits = units;
        }
        return holdingUnits;
    }

    public BigDecimal getAllocationPercentage() {
        BigDecimal percent = BigDecimal.ZERO;
        if (!(BigDecimal.ZERO.compareTo(this.balance) == 0)) {
            percent = this.balance.divide(this.portfolioBalance, 8, RoundingMode.HALF_UP);
        }
        return percent;
    }

    public Boolean isPending() {
        return this.pending;
    }

    public Boolean isExternal() {
        return this.external;
    }

    public String getSource() {
        return this.source;
    }
}
