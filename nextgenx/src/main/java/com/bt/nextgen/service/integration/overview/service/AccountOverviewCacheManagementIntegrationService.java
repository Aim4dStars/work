package com.bt.nextgen.service.integration.overview.service;


import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationType;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;

public interface AccountOverviewCacheManagementIntegrationService {
    /**
     * Remove cache entries with keys of type {@link AccountKey}.
     *
     * @param key Account key used to find cache entries.
     */
    void clearCache(AccountKey key);

    /**
     * Remove cache entries with keys of type {@link com.bt.nextgen.api.account.v3.model.AccountKey}.
     *
     * @param accountKey Account key used to find cache entry.
     */
    void clearCache(com.bt.nextgen.api.account.v3.model.AccountKey accountKey);

    /**
     * Remove categorisation cache entries with keys of type {@link AccountKey}.
     *
     * @param key      Account key used to find cache entries.
     * @param category Type of cash categorisation.
     */
    void clearCategorisationCache(AccountKey key, CashCategorisationType category);

    /**
     * Remove cash transations cache entries.
     *
     * @param identifier Cache key for cash transactions.
     */
    void clearCashTransactionCache(WrapAccountIdentifier identifier);
}
