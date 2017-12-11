package com.bt.nextgen.service.avaloq.order;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.integration.Origin;
import com.bt.nextgen.service.integration.order.ExpiryMethod;
import com.bt.nextgen.service.integration.order.Order;
import com.bt.nextgen.service.integration.order.OrderDetail;
import com.bt.nextgen.service.integration.order.OrderStatus;
import com.bt.nextgen.service.integration.order.OrderType;
import com.bt.nextgen.service.integration.order.PriceType;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@ServiceBean(xpath = "doc", type = ServiceBeanType.CONCRETE)
public class OrderImpl implements Order {
    private static final String XML_HEADER = "doc_head_list/doc_head/";

    @NotNull
    @ServiceElement(xpath = XML_HEADER + "doc_id/val")
    private String orderId;

    @ServiceElement(xpath = XML_HEADER + "doc_id/val")
    private String displayOrderId;

    @NotNull
    @ServiceElement(xpath = XML_HEADER + "order_date/val", converter = DateTimeTypeConverter.class)
    private DateTime createDate;

    @NotNull
    @ServiceElement(xpath = XML_HEADER + "medium_id/val", staticCodeCategory = "MEDIUM")
    private Origin origin;

    @NotNull
    @ServiceElement(xpath = XML_HEADER + "bp_id/val")
    private String accountId;

    @NotNull
    @ServiceElement(xpath = XML_HEADER + "order_type_id/val", staticCodeCategory = "ORDER_TYPE")
    private OrderType orderType;

    @NotNull
    @ServiceElement(xpath = XML_HEADER + "asset_id/val")
    private String assetId;

    @ServiceElement(xpath = XML_HEADER + "qty/val")
    private BigDecimal amount;

    @ServiceElement(xpath = XML_HEADER + "brokerage/val", converter = BigDecimalConverter.class)
    private BigDecimal brokerage;

    @ServiceElement(xpath = XML_HEADER + "net_amt/val")
    private BigDecimal netAmount;

    @ServiceElement(xpath = XML_HEADER + "price_estim/val")
    private BigDecimal estimatedPrice;

    @NotNull
    @ServiceElement(xpath = XML_HEADER + "ui_wfs_id/val", staticCodeCategory = "ORDER_STATUS")
    private OrderStatus status;

    @NotNull
    @ServiceElement(xpath = XML_HEADER + "cancel_status/val")
    private Boolean cancellable;

    @ServiceElement(xpath = XML_HEADER + "last_trans_seq_nr/val")
    private String lastTranSeqId;

    @ServiceElementList(xpath = XML_HEADER + "detail_list/detail", type = OrderDetailImpl.class)
    private List<OrderDetail> details;

    /**
     * This is the quantity that has been executed for a partially executed LS order. This field will only be populated for LS
     * orders. And if the order has been placed and not executed, it will return 0.
     */
    @ServiceElement(xpath = XML_HEADER + "fill_qty/val")
    private Integer filledQuantity;

    /**
     * This is the expiry type for this order. It will only be populated for LS orders.
     */
    @ServiceElement(xpath = XML_HEADER + "expir_type/val", staticCodeCategory = "EXPIRY_METHOD")
    private ExpiryMethod expiryType;

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
     * This is the rejection reason for this order. It will only be populated for LS orders and only when that order has a status
     * of OrderStatus.FAILED.
     */
    @ServiceElement(xpath = XML_HEADER + "nack_reason/val")
    private String rejectionReason;

    /**
     * This is the total fillable quantity for this order. It can be used in place of the qty found in the details of the order.
     */
    @ServiceElement(xpath = XML_HEADER + "orig_qty/val")
    private BigDecimal originalQuantity;

    @ServiceElement(xpath = XML_HEADER + "contr_notes/val")
    private Boolean contractNotes;

    /**
     * This is the panel broker's name.
     */
    @ServiceElement(xpath = XML_HEADER + "cnt_name/val")
    private String brokerName;

