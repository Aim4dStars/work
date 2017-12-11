package com.bt.nextgen.api.corporateaction.v1.service;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountBalance;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetPrice;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.financialmarketinstrument.FinancialMarketInstrumentIntegrationService;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.core.concurrent.AbstractConcurrentComplete;
import com.btfin.panorama.core.concurrent.Concurrent;
import com.btfin.panorama.core.concurrent.ConcurrentCallable;
import com.btfin.panorama.core.concurrent.ConcurrentComplete;
import com.btfin.panorama.core.concurrent.ConcurrentResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class CorporateActionCommonServiceImpl implements CorporateActionCommonService {

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Autowired
    @Qualifier("avaloqClientIntegrationService")
    private ClientIntegrationService clientService;

    @Autowired
    private FinancialMarketInstrumentIntegrationService fmiService;

    @Autowired
    private UserProfileService userProfileService;

    @Override
    public CorporateActionClientAccountDetails loadClientAccountDetails(CorporateActionContext context,
                                                                        List<CorporateActionAccount> accounts,
                                                                        ServiceErrors serviceErrors) {
        if (context.isDealerGroupOrInvestmentManager()) {
            // Load concurrently, skip clients map due to appalling performance
            List<CorporateActionClientAccountDetails> result = new ArrayList<>();
            Concurrent.when(loadWrapAccounts(serviceErrors), loadAccountBalancesMap(accounts, serviceErrors))
                      .done(processResults(result, serviceErrors)).execute();
            return result.get(0);
        } else {
            // Get the available balances map
            List<AccountKey> accountKeys = getAccountKeys(accounts);
            Map<AccountKey, AccountBalance> accountBalancesMap = accountService
                    .loadAccountBalancesMap(accountKeys, serviceErrors);

            return new CorporateActionClientAccountDetails(null, null, accountBalancesMap);
        }
    }

    private List<AccountKey> getAccountKeys(List<CorporateActionAccount> accounts) {
        List<AccountKey> accountKeys = new ArrayList<>();
        if (accounts != null) {
            for (CorporateActionAccount account : accounts) {
                accountKeys.add(AccountKey.valueOf(account.getAccountId()));
            }
        }
        return accountKeys;
    }

    private ConcurrentComplete processResults(final List<CorporateActionClientAccountDetails> results,
                                              final ServiceErrors serviceErrors) {

        return new AbstractConcurrentComplete() {

            @Override
            public void run() {
                List<? extends ConcurrentResult<?>> r = this.getResults();
                Map<AccountKey, WrapAccount> accountsMap = (Map<AccountKey, WrapAccount>) r.get(0).getResult();
                Map<AccountKey, AccountBalance> accountBalancesMap = (Map<AccountKey, AccountBalance>) r.get(1).getResult();

                results.add(new CorporateActionClientAccountDetails(accountsMap, null, accountBalancesMap));
            }
        };
    }

    private ConcurrentCallable<Map<AccountKey, WrapAccount>> loadWrapAccounts(final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<Map<AccountKey, WrapAccount>>() {

            @Override
            public Map<AccountKey, WrapAccount> call() {
                return accountService.loadWrapAccountWithoutContainers(serviceErrors);
            }
        };
    }

    private ConcurrentCallable<Map<AccountKey, AccountBalance>> loadAccountBalancesMap(
            final List<CorporateActionAccount> accounts, final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<Map<AccountKey, AccountBalance>>() {

            @Override
            public Map<AccountKey, AccountBalance> call() {
                List<AccountKey> accountKeys = getAccountKeys(accounts);
                return accountService.loadAccountBalancesMap(accountKeys, serviceErrors);
            }
        };
    }

    @Override
    public BigDecimal getAssetPrice(Asset asset, ServiceErrors serviceErrors) {
        AssetPrice assetPrice = fmiService.loadAssetPrice(userProfileService.getUserId(), asset, false, true);

        return new BigDecimal(assetPrice.getLastPrice());
    }

    @Override
    public UserProfileService getUserProfileService() {
        return userProfileService;
    }
}
