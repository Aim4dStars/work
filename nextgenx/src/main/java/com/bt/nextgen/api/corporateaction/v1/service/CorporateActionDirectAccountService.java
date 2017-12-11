package com.bt.nextgen.api.corporateaction.v1.service;

import java.util.List;
import java.util.Map;

import ch.lambdaj.function.matcher.Predicate;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;

import static ch.lambdaj.Lambda.exists;
import static ch.lambdaj.Lambda.select;

@Component
public class CorporateActionDirectAccountService {
    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    private BrokerHelperService brokerHelperService;

    private Predicate<WrapAccount> directPredicate = new Predicate<WrapAccount>() {
        public boolean apply(WrapAccount account) {
            return brokerHelperService.isDirectInvestor(account, new ServiceErrorsImpl());
        }
    };

    public boolean isDirectAccount(String accountId) {
        Map<AccountKey, WrapAccount> accountMap = accountIntegrationService.loadWrapAccountWithoutContainers(new ServiceErrorsImpl());
        WrapAccount account = accountMap.get(AccountKey.valueOf(accountId));
        if (account != null)
            return directPredicate.apply(account);
        return false;
    }

    /**
     * Method check if the caller is investor and it has at least <b>one Direct Account</b>. If method is not called for
     * investor it will return true;
     *
     * @return true if investor has one direct account
     */
    public boolean hasDirectAccountWithUser() {
        return exists(accountIntegrationService.loadWrapAccountWithoutContainers(new ServiceErrorsImpl()), directPredicate);
    }

    /**
     * Returns all the direct account for an investor if caller is not investor it will return empty list
     *
     * @return true if investor has one direct account
     */
    public List<WrapAccount> getDirectAccounts() {
        return select(accountIntegrationService.loadWrapAccountWithoutContainers(new ServiceErrorsImpl()).values(), directPredicate);
    }

    public boolean hasDirectAccounts(final List<String> accounts) {
        return accounts.size() == select(select(accountIntegrationService.loadWrapAccountWithoutContainers(new ServiceErrorsImpl())
                .values(), new Predicate<WrapAccount>() {
            public boolean apply(WrapAccount account) {
                return accounts.contains(account.getAccountKey().getId());
            }
        }), directPredicate).size();
    }
}