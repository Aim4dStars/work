package com.bt.nextgen.service.avaloq.order;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.integration.order.OrderDetail;
import com.bt.nextgen.service.integration.order.OrderTransaction;
import com.bt.nextgen.service.integration.order.OrderType;
import com.bt.nextgen.service.integration.order.PriceType;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

@ServiceBean(xpath = "doc", type = ServiceBeanType.CONCRETE)
public class OrderTransactionImpl implements OrderTransaction {
    private static final String XML_HEADER = "doc_head_list/doc_head/";

    @NotNull
    @ServiceElement(xpath = XML_HEADER + "doc_id/val")
    private String orderId;

    @NotNull
    @ServiceElement(xpath = XML_HEADER + "trx_date/val", converter = DateTimeTypeConverter.class)
    private DateTime tradeDate;

    @NotNull
    @ServiceElement(xpath = XML_HEADER + "bp_id/val")
    private String accountId;

    @NotNull
    @ServiceElement(xpath = XML_HEADER + "order_type_id/val", staticCodeCategory = "ORDER_TYPE")
    private OrderType orderType;

    @NotNull
    @ServiceElement(xpath = XML_HEADER + "asset_id/val")
    private String assetId;

    @ServiceElementList(xpath = XML_HEADER + "detail_list/detail", type = OrderDetailImpl.class)
    private List<OrderDetail> details;

    /**
     * This is the quantity that has been executed for a partially executed LS order. This field will only be populated for LS
     * orders. And if the order has been placed and not executed, it will return 0.
     */
    @ServiceElement(xpath = XML_HEADER + "fill_qty/val")
    private Long filledQuantity;

    /**
     * This is the price execution type for this order. It will only be populated for LS orders.
     */
    @ServiceElement(xpath = XML_HEADER + "exec_type/val", staticCodeCategory = "PRICE_TYPE")
    private PriceType priceType;

    /**
     * This is the limit price for this order. It will only be populated for LS orders and only when that order has a priceType of
     * PriceType.LIMIT .
     */
    @ServiceElement(xpath = XML_HEADER + "limit_price/val")
    private BigDecimal limitPrice;

    /**
     * This is the consideration for this order .
     */

    @ServiceElement(xpath = XML_HEADER + "qty/val")
    private BigDecimal consideration;
    /**
     * This is the settlement date for this order. It will only be populated for LS orders.
     */

    @ServiceElement(xpath = XML_HEADER + "val_date/val", converter = DateTimeTypeConverter.class)
    private DateTime settlementDate;

    /**
     * This is the transaction fee for this order. It will only be populated for LS orders.
     */
    @ServiceElement(xpath = XML_HEADER + "brokerage/val")
    private BigDecimal transactionFee;

    /**
     * This is the cancellable status for this order.
     */
    @NotNull
    @ServiceElement(xpath = XML_HEADER + "cancel_status/val")
    private Boolean cancellable;

    /**
     * This is the original order quantity for this order. It can be use in place of the qty found in the details of the order.
     */
    @ServiceElement(xpath = XML_HEADER + "orig_qty/val")
    private BigDecimal originalQuantity;

    @Override
    public String getOrderId() {
        return orderId;
    }

    @Override
    public DateTime getTradeDate() {
        return tradeDate;
    }

    @Override
    public String getAccountId() {
        return accountId;
    }

    @Override
    public OrderType getOrderType() {
        return orderType;
    }

    @Override
    public String getAssetId() {
        return assetId;
    }

    @Override
    public List<OrderDetail> getDetails() {
        return details;
    }

    @Override
    public Long getFilledQuantity() {
        return filledQuantity;
    }

    @Override
    public PriceType getPriceType() {
        return priceType;
    }

    @Override
    public BigDecimal getTransactionFee() {
        return transactionFee;
    }

    @Override
    public BigDecimal getLimitPrice() {
        return limitPrice;
    }

    @Override
    public DateTime getSettlementDate() {
        return settlementDate;
    }

    @Override
    public BigDecimal getOriginalQuantity() {
        return originalQuantity;
    }

    @Override
    public BigDecimal getConsideration() {
        return consideration;
    }

    @Override
    public Boolean getCancellable() {
        return cancellable;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setTradeDate(DateTime tradeDate) {
        this.tradeDate = tradeDate;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public void setDetails(List<OrderDetail> details) {
        this.details = details;
    }

    public void setFilledQuantity(Long filledQuantity) {
        this.filledQuantity = filledQuantity;
    }

    public void setPriceType(PriceType priceType) {
        this.priceType = priceType;
    }

    public void setLimitPrice(BigDecimal limitPrice) {
        this.limitPrice = limitPrice;
    }

    public void setConsideration(BigDecimal consideration) {
        this.consideration = consideration;
    }

    public void setSettlementDate(DateTime settlementDate) {
        this.settlementDate = settlementDate;
    }

    public void setTransactionFee(BigDecimal transactionFee) {
        this.transactionFee = transactionFee;
    }

    public void setCancellable(Boolean cancellable) {
        this.cancellable = cancellable;
    }

    public void setOriginalQuantity(BigDecimal originalQuantity) {
        this.originalQuantity = originalQuantity;
    }

}