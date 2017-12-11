package com.bt.nextgen.service.wrap.integration.transaction;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.transaction.WrapCashStatementConverter;
import com.bt.nextgen.service.avaloq.transactionhistory.TransactionHistoryConverter;
import com.bt.nextgen.service.avaloq.transactionhistory.WrapTransactionHistoryConverter;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.transaction.DashboardTransactionIntegrationService;
import com.bt.nextgen.service.integration.transactionhistory.TransactionHistory;
import com.btfin.panorama.wrap.model.CashStatement;
import com.btfin.panorama.wrap.service.TransactionService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("wrapTransactionIntegrationServiceImpl")
@Profile("WrapOffThreadImplementation")
public class WrapTransactionIntegrationService implements DashboardTransactionIntegrationService {

    private static Logger logger = LoggerFactory.getLogger(WrapTransactionIntegrationService.class);

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionHistoryConverter transactionHistoryConverter;

    @Autowired
    private WrapTransactionHistoryConverter wrapTransactionHistoryConverter;

    /*This would not be required here going forward*/
    @Autowired
    private BankDateIntegrationService bankDate;

    @Override
    public List<TransactionHistory> loadTransactionHistory(String migrationId, DateTime dateTo, DateTime dateFrom, ServiceErrors serviceErrors) {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<com.btfin.panorama.wrap.model.TransactionHistory> wrapTransactionHistories =
                transactionService.getTransactionHistoryForClient(migrationId, dateFrom, dateTo, serviceErrors);
        List<TransactionHistory> transactionHistories = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(wrapTransactionHistories)) {
            transactionHistories = wrapTransactionHistoryConverter.convertWrapTransactionsToPanorama(wrapTransactionHistories, serviceErrors);
        }

        stopWatch.stop();
        logger.info("WrapTransactionServiceIntegration::loadTransactionHistory: wrap request timetaken= {} ms , {} count", stopWatch.getTime(), transactionHistories.size());
        return transactionHistories;
    }

    @Override
    public List<TransactionHistory> loadCashTransactionHistory(String migrationId, DateTime dateTo, DateTime dateFrom, ServiceErrors serviceErrors) {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<CashStatement> cashStatements = transactionService.getCashStatementForClient(migrationId, dateFrom, dateTo, serviceErrors);
        stopWatch.stop();

        final List<TransactionHistory> updatedPastTransactions = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(cashStatements)) {
            logger.info("WrapTransactionServiceIntegration::loadCashTransactionHistory: wrap request timetaken= {} ms , {} count", stopWatch.getTime(), cashStatements.size());
            stopWatch.reset();
            stopWatch.start();
            updatedPastTransactions.addAll(WrapCashStatementConverter.toTransactions(cashStatements));
            stopWatch.stop();
            logger.info("WrapTransactionServiceIntegration::loadCashTransactionHistory: wrap object massaging timetaken= {} ms", stopWatch.getTime());
        }
        else {
            logger.info("WrapTransactionServiceIntegration::loadCashTransactionHistory: wrap request timetaken= {} ms , {} count", stopWatch.getTime(), "empty collection");
        }
        return transactionHistoryConverter.evaluateBalanceAndSystemTransaction(updatedPastTransactions,
                bankDate.getBankDate(serviceErrors));
    }
}