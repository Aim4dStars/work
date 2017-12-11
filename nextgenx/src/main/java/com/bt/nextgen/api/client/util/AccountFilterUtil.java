package com.bt.nextgen.api.client.util;

import com.bt.nextgen.api.account.v1.model.AccountDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountBalance;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by L062329 on 25/02/2015.
 */
public class AccountFilterUtil extends FilterUtil {
    private Map<AccountKey, WrapAccount> accounts;
    private Map<AccountKey, AccountBalance> accountBalances;
    private Map<ProductKey, Product> products;

    /**
     * @param accounts
     * @param accountBalances
     * @param products
     * @param brokerIntegrationService
     */
    public AccountFilterUtil( Map<AccountKey, WrapAccount> accounts,
                             Map<AccountKey, AccountBalance> accountBalances, Map<ProductKey, Product> products,
                             BrokerIntegrationService brokerIntegrationService) {
        super(brokerIntegrationService);
        this.accounts = accounts;
        this.accountBalances = accountBalances;
        this.products = products;

    }

    public List<AccountDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        Map<AccountKey, AccountDto> filteredAccounts = getAccountFilterDtoMap(criteriaList, serviceErrors, accounts,
                accountBalances, products);
        return mapAccountClients(filteredAccounts);

    }

    private List<AccountDto> mapAccountClients(Map<AccountKey, AccountDto> filteredAccounts) {
        List<AccountDto> accountDtos = new ArrayList<>();
        for (Map.Entry<AccountKey, WrapAccount> entry : accounts.entrySet()) {
            WrapAccount wrapAccount = entry.getValue();
            AccountKey accountKey = wrapAccount.getAccountKey();
            AccountDto accountDto = filteredAccounts.get(accountKey);
            if (accountDto != null) {
            accountDtos.add(accountDto);
        }

        }
        return accountDtos;
    }


}
