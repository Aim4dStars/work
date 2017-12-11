package com.bt.nextgen.api.order.model;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.integration.order.OrderTransaction;

import java.math.BigDecimal;
import java.util.List;

public class TradeOrderDto extends BaseDto implements KeyedDto<OrderKey> {

    private final OrderKey key;
    private final String orderType;
    private final BigDecimal originalQuantity;
    private final AssetDto asset;
    private final String priceType;
    private final BigDecimal limitPrice;
    private final Boolean cancellable;
    private final List<OrderTransactionDto> orderTransactions;

    public TradeOrderDto(OrderKey key, String orderType, AssetDto asset, Boolean cancellable, OrderTransaction orderTransaction,
            List<OrderTransactionDto> orderTransactions) {
        this.key = key;
        this.orderType = orderType;
        this.originalQuantity = orderTransaction.getOriginalQuantity().abs();
        this.asset = asset;
        this.priceType = orderTransaction.getPriceType().getDisplayName();
        this.limitPrice = orderTransaction.getLimitPrice();
        this.cancellable = cancellable;
        this.orderTransactions = orderTransactions;
    }

    @Override
    public OrderKey getKey() {
        return key;
    }

    public String getOrderType() {
        return orderType;
    }

    public AssetDto getAsset() {
        return asset;
    }

    public String getPriceType() {
        return priceType;
    }

    public BigDecimal getLimitPrice() {
        return limitPrice;
    }

    public BigDecimal getOriginalQuantity() {
        return originalQuantity;
    }

    public List<OrderTransactionDto> getOrderTransactions() {
        return orderTransactions;
    }

    public BigDecimal getTotalFilledUnits() {
        BigDecimal totalFilledUnits = new BigDecimal(0);
        for (OrderTransactionDto orderTransaction : orderTransactions) {
            totalFilledUnits = totalFilledUnits.add(orderTransaction.getUnits());
        }
        return totalFilledUnits;
    }

    public BigDecimal getOutstandingUnits() {
        if (cancellable) {
            return originalQuantity.subtract(getTotalFilledUnits());
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getSumTotalConsideration() {
        BigDecimal sumTotalConsideration = BigDecimal.ZERO;
        for (OrderTransactionDto orderTransaction : orderTransactions) {
            sumTotalConsideration = sumTotalConsideration.add(orderTransaction.getConsideration());
        }
        return sumTotalConsideration;
    }

    public Boolean getCancellable() {
        return cancellable;
    }

}
