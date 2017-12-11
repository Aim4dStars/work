package com.bt.nextgen.service.integration.order;

import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import org.joda.time.DateTime;

import java.math.BigInteger;
import java.util.List;

public interface OrderGroup {
    public String getOrderGroupId();

    public DateTime getLastUpdateDate();

    public String getOrderType();

    public List<OrderItem> getOrders();

    public String getReference();

    public ClientKey getOwner();

    public AccountKey getAccountKey();

    public List<ValidationError> getWarnings();

    public BigInteger getTransactionSeq();

    public String getOwnerName();

    public String getFirstNotification();
}
