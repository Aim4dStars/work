package com.bt.nextgen.service.integration.order;

import com.bt.nextgen.service.integration.Origin;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

public interface Order extends com.btfin.panorama.core.security.integration.order.OrderIdentifier {
    public DateTime getCreateDate();

    public Origin getOrigin();

    public String getAccountId();

    public OrderType getOrderType();

    public String getAssetId();

    public BigDecimal getAmount();

    public BigDecimal getNetAmount();

    public BigDecimal getEstimatedPrice();

    public OrderStatus getStatus();

    public Boolean getCancellable();

    public String getDisplayOrderId();

    public String getLastTranSeqId();

    public List<OrderDetail> getDetails();

    Integer getFilledQuantity();

    ExpiryMethod getExpiryType();

    PriceType getPriceType();

    BigDecimal getLimitPrice();

    String getRejectionReason();

    BigDecimal getOriginalQuantity();

    Boolean getContractNotes();
    
    String getBrokerName();
    
    String getExternalOrderId();
    
    DateTime getTradeDate();

    BigDecimal getCancellationCount();

    BigDecimal getMaxCancellationCount();

    /* Gets the brokerage for the investment*/
    BigDecimal getBrokerage();
}
