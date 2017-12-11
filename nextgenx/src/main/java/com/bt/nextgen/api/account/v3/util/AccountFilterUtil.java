package com.bt.nextgen.api.account.v3.util;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.integration.account.AccountBalance;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The Class AccountFilterUtil.
 */
public class AccountFilterUtil extends FilterUtil {

    /** The accounts. */
    private Map<AccountKey, WrapAccount> accounts;

    private Map<AccountKey, AccountDto> accountDtoMap;
    private Map<AccountKey, AccountBalance> accountBalances;

    /** The products. */
    private Map<ProductKey, Product> products;

    public AccountFilterUtil(Map<AccountKey, AccountDto> accountDtoMap) {
        super(null);
        this.accountDtoMap = accountDtoMap;

    }

    /**
     * Instantiates a new account filter util.
     *
     * @param accounts
     *            the accounts
     * @param products
     *            the products
     * @param brokerIntegrationService
     *            the broker integration service
     */
    public AccountFilterUtil(Map<AccountKey, WrapAccount> accounts,  Map<AccountKey, AccountBalance> accountBalances,
                             Map<ProductKey, Product> products, BrokerIntegrationService brokerIntegrationService) {
        super(brokerIntegrationService);
        this.accounts = accounts;
        this.products = products;
        this.accountBalances = accountBalances;
    }

    /**
     * Search.
     *
     * @param criteriaList
     *            the criteria list
     * @param accountBrokerMap
     * @param directMap
     *            the service errors
     * @return the list
     */
    public List<AccountDto> search(List<ApiSearchCriteria> criteriaList, Map<WrapAccount, Boolean> directMap,
            Map<WrapAccount, BrokerUser> accountBrokerMap) {
        final Map<AccountKey, AccountDto> filteredAccounts = getAccountFilterDtoMap(criteriaList, accounts, accountBalances,
                 products, directMap, accountBrokerMap);
        return mapAccountClients(filteredAccounts);

    }

    public List<AccountDto> search(List<ApiSearchCriteria> criteriaList) {
        final Map<AccountKey, AccountDto> filteredAccounts = getAccountFilterDtoMap(criteriaList, accountDtoMap);
        return new ArrayList<>(filteredAccounts.values());

    }

    /**
     * Map account clients.
     *
     * @param filteredAccounts
     *            the filtered accounts
     * @return the list
     */
    private List<AccountDto> mapAccountClients(Map<AccountKey, AccountDto> filteredAccounts) {
        final List<AccountDto> accountDtos = new ArrayList<>();
        for (Map.Entry<AccountKey, WrapAccount> entry : accounts.entrySet()) {
            final WrapAccount wrapAccount = entry.getValue();
            final AccountKey accountKey = wrapAccount.getAccountKey();
            final AccountDto accountDto = filteredAccounts.get(accountKey);
            if (accountDto != null) {
                accountDtos.add(accountDto);
            }
        }
        return accountDtos;
    }
}
