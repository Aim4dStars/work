package com.bt.nextgen.service.avaloq.order;

import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.order.OrderGroup;
import com.bt.nextgen.service.integration.order.OrderItem;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

import java.math.BigInteger;
import java.util.List;

public class OrderGroupImpl implements OrderGroup {
    @NotNull
    private String orderGroupId;

    @NotNull
    private DateTime lastUpdateDate;

    @NotNull
    private String orderType;

    private List<OrderItem> orders;

    private String reference;

    private ClientKey owner;

    private String ownerName;

    private AccountKey accountKey;

    private BigInteger transactionSeq;

    private List<ValidationError> warnings;

    private String firstNotification;

    public OrderGroupImpl() {
    }

    public OrderGroupImpl(AccountKey accountKey, String orderGroupId, ClientKey lastUpdatedBy, BigInteger transactionSeq,
            DateTime lastUpdateDate, List<OrderItem> orders, String reference) {
        this(accountKey, orderGroupId, lastUpdatedBy, lastUpdateDate, transactionSeq, orders, null, reference);
    }

    public OrderGroupImpl(AccountKey accountKey, String orderGroupId, ClientKey owner, DateTime lastUpdateDate,
            BigInteger transactionSeq, List<OrderItem> orders, List<ValidationError> warnings, String reference) {
        super();
        this.orderGroupId = orderGroupId;
        this.accountKey = accountKey;
        this.owner = owner;
        this.lastUpdateDate = lastUpdateDate;
        this.transactionSeq = transactionSeq;
        this.orders = orders;
        this.warnings = warnings;
        this.reference = reference;
    }

    public OrderGroupImpl(AccountKey accountKey, String orderGroupId, BigInteger transactionSeq, List<OrderItem> orders,
            List<ValidationError> warnings) {
        super();
        this.orderGroupId = orderGroupId;
        this.accountKey = accountKey;
        this.transactionSeq = transactionSeq;
        this.orders = orders;
        this.warnings = warnings;
    }

    @Override
    public String getOrderGroupId() {
        return orderGroupId;
    }

    @Override
    public List<ValidationError> getWarnings() {
        return warnings;
    }

    @Override
    public DateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    @Override
    public String getOrderType() {
        return orderType;
    }

    @Override
    public String getReference() {
        return reference;
    }

    @Override
    public ClientKey getOwner() {
        return owner;
    }

    @Override
    public AccountKey getAccountKey() {
        return accountKey;
    }

    @Override
    public List<OrderItem> getOrders() {
        return orders;
    }

    @Override
    public BigInteger getTransactionSeq() {
        return transactionSeq;
    }

    @Override
    public String getOwnerName() {
        return ownerName;
    }

    public void setOrderGroupId(String orderGroupId) {
        this.orderGroupId = orderGroupId;
    }

    public void setLastUpdateDate(DateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public void setOrders(List<OrderItem> orders) {
        this.orders = orders;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public void setOwner(ClientKey owner) {
        this.owner = owner;
    }

    public void setAccountKey(AccountKey accountKey) {
        this.accountKey = accountKey;
    }

    public void setWarnings(List<ValidationError> warnings) {
        this.warnings = warnings;
    }

    public void setTransactionSeq(BigInteger tranactionSeq) {
        this.transactionSeq = tranactionSeq;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @Override
    public String getFirstNotification() {
        return firstNotification;
    }

    public void setFirstNotification(String firstNotification) {
        this.firstNotification = firstNotification;
    }
}
