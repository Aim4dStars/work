package com.bt.nextgen.api.account.v3.util;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.service.integration.account.AccountBalance;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductKey;

import java.util.Map;

public final class AccountUtil {

    private AccountUtil() {}

    public static AccountFilterUtil getAccountFilterUtil(Map<AccountKey, WrapAccount> accounts,
                                                         Map<AccountKey, AccountBalance> accountBalances,
                                                         Map<ProductKey, Product> products,
                                                         BrokerIntegrationService brokerIntegrationService) {
        return new AccountFilterUtil(accounts, accountBalances, products, brokerIntegrationService);
    }

    public static AccountFilterUtil getAccountFilterUtil(Map<AccountKey, AccountDto> accounts) {
        return new AccountFilterUtil(accounts);
    }

    public static AccountSearchUtil getAccountSearchUtil(String queryString) {
        return new AccountSearchUtil(queryString);
    }
}
