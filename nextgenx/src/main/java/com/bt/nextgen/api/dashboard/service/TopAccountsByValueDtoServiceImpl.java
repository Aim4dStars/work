package com.bt.nextgen.api.dashboard.service;

import com.bt.nextgen.api.dashboard.model.TopAccountDto;
import com.bt.nextgen.api.dashboard.model.TopAccountsByValueDto;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.dashboard.TopAccountsByValueImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.dashboard.AdviserPerformanceIntegrationService;
import com.btfin.panorama.core.concurrent.AbstractConcurrentComplete;
import com.btfin.panorama.core.concurrent.Concurrent;
import com.btfin.panorama.core.concurrent.ConcurrentCallable;
import com.btfin.panorama.core.concurrent.ConcurrentComplete;
import com.btfin.panorama.core.concurrent.ConcurrentResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TopAccountsByValueDtoServiceImpl implements TopAccountsByValueService {
    @Autowired
    private AdviserPerformanceIntegrationService adviserService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Override
    public TopAccountsByValueDto findOne(ServiceErrors serviceErrors) {
        // Get the ID of logged in adviser
        String brokerId = userProfileService.getPositionId();
        BrokerKey brokerKey = BrokerKey.valueOf(brokerId);

        // Get the list of accounts under this adviser
        Map<AccountKey, WrapAccount> allAccounts = accountService.loadWrapAccountWithoutContainers(serviceErrors);

        List<TopAccountsByValueImpl> topAccountsByCash = new ArrayList<>();
        List<TopAccountsByValueImpl> topAccountsByPortfolio = new ArrayList<>();
        Concurrent.when(loadTopAccountsByCash(brokerKey, serviceErrors), loadTopAccountsByPortfolio(brokerKey, serviceErrors))
                .done(processResults(topAccountsByCash, topAccountsByPortfolio)).execute();

        List<TopAccountDto> topByCashDtoList = convertToDto(allAccounts, topAccountsByCash, serviceErrors);
        List<TopAccountDto> topByPortfolioDtoList = convertToDto(allAccounts, topAccountsByPortfolio, serviceErrors);

        TopAccountsByValueDto topAccounts = new TopAccountsByValueDto(topByCashDtoList, topByPortfolioDtoList);

        return topAccounts;
    }

    protected ConcurrentCallable<List<TopAccountsByValueImpl>> loadTopAccountsByCash(final BrokerKey brokerKey,
            final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<List<TopAccountsByValueImpl>>() {
            @Override
            public List<TopAccountsByValueImpl> call() {
                return adviserService.loadTopAccountsByCash(brokerKey, serviceErrors);
            }
        };
    }

    protected ConcurrentCallable<List<TopAccountsByValueImpl>> loadTopAccountsByPortfolio(final BrokerKey brokerKey,
            final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<List<TopAccountsByValueImpl>>() {
            @Override
            public List<TopAccountsByValueImpl> call() {
                return adviserService.loadTopAccountsByPortfolio(brokerKey, serviceErrors);
            }
        };
    }

    private ConcurrentComplete processResults(final List<TopAccountsByValueImpl> topAccountsByCash,
            final List<TopAccountsByValueImpl> topAccountsByPortfolio) {
        return new AbstractConcurrentComplete() {
            @Override
            public void run() {
                List<? extends ConcurrentResult<?>> r = this.getResults();

                topAccountsByCash.addAll((List<TopAccountsByValueImpl>) r.get(0).getResult());
                topAccountsByPortfolio.addAll((List<TopAccountsByValueImpl>) r.get(1).getResult());
            }
        };
    }

    protected List<TopAccountDto> convertToDto(Map<AccountKey, WrapAccount> accountMap,
            List<TopAccountsByValueImpl> topAccountsByValue, ServiceErrors serviceErrors) {
        // Create top ten DTO list from given list
        List<TopAccountDto> topAccountsList = new ArrayList<TopAccountDto>();

        for (TopAccountsByValueImpl account : topAccountsByValue) {
            if (account.getAccountId() != null) {
                AccountKey accountKey = AccountKey.valueOf(account.getAccountId());

                // Retrieve account name from list of available accounts
                String accountName = "";
                if (accountMap.get(accountKey) != null) {
                    accountName = accountMap.get(accountKey).getAccountName();
                }

                EncodedString encodedAccountId = EncodedString.fromPlainText(accountKey.getId());
                AccountKey encodedAccountKey = AccountKey.valueOf(encodedAccountId.toString());

                TopAccountDto temp = new TopAccountDto(encodedAccountKey, accountName, account.getCashValue(),
                        account.getPortfolioValue());

                topAccountsList.add(temp);
            }
        }

        return topAccountsList;
    }
}
