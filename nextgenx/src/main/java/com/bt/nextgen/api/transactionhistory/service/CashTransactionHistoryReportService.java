package com.bt.nextgen.api.transactionhistory.service;

import com.bt.nextgen.api.transactionhistory.model.CashTransactionHistoryDto;
import org.joda.time.DateTime;

public interface CashTransactionHistoryReportService
{
    /**
     * Finds a CashTransactionHistoryDto by it's receiptNo.
     *
     * @param accountId for which the transaction belongs to
     * @param direction CREDIT/DEBIT
     * @param startDate from when to start looking for transactions
     * @param endDate look for transactions up to this date
     * @param receiptNo receipt number of the transaction being retrieved for the date range
     * @return the found CashTransactionHistoryDto, null if not found.
     */
    CashTransactionHistoryDto retrievePastTransaction(String accountId, String direction, DateTime startDate,
                                                      DateTime endDate, String receiptNo);
}
