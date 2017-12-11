package com.bt.nextgen.service.wrap.integration;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.base.AvaloqAccountIntegrationService;
import com.bt.nextgen.service.integration.base.ThirdPartyDetails;
import com.bt.nextgen.service.integration.transaction.DashboardTransactionIntegrationService;
import com.bt.nextgen.service.integration.transaction.TransactionIntegrationService;
import com.bt.nextgen.service.integration.transactionhistory.TransactionHistory;
import com.btfin.panorama.core.concurrent.AbstractConcurrentComplete;
import com.btfin.panorama.core.concurrent.Concurrent;
import com.btfin.panorama.core.concurrent.ConcurrentCallable;
import com.btfin.panorama.core.concurrent.ConcurrentComplete;
import com.btfin.panorama.core.concurrent.ConcurrentResult;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("ThirdPartyTransactionIntegrationService")
@Profile({"WrapOffThreadImplementation"})
public class TransactionIntegrationServiceImpl implements DashboardTransactionIntegrationService {

    @Autowired
    @Qualifier("wrapTransactionIntegrationServiceImpl")
    private DashboardTransactionIntegrationService wrapTransactionIntegrationService;

    @Autowired
    @Qualifier("AvaloqTransactionIntegrationServiceImpl")
    private TransactionIntegrationService transactionIntegrationService;

    /*This service will return third party details (WRAP, ASGARD) from Avaloq*/
    @Autowired
    @Qualifier("ThirdPartyAvaloqAccountIntegrationService")
    private AvaloqAccountIntegrationService avaloqAccountIntegrationService;

    @Override
    public List<TransactionHistory> loadTransactionHistory(String accountId, DateTime dateTo, DateTime dateFrom, ServiceErrors serviceErrors) {
        List<TransactionHistory> transactions = new ArrayList<>();
        ThirdPartyDetails thirdPartyDetails = getThirdPartyDetails(AccountKey.valueOf(accountId), serviceErrors);
        if (null != thirdPartyDetails.getMigrationDate() &&
                (dateFrom.isEqual(thirdPartyDetails.getMigrationDate()) || dateFrom.isBefore(thirdPartyDetails.getMigrationDate()))) {
            Concurrent.when(loadPanoramaTransactionHistory(accountId, dateTo, dateFrom, serviceErrors),
                    loadWrapTransactionHistory(thirdPartyDetails.getMigrationKey(), dateTo, dateFrom, serviceErrors)).done(processAllTransactionHistory(transactions)).execute();
        }
        else {
            transactions = transactionIntegrationService.loadTransactionHistory(accountId, dateTo, dateFrom, serviceErrors);
        }
        return transactions;
    }

    private ConcurrentCallable<List<TransactionHistory>> loadPanoramaTransactionHistory(final String accountId, final DateTime dateTo,
                                                                                        final DateTime dateFrom, final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<List<TransactionHistory>>() {
            @Override
            public List<TransactionHistory> call() {
                return transactionIntegrationService.loadTransactionHistory(accountId, dateTo, dateFrom, serviceErrors);
            }
        };
    }

    private ConcurrentCallable<List<TransactionHistory>> loadWrapTransactionHistory(final String migrationId, final DateTime dateTo,
                                                                                    final DateTime dateFrom, final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<List<TransactionHistory>>() {
            @Override
            public List<TransactionHistory> call() {
                return wrapTransactionIntegrationService.loadTransactionHistory(migrationId, dateTo, dateFrom, serviceErrors);
            }
        };
    }

    private ConcurrentComplete processAllTransactionHistory(final List<TransactionHistory> transactions) {
        return new AbstractConcurrentComplete() {
            @Override
            public void run() {
                List<? extends ConcurrentResult<?>> results = this.getResults();
                for (ConcurrentResult concurrentResult : results) {
                    if (concurrentResult != null) {
                        List<TransactionHistory> transactionResult = (List<TransactionHistory>) concurrentResult.getResult();
                        transactions.addAll(transactionResult);
                    }
                }
            }
        };
    }

    @Override
    public List<TransactionHistory> loadCashTransactionHistory(String accountId, DateTime dateTo,
                                                               DateTime dateFrom, ServiceErrors serviceErrors) {
        List<TransactionHistory> transactions = new ArrayList<>();
        ThirdPartyDetails thirdPartyDetails = getThirdPartyDetails(AccountKey.valueOf(accountId), serviceErrors);
        if (null != thirdPartyDetails.getMigrationDate() &&
                (dateFrom.isEqual(thirdPartyDetails.getMigrationDate()) || dateFrom.isBefore(thirdPartyDetails.getMigrationDate()))) {
            Concurrent.when(loadPanoramaCashTransactionHistory(accountId, dateTo, dateFrom, serviceErrors),
                    loadWrapCashTransactionHistory(thirdPartyDetails.getMigrationKey(), dateTo, dateFrom, serviceErrors))
                    .done(processAllTransactionHistory(transactions)).execute();
        }
        else {
            transactions = transactionIntegrationService.loadCashTransactionHistory(accountId, dateTo, dateFrom, serviceErrors);
        }
        return transactions;
    }

    private ConcurrentCallable<List<TransactionHistory>> loadPanoramaCashTransactionHistory(final String accountId, final DateTime dateTo,
                                                                                            final DateTime dateFrom, final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<List<TransactionHistory>>() {
            @Override
            public List<TransactionHistory> call() {
                return transactionIntegrationService.loadCashTransactionHistory(accountId, dateTo, dateFrom, serviceErrors);
            }
        };
    }

    private ConcurrentCallable<List<TransactionHistory>> loadWrapCashTransactionHistory(final String migrationId, final DateTime dateTo,
                                                                                        final DateTime dateFrom, final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<List<TransactionHistory>>() {
            @Override
            public List<TransactionHistory> call() {
                return wrapTransactionIntegrationService.loadCashTransactionHistory(migrationId, dateTo, dateFrom, serviceErrors);
            }
        };
    }

    private ThirdPartyDetails getThirdPartyDetails(AccountKey accountKey, ServiceErrors serviceErrors) {
        // Get the M# and other details from ThirdPartyAvaloqAccountIntegrationService
        return avaloqAccountIntegrationService.getThirdPartySystemDetails(accountKey, serviceErrors);
    }

}