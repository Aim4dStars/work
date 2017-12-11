package com.bt.nextgen.service.integration.transaction;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.transactionhistory.TransactionHistory;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;

import java.util.List;

public interface TransactionIntegrationService extends DashboardTransactionIntegrationService {
    List<Transaction> loadScheduledTransactions(WrapAccountIdentifier identifier, ServiceErrors serviceErrors);

    List<TransactionHistory> loadRecentCashTransactions(WrapAccountIdentifier accountId, ServiceErrors serviceErrors);
}
