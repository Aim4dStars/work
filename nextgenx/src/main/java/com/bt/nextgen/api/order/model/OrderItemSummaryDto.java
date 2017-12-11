package com.bt.nextgen.api.order.model;

import com.bt.nextgen.service.integration.order.PriceType;

import java.math.BigDecimal;
import java.math.BigInteger;

public class OrderItemSummaryDto {
    private final BigDecimal amount;
    private final boolean isFull;
    private final String distributionMethod;
    private final BigInteger units;
    private final BigDecimal price;
    private final String expiry;
    private final PriceType priceType;

    public OrderItemSummaryDto(BigDecimal amount, boolean isFull, String distributionMethod, BigInteger units, BigDecimal price,
            String expiry, PriceType priceType) {
        this.amount = amount;
        this.isFull = isFull;
        this.distributionMethod = distributionMethod;
        this.units = units;
        this.price = price;
        this.expiry = expiry;
        this.priceType = priceType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public boolean getIsFull() {
        return isFull;
    }

    public String getDistributionMethod() {
        return distributionMethod;
    }

    public BigInteger getUnits() {
        return units;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getExpiry() {
        return expiry;
    }

    public PriceType getPriceType() {
        return priceType;
    }
}
