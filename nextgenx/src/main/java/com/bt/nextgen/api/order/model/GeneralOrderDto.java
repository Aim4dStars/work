package com.bt.nextgen.api.order.model;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.BaseDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import org.joda.time.DateTime;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", defaultImpl = GeneralOrderDto.class)
@JsonSubTypes({ @JsonSubTypes.Type(value = ShareOrderDto.class, name = "ShareOrder") })
public class GeneralOrderDto extends BaseDto implements OrderDto {
    @JsonView(JsonViews.Write.class)
    private OrderKey key;

    @JsonView(JsonViews.Write.class)
    private String status;

    @JsonView(JsonViews.Write.class)
    private Boolean cancellable;

    @JsonView(JsonViews.Write.class)
    private AssetDto asset;

    @JsonView(JsonViews.Write.class)
    private String lastTranSeqId;

    @JsonView(JsonViews.Write.class)
    private BigDecimal quantity;

    private DateTime submitDate;
    private String displayOrderId;
    private String origin;
    private String accountKey;
    private String accountName;
    private String accountNumber;
    private String orderType;
    private String assetCode;
    private BigDecimal amount;
    private BigDecimal price;
    private Boolean amendable;
    private Boolean contractNotes;
    private Boolean external;
    private BigDecimal brokerage;

    public GeneralOrderDto() {
        super();
    }

    @Override
    public String getDisplayOrderId() {
        return displayOrderId;
    }

    public void setDisplayOrderId(String displayOrderId) {
        this.displayOrderId = displayOrderId;
    }

    @Override
    public DateTime getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(DateTime submitDate) {
        this.submitDate = submitDate;
    }

    @Override
    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    @Override
    public String getAccountKey() {
        return accountKey;
    }

    public void setAccountKey(String accountKey) {
        this.accountKey = accountKey;
    }

    @Override
    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @Override
    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override
    public AssetDto getAsset() {
        return asset;
    }

    public void setAsset(AssetDto asset) {
        this.asset = asset;
    }

    /**
     * @deprecated assetCode is referenced from the asset.
     *
     */
    @Override
    @Deprecated
    public String getAssetCode() {
        return assetCode;
    }

    /**
     * @deprecated assetCode is referenced from the asset.
     *
     */
    @Deprecated
    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    @Override
    public Boolean getCancellable() {
        return cancellable;
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
    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public OrderKey getKey() {
        return key;
    }

    public void setKey(OrderKey orderKey) {
        this.key = orderKey;
    }

    @Override
    public Boolean getAmendable() {
        return amendable;
    }

    public void setAmendable(Boolean amendable) {
        this.amendable = amendable;
    }

    @Override
    public Boolean getContractNotes() {
        return contractNotes;
    }

    public void setContractNotes(Boolean contractNotes) {
        this.contractNotes = contractNotes;
    }

    @Override
    public Boolean getExternal() {
        return external;
    }

    public void setExternal(Boolean external) {
        this.external = external;
    }

    @Override
    public BigDecimal getBrokerage() {
        return brokerage;
    }

    public void setBrokerage(BigDecimal brokerage) {
        this.brokerage = brokerage;
    }
}
