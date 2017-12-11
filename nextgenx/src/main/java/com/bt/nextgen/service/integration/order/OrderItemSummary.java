package com.bt.nextgen.service.integration.order;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface OrderItemSummary {
    public BigDecimal getAmount();

    public Boolean getIsFull();

    public String getDistributionMethod();

    public BigInteger getUnits();

    public BigDecimal getPrice();

    public String getExpiry();

    public PriceType getPriceType();
}
