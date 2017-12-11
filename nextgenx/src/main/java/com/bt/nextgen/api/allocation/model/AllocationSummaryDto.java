package com.bt.nextgen.api.allocation.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * @deprecated use account.v2.mode.allocation
 */
@Deprecated
public abstract class AllocationSummaryDto extends BaseDto implements Comparable<AllocationSummaryDto> {

    private String name;
    private BigDecimal marketValue;
    private BigDecimal allocationPercent;
    private List<HoldingAllocationDto> securities;

    public AllocationSummaryDto(String name, BigDecimal marketValue, BigDecimal allocationPercent,
            List<HoldingAllocationDto> securities) {
        super();
        this.marketValue = marketValue;
        this.allocationPercent = allocationPercent;
        this.name = name;
        this.securities = securities;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getMarketValue() {
        return marketValue;
    }

    public BigDecimal getAllocationPercent() {
        return allocationPercent;
    }

    public List<HoldingAllocationDto> getSecurities() {
        return securities;
    }

    @Override
    public int compareTo(AllocationSummaryDto o) {
        return name.compareToIgnoreCase(o.name);
    }
}
