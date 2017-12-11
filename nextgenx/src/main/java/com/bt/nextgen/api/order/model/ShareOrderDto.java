package com.bt.nextgen.api.order.model;



import com.bt.nextgen.config.JsonViews;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;

import java.math.BigDecimal;

/**
 * The Class ShareOrderDto holds extra information specific only to orders dealing with listed securities.
 */
@JsonTypeName("ShareOrder")
public class ShareOrderDto extends GeneralOrderDto {

    /**
     * This is the number of units that have been filled for this order.
     */
    private Integer filledQuantity;

    /**
     * This is the priceType set for the order. {@link com.bt.nextgen.service.integration.order.PriceType}
     */
    @JsonView(JsonViews.Write.class)
    private String priceType;

    /**
     * This is the expiry method for this order. {@link com.bt.nextgen.service.integration.order.ExpiryMethod}
     */
    @JsonView(JsonViews.Write.class)
    private String expiryType;

    /**
     * This is the limit price for this order, it will only be populated for LIMIT orders.
     */
    @JsonView(JsonViews.Write.class)
    private BigDecimal limitPrice;

    /**
     * This is the reason this order was rejected, it will only be populated in the case of Failed (Rejected) orders.
     */
    private String rejectionReason;
    
    /**
     * This is the panel broker's name.
     */
    private String brokerName;

    /**
     * This is the order id for the panel broker.
     */
    private String externalOrderId;

    /**
     * The number of cancellations (un-traded deletes)
     */
    private BigDecimal cancellationCount;

    /**
     * The maximum number of allowed cancellations (un-traded deletes)
     */
    private BigDecimal maxCancellationCount;

    public Integer getFilledQuantity() {
        return filledQuantity;
    }

    public void setFilledQuantity(Integer filledQuantity) {
        this.filledQuantity = filledQuantity;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public String getExpiryType() {
        return expiryType;
    }

    public void setExpiryType(String expiryType) {
        this.expiryType = expiryType;
    }

    public BigDecimal getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(BigDecimal limitPrice) {
        this.limitPrice = limitPrice;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public String getExternalOrderId() {
        return externalOrderId;
    }

    public void setExternalOrderId(String externalOrderId) {
        this.externalOrderId = externalOrderId;
    }

    public BigDecimal getCancellationCount() {
        return cancellationCount;
    }

    public void setCancellationCount(BigDecimal cancellationCount) {
        this.cancellationCount = cancellationCount;
    }

    public BigDecimal getMaxCancellationCount() {
        return maxCancellationCount;
    }

    public void setMaxCancellationCount(BigDecimal maxCancellationCount) {
        this.maxCancellationCount = maxCancellationCount;
    }
}
