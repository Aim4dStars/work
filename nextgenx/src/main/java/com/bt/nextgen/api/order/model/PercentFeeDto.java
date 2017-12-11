package com.bt.nextgen.api.order.model;

import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.service.avaloq.fees.FlatPercentFeesComponent;
import com.fasterxml.jackson.annotation.JsonView;

import java.math.BigDecimal;

public class PercentFeeDto {
    @JsonView(JsonViews.Write.class)
    private BigDecimal rate;

    public PercentFeeDto() {
        super();
    }

    public PercentFeeDto(FlatPercentFeesComponent component) {
        super();
        this.rate = component.getRate();
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

}
