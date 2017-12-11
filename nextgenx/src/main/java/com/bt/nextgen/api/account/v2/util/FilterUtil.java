package com.bt.nextgen.api.account.v2.util;

import com.bt.nextgen.api.account.v2.model.AccountDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.avaloq.Constants;
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
 * The Class FilterUtil.
 */
@Deprecated
public class FilterUtil {

    /** The broker integration service. */
    protected BrokerIntegrationService brokerIntegrationService;

    /**
     * Instantiates a new filter util.
     *
     * @param brokerIntegrationService
     *            the broker integration service
     */
    public FilterUtil(BrokerIntegrationService brokerIntegrationService) {
        this.brokerIntegrationService = brokerIntegrationService;
    }

    /**
     * Method filters accounts based on provided search criteria an converts
     * Account domain object into dto objects.
     * 
     * @param criteriaList
     *            search criteria
     * @param serviceErrors
     *            service error
     * @param accountMap
     *            map of domain object indexed against account (domain) key
     * @param accountBalanceMap
     *            map of account balance domain objects indexed against account
     *            (domain) key
     * @param productMap
     *            map of product domain object indexed Product (domain) Key
     * @param accountBrokerMap
     * @param directMap
     * @return filtered map of account key and account dtos
     */
    protected Map<AccountKey, AccountDto> getAccountFilterDtoMap(List<ApiSearchCriteria> criteriaList,
            Map<AccountKey, WrapAccount> accountMap, Map<AccountKey, AccountBalance> accountBalanceMap,
            Map<ProductKey, Product> productMap, Map<WrapAccount, Boolean> directMap,
            Map<WrapAccount, BrokerUser> accountBrokerMap) {
        final AccountDtoConverter accountDtoConverter = new AccountDtoConverter(accountMap, accountBalanceMap, productMap,
                directMap, accountBrokerMap);
        final Map<AccountKey, AccountDto> accountDtoMap = accountDtoConverter.convert();
        final List<ApiSearchCriteria> accountFilters = accountFilterCriteria(criteriaList);
        if (!accountFilters.isEmpty()) {
            final AccountFilterMatcher accountFilterMatcher = new AccountFilterMatcher(accountFilterCriteria(criteriaList),
                    accountDtoMap);
            return accountFilterMatcher.filter();
        } else {
            return accountDtoMap;
        }
    }

    /**
     * Method returns search criteria for clients (important* it does not
     * includes display name filter).
     *
     * @param criteriaList
     *            list of all filter criterias
     * @return client filter criterias
     */
    public List<ApiSearchCriteria> clientFilterCriteria(List<ApiSearchCriteria> criteriaList) {
        final List<ApiSearchCriteria> clientCriteria = new ArrayList<>();
        if (criteriaList != null && !criteriaList.isEmpty()) {
            for (ApiSearchCriteria criteria : criteriaList) {
                if (Constants.STATE.equalsIgnoreCase(criteria.getProperty())
                        || Constants.COUNTRY.equalsIgnoreCase(criteria.getProperty())
                        || Constants.CLIENT_DISPLAY_NAME.equalsIgnoreCase(criteria.getProperty())) {
                    clientCriteria.add(criteria);
                }
            }
        }
        return clientCriteria;
    }

    /**
     * Method returns search criteria for accounts (important* it does not
     * includes display name filter).
     *
     * @param criteriaList
     *            list of all filter criterias
     * @return account filter criterias
     */
    public List<ApiSearchCriteria> accountFilterCriteria(List<ApiSearchCriteria> criteriaList) {
        final List<ApiSearchCriteria> accountCriteria = new ArrayList<>();
        if (criteriaList != null && !criteriaList.isEmpty()) {
            for (ApiSearchCriteria criteria : criteriaList) {
                if (!Constants.STATE.equalsIgnoreCase(criteria.getProperty())
                        && !Constants.COUNTRY.equalsIgnoreCase(criteria.getProperty())
                        && !Constants.CLIENT_DISPLAY_NAME.equalsIgnoreCase(criteria.getProperty())) {
                    accountFilterCriteria(accountCriteria, criteria);
                }
            }
        }
        return accountCriteria;
    }

    /**
     * Account filter criteria.
     *
     * @param accountCriteria
     *            the account criteria
     * @param criteria
     *            the criteria
     */
    private void accountFilterCriteria(List<ApiSearchCriteria> accountCriteria, ApiSearchCriteria criteria) {
        if (!Constants.OWNER.equalsIgnoreCase(criteria.getProperty())
                && !Constants.APPROVER.equalsIgnoreCase(criteria.getProperty())) {
            accountCriteria.add(criteria);
        }
    }
}
