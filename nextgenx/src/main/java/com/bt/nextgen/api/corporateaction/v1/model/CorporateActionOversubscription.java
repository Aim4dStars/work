package com.bt.nextgen.api.corporateaction.v1.model;

import java.math.BigDecimal;

public class CorporateActionOversubscription {
    private BigDecimal maximumQuantity;
    private BigDecimal maximumPercent;

    public CorporateActionOversubscription(BigDecimal maximumQuantity, BigDecimal maximumPercent) {
        this.maximumQuantity = maximumQuantity;
        this.maximumPercent = maximumPercent;
    }

    public BigDecimal getMaximumQuantity() {
        return maximumQuantity;
    }

    public BigDecimal getMaximumPercent() {
        return maximumPercent;
    }

    @Override
    public String toString() {
        return "CorporateActionOversubscription{" +
                "maximumQuantity=" + maximumQuantity +
                ", maximumPercent=" + maximumPercent +
                '}';
    }
}
