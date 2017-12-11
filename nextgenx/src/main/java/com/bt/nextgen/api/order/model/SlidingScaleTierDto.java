package com.bt.nextgen.api.order.model;

import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.service.avaloq.fees.SlidingScaleTiers;
import com.fasterxml.jackson.annotation.JsonView;

import java.math.BigDecimal;

public class SlidingScaleTierDto {
    @JsonView(JsonViews.Write.class)
    private BigDecimal lowerBound;

    @JsonView(JsonViews.Write.class)
    private BigDecimal upperBound;

    @JsonView(JsonViews.Write.class)
    private BigDecimal rate;

    public SlidingScaleTierDto() {
        super();
    }

    public SlidingScaleTierDto(SlidingScaleTiers tier) {
        this.lowerBound = tier.getLowerBound();
        this.upperBound = tier.getUpperBound();
        this.rate = tier.getPercent();
    }

    public BigDecimal getLowerBound() {
        if (lowerBound == null) {
            return BigDecimal.ZERO;
        }
        return lowerBound;
    }

    public void setLowerBound(BigDecimal lowerBound) {
        this.lowerBound = lowerBound;
    }

    public BigDecimal getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(BigDecimal upperBound) {
        this.upperBound = upperBound;
    }

    public BigDecimal getRate() {
        if (rate == null) {
            return BigDecimal.ZERO;
        }
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

}
