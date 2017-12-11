package com.bt.nextgen.service.integration.transactionhistory;

import java.math.BigDecimal;

public interface TransactionSubType {
    String getTransactionSubType();

    BigDecimal getTransactionSubTypeAmount();

    String getTransactionSubTypeDescription();

    String getTransactionType();
}
