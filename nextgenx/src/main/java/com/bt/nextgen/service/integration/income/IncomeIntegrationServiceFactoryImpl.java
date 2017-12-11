package com.bt.nextgen.service.integration.income;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.income.WrapAccountIncomeDetailsImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.base.AvaloqAccountIntegrationService;
import com.bt.nextgen.service.integration.base.ThirdPartyDetails;
import com.bt.nextgen.service.wrap.integration.income.WrapIncomeIntegrationService;
import com.btfin.panorama.core.concurrent.AbstractConcurrentComplete;
import com.btfin.panorama.core.concurrent.Concurrent;
import com.btfin.panorama.core.concurrent.ConcurrentCallable;
import com.btfin.panorama.core.concurrent.ConcurrentComplete;
import com.btfin.panorama.core.concurrent.ConcurrentResult;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Retrieves various investment income from avaloq and wrap.
 * Created by L067221 on 9/08/2017.
 */
@Service
@Profile({"WrapOffThreadImplementation"})
public class IncomeIntegrationServiceFactoryImpl implements IncomeIntegrationServiceFactory {

    @Autowired
    private IncomeIntegrationService incomeIntegrationService;

    @Autowired
    private WrapIncomeIntegrationService wrapIncomeIntegrationService;

    /*This service will return third party details (WRAP, ASGARD) from Avaloq*/
    @Autowired
    @Qualifier("ThirdPartyAvaloqAccountIntegrationService")
    private AvaloqAccountIntegrationService avaloqAccountIntegrationService;

    @Override
    public List<WrapAccountIncomeDetails> loadIncomeReceivedDetails(AccountKey accountKey, DateTime startDate, DateTime endDate, ServiceErrors serviceErrors) {

        List<SubAccountIncomeDetails> incomeDetailsList = new ArrayList<>();
        // Get the M# and other details from ThirdPartyAvaloqAccountIntegrationService
        final ThirdPartyDetails thirdPartyDetails = avaloqAccountIntegrationService.getThirdPartySystemDetails(accountKey, serviceErrors);
        final DateTime migrationDate = thirdPartyDetails.getMigrationDate();
        if (null == migrationDate || startDate.isEqual(migrationDate) || startDate.isAfter(migrationDate)) {
            final List<WrapAccountIncomeDetails> wrapAccountIncomeDetailsList = incomeIntegrationService.
                    loadIncomeReceivedDetails(accountKey, startDate, endDate, serviceErrors);
            if (CollectionUtils.isNotEmpty(wrapAccountIncomeDetailsList)) {
                incomeDetailsList = wrapAccountIncomeDetailsList.get(0).getSubAccountIncomeDetailsList();
            }
        }
        else if (endDate.isBefore(migrationDate)) {
            incomeDetailsList = wrapIncomeIntegrationService.loadIncomeReceivedDetails(thirdPartyDetails.getMigrationKey(),
                    startDate, endDate, serviceErrors);
        }
        else {
            Concurrent.when(loadPanoramaIncomeReceivedDetails(accountKey, startDate, endDate, serviceErrors),
                    loadWrapIncomeReceivedDetails(thirdPartyDetails.getMigrationKey(), startDate, endDate, serviceErrors))
                    .done(processAllIncomeReceivedDetails(incomeDetailsList)).execute();
        }

        final WrapAccountIncomeDetails wrapIncomeDetails = new WrapAccountIncomeDetailsImpl();
        ((WrapAccountIncomeDetailsImpl) wrapIncomeDetails).
                setSubAccountIncomeDetailsList(incomeDetailsList);

        final List<WrapAccountIncomeDetails> wrapAccounts = new ArrayList<>();
        wrapAccounts.add(wrapIncomeDetails);

        return wrapAccounts;
    }

    /**
     * Retrieve avaloq investment income received
     *
     * @param accountKey
     * @param startDate
     * @param endDate
     * @param serviceErrors
     *
     * @return
     */
    private ConcurrentCallable<List<SubAccountIncomeDetails>> loadPanoramaIncomeReceivedDetails(final AccountKey accountKey,
                                                                                                final DateTime startDate,
                                                                                                final DateTime endDate,
                                                                                                final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<List<SubAccountIncomeDetails>>() {
            @Override
            public List<SubAccountIncomeDetails> call() {
                List<SubAccountIncomeDetails> subAccountIncomeDetails = new ArrayList<>();
                final List<WrapAccountIncomeDetails> accountIncomes = incomeIntegrationService.loadIncomeReceivedDetails(accountKey,
                        startDate, endDate, serviceErrors);
                if (CollectionUtils.isNotEmpty(accountIncomes)) {
                    subAccountIncomeDetails = accountIncomes.get(0).getSubAccountIncomeDetailsList();
                }

                return subAccountIncomeDetails;
            }
        };
    }

    /**
     * Retrieve wrap investment income received
     *
     * @param clientId
     * @param startDate
     * @param endDate
     * @param serviceErrors
     *
     * @return
     */
    private ConcurrentCallable<List<SubAccountIncomeDetails>> loadWrapIncomeReceivedDetails(final String clientId,
                                                                                            final DateTime startDate,
                                                                                            final DateTime endDate,
                                                                                            final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<List<SubAccountIncomeDetails>>() {
            @Override
            public List<SubAccountIncomeDetails> call() {
                return wrapIncomeIntegrationService.loadIncomeReceivedDetails(clientId, startDate, endDate, serviceErrors);
            }
        };
    }

    /**
     * Process wrap and avaloq investment income received
     *
     * @param incomeDetailsList
     *
     * @return
     */
    private ConcurrentComplete processAllIncomeReceivedDetails(final List<SubAccountIncomeDetails> incomeDetailsList) {
        return new AbstractConcurrentComplete() {
            @Override
            public void run() {
                for (ConcurrentResult concurrentResult : this.getResults()) {
                    if (concurrentResult != null) {
                        final List<SubAccountIncomeDetails> subAccountIncomeDetailsList = (List<SubAccountIncomeDetails>) concurrentResult.getResult();
                        if (CollectionUtils.isNotEmpty(subAccountIncomeDetailsList)) {
                            incomeDetailsList.addAll(subAccountIncomeDetailsList);
                        }
                    }
                }
            }
        };
    }

}
