package com.bt.nextgen.service.avaloq.order;

import com.bt.nextgen.service.integration.order.OrderItemSummary;
import com.bt.nextgen.service.integration.order.PriceType;

import javax.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;

public class OrderItemSummaryImpl implements OrderItemSummary {
    @NotNull
    private BigDecimal amount;

    @NotNull
    private boolean isFull;

    private String distributionMethod;

    private BigInteger units;

    private BigDecimal price;

    private String expiry;

    private PriceType priceType;

    public OrderItemSummaryImpl() {
        super();
    }

    public OrderItemSummaryImpl(BigDecimal amount, boolean isFull, String distributionMethod, BigInteger units, BigDecimal price,
            String expiry, PriceType priceType) {
        this.amount = amount;
        this.isFull = isFull;
        this.distributionMethod = distributionMethod;
        this.units = units;
        this.price = price;
        this.expiry = expiry;
        this.priceType = priceType;
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public Boolean getIsFull() {
        return isFull;
    }

    @Override
    public String getDistributionMethod() {
        return distributionMethod;
    }

    @Override
    public BigInteger getUnits() {
        return units;
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public String getExpiry() {
        return expiry;
    }

    @Override
    public PriceType getPriceType() {
        return priceType;
    }
}
