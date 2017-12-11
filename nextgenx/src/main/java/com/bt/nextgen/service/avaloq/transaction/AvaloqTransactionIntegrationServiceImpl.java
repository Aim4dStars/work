package com.bt.nextgen.service.avaloq.transaction;

import com.bt.nextgen.reports.service.ReportGenerationServiceImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.avaloq.transactionhistory.TransactionHistoryConverter;
import com.bt.nextgen.service.avaloq.transactionhistory.TransactionHistoryHolderImpl;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.transaction.Transaction;
import com.bt.nextgen.service.integration.transaction.TransactionHolder;
import com.bt.nextgen.service.integration.transaction.TransactionIntegrationService;
import com.bt.nextgen.service.integration.transactionhistory.TransactionHistory;
import com.bt.nextgen.service.integration.transactionhistory.TransactionHistoryHolder;
import com.btfin.panorama.core.validation.Validator;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("AvaloqTransactionIntegrationServiceImpl")
public class AvaloqTransactionIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements
        TransactionIntegrationService {
    private static Logger logger = LoggerFactory.getLogger(AvaloqTransactionIntegrationServiceImpl.class);

    @Autowired
    private AvaloqExecute avaloqExecute;

    @Autowired
    private Validator validator;

    @Autowired
    private TransactionHistoryConverter transactionHistoryConverter;

    @Autowired
    private BankDateIntegrationService bankDate;

    /**
     * This method loads scheduled transactions of all types for one account.
     */
    @Override
    public List<Transaction> loadScheduledTransactions(WrapAccountIdentifier identifier, final ServiceErrors serviceErrors) {
        List<Transaction> scdTransactions = new ArrayList<>();

        TransactionHolder transactions = avaloqExecute.executeReportRequestToDomain(new AvaloqReportRequest(
                        Template.SCHEDULED_TRANSACTION_LIST.getName()).forScheduledTransactionAccount(identifier.getAccountIdentifier()),
                TransactionHolderImpl.class, serviceErrors);

        if (transactions != null && transactions.getScheduledTransactions() != null) {
            scdTransactions = transactions.getScheduledTransactions();
            logger.info("Loaded {} scheduled transactions for portfolio ID {} ", transactions.getScheduledTransactions().size(),
                    identifier.getAccountIdentifier());
        }

        return scdTransactions;
    }

    @Override
    public List<TransactionHistory> loadTransactionHistory(final String accountId, final DateTime dateTo,
                                                           final DateTime dateFrom, final ServiceErrors serviceErrors) {
        List<TransactionHistory> transactions = new ArrayList<>();
        List<TransactionHistory> completeTransactions = new ArrayList<>();

        AvaloqReportRequest req = new AvaloqReportRequest(Template.TRANSACTION_HISTORY.getName()).forAccount(accountId)
                .forDateTime(ReportGenerationServiceImpl.PARAM_TRX_DATE_FROM, dateFrom)
                .forDateTime(ReportGenerationServiceImpl.PARAM_TRX_DATE_TO, dateTo);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        TransactionHistoryHolder transactionHolder = avaloqExecute.executeReportRequestToDomain(req,
                TransactionHistoryHolderImpl.class, serviceErrors);

        if (transactionHolder != null && transactionHolder.getTransactions() != null) {
            transactions = transactionHistoryConverter.setExtraDetails(transactionHolder.getTransactions(), serviceErrors);
            completeTransactions = transactionHistoryConverter.setTransactionSubTypes(transactions);
        }
        stopWatch.stop();
        logger.info("AvaloqTransactionIntegrationServiceImpl::loadCashTransactionHistory: avaloq request timetaken= {} " +
                        "ms, {} count ", stopWatch.getTime(),
                (transactionHolder != null && CollectionUtils.isNotEmpty(transactionHolder.getTransactions())) ? transactionHolder.getTransactions().size() : "Empty collection");

        return completeTransactions;
    }

    @Override
    public List<TransactionHistory> loadCashTransactionHistory(String portfolioId, DateTime dateTo, DateTime dateFrom,
                                                               ServiceErrors serviceErrors) {
        List<TransactionHistory> pastTransactions = new ArrayList<>();

        TransactionHistoryHolder transactions = avaloqExecute.executeReportRequestToDomain(
                new AvaloqReportRequest(Template.CASH_PAST_TRANSACTIONS.getName()).forPastTransactions(
                        portfolioId, dateFrom.toString(), dateTo.toString()), TransactionHistoryHolderImpl.class, serviceErrors);

        if (transactions != null && transactions.getTransactions() != null) {
            pastTransactions = transactionHistoryConverter.evaluateBalanceAndSystemTransaction(transactions.getTransactions(),
                    bankDate.getBankDate(serviceErrors));
        }

        return pastTransactions;

    }

    @Override
    public List<TransactionHistory> loadRecentCashTransactions(WrapAccountIdentifier portfolio, ServiceErrors serviceErrors) {
        List<TransactionHistory> pastTransactions = new ArrayList<>();

        TransactionHistoryHolder transactions = avaloqExecute.executeReportRequestToDomain(new AvaloqReportRequest(
                        Template.CASH_RECENT_PAST_TRANSACTIONS.getName()).forAccount(portfolio.getAccountIdentifier()),
                TransactionHistoryHolderImpl.class, serviceErrors);

        if (transactions != null && transactions.getTransactions() != null) {
            pastTransactions = transactionHistoryConverter.evaluateBalanceAndSystemTransaction(transactions.getTransactions(),
                    bankDate.getBankDate(serviceErrors));
        }

        return pastTransactions;
    }
}
