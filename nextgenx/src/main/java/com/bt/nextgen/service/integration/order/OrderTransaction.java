package com.bt.nextgen.service.integration.order;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

public interface OrderTransaction extends com.btfin.panorama.core.security.integration.order.OrderIdentifier {
    public DateTime getTradeDate();

    public String getAccountId();

    public OrderType getOrderType();

    public String getAssetId();

    public List<OrderDetail> getDetails();

    public Long getFilledQuantity();

    public PriceType getPriceType();

    public BigDecimal getLimitPrice();

    public BigDecimal getOriginalQuantity();

    public BigDecimal getTransactionFee();

    public BigDecimal getConsideration();

    public DateTime getSettlementDate();

    public Boolean getCancellable();
}
