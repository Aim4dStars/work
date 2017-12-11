package com.bt.nextgen.service.integration.transfer;

import com.bt.nextgen.service.integration.order.OrderStatus;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public interface TransferItem {

    public String getSettlementId();

    public OrderStatus getTransferStatus();

    public String getAssetId();

    public BigDecimal getQuantity();

    public BigDecimal getAmount();

    public DateTime getTransactionDateTime();

}
