package com.bt.nextgen.api.order.model;

import org.joda.time.DateTime;

import java.math.BigDecimal;

public class OrderTransactionDto {
    private final DateTime tradeDate;
    private final BigDecimal units;
    private final BigDecimal price;
    private final BigDecimal transactionFee;
    private final BigDecimal consideration;
    private final DateTime settlementDate;

    public OrderTransactionDto(DateTime tradeDate, BigDecimal units, BigDecimal price, BigDecimal transactionFee,
            BigDecimal consideration, DateTime settlementDate) {
        this.tradeDate = tradeDate;
        this.units = units;
        this.price = price;
        this.transactionFee = transactionFee;
        this.consideration = consideration;
        this.settlementDate = settlementDate;
    }

    public DateTime getTradeDate() {
        return tradeDate;
    }

    public BigDecimal getUnits() {
        return units;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getTransactionFee() {
        return transactionFee;
    }

    public BigDecimal getConsideration() {
        return consideration;
    }

    public DateTime getSettlementDate() {
        return settlementDate;
    }
}
