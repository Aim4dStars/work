package com.bt.nextgen.api.order.model;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.fasterxml.jackson.annotation.JsonView;
import org.joda.time.DateTime;

import java.math.BigInteger;
import java.util.List;

public class OrderGroupDto extends BaseDto implements KeyedDto<OrderGroupKey> {
    @JsonView(JsonViews.Write.class)
    private OrderGroupKey key;

    @JsonView(JsonViews.Write.class)
    private DateTime lastUpdateDate;

    @JsonView(JsonViews.Write.class)
    private String reference;

    @JsonView(JsonViews.Write.class)
    private String status;

    @JsonView(JsonViews.Write.class)
    private BigInteger transactionSeq;

    @JsonView(JsonViews.Write.class)
    private List<OrderItemDto> orders;

    @JsonView(JsonViews.Write.class)
    private List<DomainApiErrorDto> warnings;

    private AccountKey accountKey;
    private String accountName;
    private String owner;
    private String ownerName;
    private String firstNotification;
    private boolean twoFaRequired;

    public OrderGroupDto() { // default constructor
    }

    @SuppressWarnings("squid:S00107")
    public OrderGroupDto(OrderGroupKey key, DateTime lastUpdateDate, BigInteger transactionSeq, List<OrderItemDto> orders,
            List<DomainApiErrorDto> warnings, String owner, String ownerName, String reference, String accountName,
            AccountKey accountKey) {
        super();
        this.key = key;
        this.lastUpdateDate = lastUpdateDate;
        this.transactionSeq = transactionSeq;
        this.warnings = warnings;
        this.orders = orders;
        this.owner = owner;
        this.ownerName = ownerName;
        this.reference = reference;
        this.accountName = accountName;
        this.accountKey = accountKey;
    }

    @Override
    public OrderGroupKey getKey() {
        return key;
    }

    public void setKey(OrderGroupKey key) {
        this.key = key;
    }

    public DateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(DateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public List<OrderItemDto> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderItemDto> orders) {
        this.orders = orders;
    }

    public List<DomainApiErrorDto> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<DomainApiErrorDto> warnings) {
        this.warnings = warnings;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public AccountKey getAccountKey() {
        return accountKey;
    }

    public void setAccountKey(AccountKey accountKey) {
        this.accountKey = accountKey;
    }

    public BigInteger getTransactionSeq() {
        return transactionSeq;
    }

    public void setTransactionSeq(BigInteger transactionSeq) {
        this.transactionSeq = transactionSeq;
    }

    public String getFirstNotification() {
        return firstNotification;
    }

    public void setFirstNotification(String firstNotification) {
        this.firstNotification = firstNotification;
    }

    public boolean isTwoFaRequired() {
        return twoFaRequired;
    }

    public void setTwoFaRequired(boolean twoFaRequired) {
        this.twoFaRequired = twoFaRequired;
    }
}