    /**
     * This is the order id for the panel broker.
     */
    @ServiceElement(xpath = XML_HEADER + "extl_ref_nr/val")
    private String externalOrderId;

    @ServiceElement(xpath = XML_HEADER + "trx_date/val", converter = DateTimeTypeConverter.class)
    private DateTime tradeDate;

    /**
     * Count of cancellations (per container per asset per day)
     */
    @ServiceElement(xpath = XML_HEADER + "untrd_canc_cnt/val")
    private BigDecimal cancellationCount;

    /**
     * Number of allowed cancellations (per container per asset per day)
     */
    @ServiceElement(xpath = XML_HEADER + "max_alw_untrd_canc/val")
    private BigDecimal maxCancellationCount;

    public OrderImpl() {
    }

    @Override
    public String getOrderId() {
        return orderId;
    }

    @Override
    public String getDisplayOrderId() {
        return displayOrderId;
    }

    @Override
    public DateTime getCreateDate() {
        return createDate;
    }

    @Override
    public Origin getOrigin() {
        return origin;
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
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    @Override
    public BigDecimal getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(BigDecimal estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }

    @Override
    public OrderStatus getStatus() {
        return status;
    }

    @Override
    public Boolean getCancellable() {
        return cancellable;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setDisplayOrderId(String displayOrderId) {
        this.displayOrderId = displayOrderId;
    }

    public void setCreateDate(DateTime createDate) {
        this.createDate = createDate;
    }

    public void setOrigin(Origin origin) {
        this.origin = origin;
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

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setCancellable(Boolean cancellable) {
        this.cancellable = cancellable;
    }

    @Override
    public String getLastTranSeqId() {
        return lastTranSeqId;
    }

    public void setLastTranSeqId(String lastTranSeqId) {
        this.lastTranSeqId = lastTranSeqId;
    }

    @Override
    public List<OrderDetail> getDetails() {
        return details;
    }

    public void setDetails(List<OrderDetail> details) {
        this.details = details;
    }

    @Override
    public Integer getFilledQuantity() {
        return filledQuantity;
    }

    public void setFilledQuantity(Integer filledQuantity) {
        this.filledQuantity = filledQuantity;
    }

    @Override
    public ExpiryMethod getExpiryType() {
        return expiryType;
    }

    public void setExpiryType(ExpiryMethod expiryType) {
        this.expiryType = expiryType;
    }

    @Override
    public PriceType getPriceType() {
        return priceType;
    }

    public void setPriceType(PriceType priceType) {
        this.priceType = priceType;
    }

    @Override
    public BigDecimal getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(BigDecimal limitPrice) {
        this.limitPrice = limitPrice;
    }

    @Override
    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    @Override
    public BigDecimal getOriginalQuantity() {
        return originalQuantity;
    }

    public void setOriginalQuantity(BigDecimal originalQuantity) {
        this.originalQuantity = originalQuantity;
    }

    @Override
    public Boolean getContractNotes() {
        return contractNotes;
    }

    public void setContractNotes(Boolean contractNotes) {
        this.contractNotes = contractNotes;
    }

    @Override
    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    @Override
    public String getExternalOrderId() {
        return externalOrderId;
    }

    public void setExternalOrderId(String externalOrderId) {
        this.externalOrderId = externalOrderId;
    }

    @Override
    public DateTime getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(DateTime tradeDate) {
        this.tradeDate = tradeDate;
    }

    @Override
    public BigDecimal getCancellationCount() {
        return cancellationCount;
    }

    public void setCancellationCount(BigDecimal cancellationCount) {
        this.cancellationCount = cancellationCount;
    }

    @Override
    public BigDecimal getMaxCancellationCount() {
        return maxCancellationCount;
    }

    public void setMaxCancellationCount(BigDecimal maxCancellationCount) {
        this.maxCancellationCount = maxCancellationCount;
    }

    @Override
    public BigDecimal getBrokerage() {
        return brokerage;
    }
}
