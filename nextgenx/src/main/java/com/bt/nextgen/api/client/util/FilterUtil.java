package com.bt.nextgen.api.client.util;

import com.bt.nextgen.api.account.v1.model.AccountDto;
import com.bt.nextgen.api.client.model.ClientDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.integration.account.AccountBalance;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by L062329 on 25/02/2015.
 */
public class FilterUtil {

    private static final Logger logger = LoggerFactory.getLogger(FilterUtil.class);
    protected BrokerIntegrationService brokerIntegrationService;

    /**
     *
     * @param brokerIntegrationService
     */
    public FilterUtil(BrokerIntegrationService brokerIntegrationService) {
        this.brokerIntegrationService = brokerIntegrationService;
    }

    /**
     * Method filters the clients based on provided search criteria an converts Client domain object into dto objects
     *
     * @param criteriaList
     *            search criteria
     * @param clientMap
     *            return map of client dto indexed to client (domain) key
     * @return filtered map of client key and client dto
     */
    protected Map<ClientKey, ClientDto> getClientFilterDtoMap(List<ApiSearchCriteria> criteriaList,
            Map<ClientKey, Client> clientMap) {

        logger.debug("Conversion to Client DTO Map start...");
        ClientListDtoConverter clientListDtoConverter = new ClientListDtoConverter(clientMap.values());
        Map<ClientKey, ClientDto> clientDtoMap = clientListDtoConverter.convert();
        logger.debug("Converted Client List to DTO Map..Now Checking if Filter needs to be applied");
        List<ApiSearchCriteria> clientFilters = clientFilterCriteria(criteriaList);
        if (!clientFilters.isEmpty()) {
            ClientFilterMatcher clientFilter = new ClientFilterMatcher(clientFilterCriteria(criteriaList), clientDtoMap);
            Map<ClientKey, ClientDto> map = clientFilter.filter();
            logger.debug("Conversion to Client DTO Map ends when client filters are not empty");
            return map;

        } else {
            logger.debug("Conversion to Client DTO Map ends when client filters are empty");
            return clientDtoMap;
        }
    }

    /**
     * Method filters accounts based on provided search criteria an converts Account domain object into dto objects
     * 
     * @param criteriaList
     *            search criteria
     * @param serviceErrors
     *            service error
     * @param accountMap
     *            map of domain object indexed against account (domain) key
     * @param accountBalanceMap
     *            map of account balance domain objects indexed against account (domain) key
     * @param productMap
     *            map of product domain object indexed Product (domain) Key
     * @return filtered map of account key and account dtos
     */
    protected Map<AccountKey, AccountDto> getAccountFilterDtoMap(List<ApiSearchCriteria> criteriaList,
            ServiceErrors serviceErrors, Map<AccountKey, WrapAccount> accountMap,
            Map<AccountKey, AccountBalance> accountBalanceMap, Map<ProductKey, Product> productMap) {
        logger.debug("Conversion to Account DTO Map starts");
        AccountDtoConverter accountDtoConverter = new AccountDtoConverter(accountMap, accountBalanceMap, productMap,
                brokerIntegrationService);
        Map<AccountKey, AccountDto> accountDtoMap = accountDtoConverter.convert(serviceErrors);
        logger.debug("Converted to  Account DTO Map ..Now Checking if Filter needs to be applied");
        List<ApiSearchCriteria> accountFilters = accountFilterCriteria(criteriaList);
        if (!accountFilters.isEmpty()) {
            AccountFilterMatcher accountFilterMatcher = new AccountFilterMatcher(accountFilterCriteria(criteriaList),
                    accountDtoMap);
            Map<AccountKey, AccountDto> map = accountFilterMatcher.filter();
            logger.debug("Filters Applied on Account DTO Map");
            return map;
        } else {
            logger.debug("No Filters Applied on Account DTO Map");
            return accountDtoMap;
        }
    }

    /**
     * Method returns search criteria for clients (important* it does not includes display name filter)
     * 
     * @param criteriaList
     *            list of all filter criterias
     * @return client filter criterias
     */
    public List<ApiSearchCriteria> clientFilterCriteria(List<ApiSearchCriteria> criteriaList) {
        List<ApiSearchCriteria> clientCriteria = new ArrayList<>();
        if (criteriaList != null && !criteriaList.isEmpty()) {
            for (ApiSearchCriteria criteria : criteriaList) {
                if (Constants.STATE.equalsIgnoreCase(criteria.getProperty())
                        || Constants.COUNTRY.equalsIgnoreCase(criteria.getProperty())
                        || Constants.CLIENT_DISPLAY_NAME.equalsIgnoreCase(criteria.getProperty())
                        || Constants.REGISTERED_ONLINE.equalsIgnoreCase(criteria.getProperty())) {
                    clientCriteria.add(criteria);
                }
            }
        }
        return clientCriteria;
    }

    /**
     * Method returns search criteria for accounts (important* it does not includes display name filter)
     * 
     * @param criteriaList
     *            list of all filter criterias
     * @return account filter criterias
     */
    public List<ApiSearchCriteria> accountFilterCriteria(List<ApiSearchCriteria> criteriaList) {
        List<ApiSearchCriteria> accountCriteria = new ArrayList<>();
        if (criteriaList != null && !criteriaList.isEmpty()) {
            for (ApiSearchCriteria criteria : criteriaList) {
                if (!Constants.STATE.equalsIgnoreCase(criteria.getProperty())
                        && !Constants.COUNTRY.equalsIgnoreCase(criteria.getProperty())
                        && !Constants.CLIENT_DISPLAY_NAME.equalsIgnoreCase(criteria.getProperty())
                        && !Constants.REGISTERED_ONLINE.equalsIgnoreCase(criteria.getProperty())) {
                    accountFilterCriteria(accountCriteria, criteria);
                }
            }
        }
        return accountCriteria;
    }

    private void accountFilterCriteria(List<ApiSearchCriteria> accountCriteria, ApiSearchCriteria criteria) {
        if (!Constants.OWNER.equalsIgnoreCase(criteria.getProperty())
                && !Constants.APPROVER.equalsIgnoreCase(criteria.getProperty())) {
            accountCriteria.add(criteria);
        }
    }
}
