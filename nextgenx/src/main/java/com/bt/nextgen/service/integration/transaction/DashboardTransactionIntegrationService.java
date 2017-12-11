package com.bt.nextgen.service.integration.transaction;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.transactionhistory.TransactionHistory;
import org.joda.time.DateTime;

import java.util.List;

/**
 * This interface defines the transaction methods for Account dashboard
 * Created by M035995 on 13/06/2017.
 */
public interface DashboardTransactionIntegrationService {

    List<TransactionHistory> loadTransactionHistory(String id, DateTime dateTo, DateTime dateFrom,
                                                           ServiceErrors serviceErrors);

    List<TransactionHistory> loadCashTransactionHistory(String id, DateTime dateTo, DateTime dateFrom,
                                                               ServiceErrors serviceErrors);
}