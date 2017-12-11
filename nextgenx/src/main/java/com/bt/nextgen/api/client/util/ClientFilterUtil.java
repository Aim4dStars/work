package com.bt.nextgen.api.client.util;

import com.bt.nextgen.api.account.v1.model.AccountDto;

import com.bt.nextgen.api.client.model.ClientDto;
import com.bt.nextgen.api.client.model.ClientIdentificationDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.integration.account.AccountBalance;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.btfin.panorama.core.security.integration.domain.Individual;
import com.btfin.panorama.core.security.integration.domain.Investor;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ClientFilterUtil utility class, gets the clients, accounts, balance and product detail from Integration service,
 * applies filter on provided criteria.
 */
public class ClientFilterUtil extends FilterUtil {
    @Qualifier("avaloqClientIntegrationService")
    private ClientIntegrationService clientIntegrationService;

    private AccountIntegrationService accountService;

    private ProductIntegrationService productIntegrationService;

    private static final Logger logger = LoggerFactory.getLogger(ClientFilterUtil.class);

    public ClientFilterUtil(ClientIntegrationService clientIntegrationService,
                            AccountIntegrationService accountService,
                            ProductIntegrationService productIntegrationService,
                            BrokerIntegrationService brokerIntegrationService) {
        super(brokerIntegrationService);
        this.clientIntegrationService = clientIntegrationService;
        this.accountService = accountService;
        this.productIntegrationService = productIntegrationService;
    }

    /**
     * Search method gets clients their accounts and account balances converts it in dto applies filter on it and return the filtered result.
     *
     * @param criteriaList
     * @param serviceErrors
     * @return List of client DTOs filtered by the criteria
     */
    public List<ClientIdentificationDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        Map<ProductKey, Product> productMap = productIntegrationService.loadProductsMap(serviceErrors);
        Map<AccountKey, AccountBalance> accountBalanceMap = accountService.loadAccountBalancesMap(serviceErrors);
        Map<ClientKey, Client> clientMap = clientIntegrationService.loadClientMap(serviceErrors);
        Map<ClientKey, ClientDto> clientDtoFilteredMap = getClientFilterDtoMap(criteriaList, clientMap);
        Map<AccountKey, WrapAccount> accountMap = accountService.loadWrapAccountWithoutContainers(serviceErrors);
        Map<AccountKey, AccountDto> accountDtoFilteredMap = getAccountFilterDtoMap(criteriaList, serviceErrors,
                accountMap, accountBalanceMap, productMap);
       Map<ClientKey, List<AccountDto>> clientAccountMap = mapClientAccounts(commonFilterCriteria(criteriaList),
                clientMap, accountMap, clientDtoFilteredMap, accountDtoFilteredMap);

        List<ClientIdentificationDto> clientIdentificationDtoList= getClientDtos(clientDtoFilteredMap, clientAccountMap);
        logger.debug("Getting Client IdentificationDto ...");
        return clientIdentificationDtoList;
    }

    public List<ClientIdentificationDto> getFilteredValue(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        Map<ProductKey, Product> productMap = productIntegrationService.loadProductsMap(serviceErrors);
        Map<ClientKey, Client> clientMap = clientIntegrationService.loadClientMap(serviceErrors);
        Map<ClientKey, ClientDto> clientDtoFilteredMap = getClientFilterDtoMap(criteriaList, clientMap);
        Map<AccountKey, WrapAccount> accountMap = accountService.loadWrapAccountWithoutContainers(serviceErrors);
        Map<AccountKey, AccountDto> accountDtoFilteredMap = getAccountFilterDtoMap(criteriaList, serviceErrors,
                accountMap, null, productMap);
        Map<ClientKey, List<AccountDto>> clientAccountMap = mapClientAccounts(commonFilterCriteria(criteriaList),
                                                                                clientMap, accountMap,
                                                                                clientDtoFilteredMap, accountDtoFilteredMap);
        return getClientDtos(clientDtoFilteredMap, clientAccountMap);
    }


    /**
     * Method gets clients and their account and merge it and sends back
     * Important : This method does not sets the balances(available cache and portfolio value) for account and hence be
     * careful while using if account balance required please {@link #search(java.util.List, com.bt.nextgen.service.ServiceErrors)} ()}
     *
     * @param serviceErrors
     * @return List of client DTOs
     */
    public List<ClientIdentificationDto> findAll(ServiceErrors serviceErrors) {
        Map<ProductKey, Product> productMap = productIntegrationService.loadProductsMap(serviceErrors);
        Map<ClientKey, Client> clientMap = clientIntegrationService.loadClientMap(serviceErrors);
        logger.debug("Getting Client Filter Map Starts...");
        Map<ClientKey, ClientDto> clientDtoFilteredMap = getClientFilterDtoMap(null, clientMap);
        logger.debug("Getting Client Filter Map Ends...");

        Map<AccountKey, WrapAccount> accountMap = accountService.loadWrapAccountWithoutContainers(serviceErrors);
        logger.debug("Account Map Retrieved ...Start conversion to AccountFilterDtoMap");
        Map<AccountKey, AccountDto> accountDtoFilteredMap = getAccountFilterDtoMap(null, serviceErrors, accountMap, null, productMap);
        logger.debug("Conversion to AccountFilterDtoMap Ends");
        Map<ClientKey, List<AccountDto>> clientAccountMap = mapClientAccounts(null, clientMap, accountMap, clientDtoFilteredMap, accountDtoFilteredMap);
        logger.debug("Mapped All Accounts to the clients");
        List<ClientIdentificationDto> clientIdentificationDtoList= getClientDtos(clientDtoFilteredMap, clientAccountMap);
        logger.debug("Converted to ClientIdentificationDto list ..End of FindAll Method");
        return clientIdentificationDtoList;
    }

    /**
     * This method iterates over filtered account dto map and associates it with owners and associated person of owners
     *
     * @param criterias             Common Filter Criterias
     * @param clientMap             Client map
     * @param accountMap            account map
     * @param clientDtoFilteredMap  filtered client(domain) key, client dto map
     * @param accountDtoFilteredMap filtered account (domain) key, account dto map
     * @return map of client key and list of account associated with it from the filtered list
     */
    private Map<ClientKey, List<AccountDto>> mapClientAccounts(List<ApiSearchCriteria> criterias,
                                                               Map<ClientKey, Client> clientMap,
                                                               Map<AccountKey, WrapAccount> accountMap,
                                                               Map<ClientKey, ClientDto> clientDtoFilteredMap,
                                                               Map<AccountKey, AccountDto> accountDtoFilteredMap) {
        Map<ClientKey, List<AccountDto>> clientAccountMap = new HashMap<>();
        //map only the filtered accounts
        Set<AccountKey> accountKeys = accountDtoFilteredMap.keySet();
        for (AccountKey accountKey : accountKeys) {
            AccountDto accountDto = accountDtoFilteredMap.get(accountKey);
            //get the Wrap account object to find out owner list of account
            WrapAccount wrapAccount = accountMap.get(accountKey);
            Set<ClientKey> filteredClientsForAccount = filterClients(criterias, clientMap, wrapAccount);
            for (ClientKey clientKey : filteredClientsForAccount) {
                addClientAccounts(clientDtoFilteredMap, clientAccountMap, accountDto, clientKey);
            }
        }
        return clientAccountMap;
    }

    private Set<ClientKey> getAssociatedPersonAccount(WrapAccount wrapAccount, Map<ClientKey, Client> clientMap) {
        Set<ClientKey> associatedPersonKeySet = new HashSet<>();
        Collection<ClientKey> clientKeys = wrapAccount.getAccountOwners();
        for (ClientKey clientKey : clientKeys) {
            associatedPersonKeySet.add(ClientKey.valueOf(clientKey.getId()));
            //go to original domain client map to find out associated clients
            Client client = clientMap.get(ClientKey.valueOf(clientKey.getId()));
            //map account dto to associated clients
            List<ClientKey> associatedPersonKeys = ((Investor) client).getAssociatedPersonKeys();
            if (associatedPersonKeys != null) {
                associatedPersonKeySet.addAll(associatedPersonKeys);
            }
        }
        return associatedPersonKeySet;
    }

    public Set<ClientKey> filterClients(List<ApiSearchCriteria> criterias, Map<ClientKey, Client> clientMap, WrapAccount wrapAccount) {
        Set<ClientKey> filteredClients = new HashSet<>();
        if (criterias != null && !criterias.isEmpty()) {
            for (ApiSearchCriteria criteria : criterias) {
                String property = criteria.getProperty();
                switch (property) {
                    case Constants.OWNER:
                        filteredClients.addAll(wrapAccount.getAccountOwners());
                        break;
                    case Constants.APPROVER:
                        filteredClients.addAll(getApprovers(clientMap, wrapAccount));
                        break;
                    default:
                        filteredClients = getAssociatedPersonAccount(wrapAccount, clientMap);
                        break;
                }
            }
        } else {
            filteredClients = getAssociatedPersonAccount(wrapAccount, clientMap);
        }
        return filteredClients;
    }

    private Set<ClientKey> getApprovers(Map<ClientKey, Client> clientMap, WrapAccount wrapAccount){
        Set<ClientKey> approverSet = new HashSet<>();
        approverSet.addAll(wrapAccount.getApprovers());
        for (ClientKey clientKey : wrapAccount.getAccountOwners()){
            Client client = clientMap.get(clientKey);
            List<ClientKey> associatedPersonKeys = ((Investor) client).getAssociatedPersonKeys();
            if (associatedPersonKeys != null) {
                for (ClientKey associatedPersonKey : associatedPersonKeys){
                    Client associatedClient = clientMap.get(associatedPersonKey);
                    if (associatedClient instanceof Individual) {
                        Individual associatedIndividual = (Individual)associatedClient;
                        if (!wrapAccount.getApprovers().contains(associatedPersonKey) && associatedIndividual.isRegistrationOnline()) {
                            approverSet.add(associatedPersonKey);
                        }
                    }
                }
            }
        }
        return approverSet;
    }

    /**
     * Supporting method to add account to client's account list if client is not already filtered already by the search criteria
     *
     * @param clientDtoFilteredMap filtered client(domain) key, client dto map
     * @param clientAccountsMap    resulting map for client key and account list
     * @param accountDto           account dto to add int the clientAccountsMap
     * @param clientKey            client (domain) key
     */
    private void addClientAccounts(Map<ClientKey, ClientDto> clientDtoFilteredMap, Map<ClientKey, List<AccountDto>> clientAccountsMap, AccountDto accountDto, ClientKey clientKey) {
        ClientDto clientDto = clientDtoFilteredMap.get(clientKey);
        if (clientDto != null) {
            List<AccountDto> accountDtoList = clientAccountsMap.get(clientKey);
            if (accountDtoList == null) {
                accountDtoList = new ArrayList<>();
                clientAccountsMap.put(clientKey, accountDtoList);
            }
            accountDtoList.add(accountDto);
            setAggregatedBalance(accountDto, clientDto);
        }
    }

    /**
     * Method added to set the aggregated balance to client level
     *
     * @param accountDto
     * @param clientDto
     */
    private void setAggregatedBalance(AccountDto accountDto, ClientDto clientDto) {
        BigDecimal availableCache = new BigDecimal(0);
        availableCache = availableCache.add(clientDto.getAvailableCash() != null ? clientDto.getAvailableCash() : new BigDecimal(0));
        availableCache = availableCache.add(accountDto.getAvailableCash() != null ? accountDto.getAvailableCash() : new BigDecimal(0));
        clientDto.setAvailableCash(availableCache);
        BigDecimal portfolioValue = new BigDecimal(0);
        portfolioValue = portfolioValue.add(clientDto.getPortfolioValue() != null ? clientDto.getPortfolioValue() : new BigDecimal(0));
        portfolioValue = portfolioValue.add(accountDto.getPortfolioValue() != null ? accountDto.getPortfolioValue() : new BigDecimal(0));
        clientDto.setPortfolioValue(portfolioValue);
    }

    /**
     * Method assigns accounts to client
     *
     * @param clientDtoFilteredMap account dto map indexed against Client (domain) key
     * @param clientAccountMap     list of account dtos indexed against Client (domain) key
     * @return list of client dtos
     */
    protected List<ClientIdentificationDto> getClientDtos(Map<ClientKey, ClientDto> clientDtoFilteredMap,
                                                          Map<ClientKey, List<AccountDto>> clientAccountMap) {
        List<ClientIdentificationDto> finalClientList = new ArrayList<>();
        Set<ClientKey> clientKeys = clientAccountMap.keySet();
        for (ClientKey clientKey : clientKeys) {
            ClientDto clientDto = clientDtoFilteredMap.get(clientKey);
            clientDto.setAccounts(clientAccountMap.get(clientKey));
            finalClientList.add(clientDto);
        }
        return finalClientList;
    }

    /**
     * Method returns search criteria for clients and accounts
     *
     * @param criteriaList list of all filter criterias
     * @return client filter criterias
     */
    public List<ApiSearchCriteria> commonFilterCriteria(List<ApiSearchCriteria> criteriaList) {
        List<ApiSearchCriteria> clientCriteria = new ArrayList<>();
        if (criteriaList != null && !criteriaList.isEmpty()) {
            for (ApiSearchCriteria criteria : criteriaList) {
                if (Constants.OWNER.equalsIgnoreCase(criteria.getProperty())
                        || Constants.APPROVER.equalsIgnoreCase(criteria.getProperty())) {
                    clientCriteria.add(criteria);
                }
            }
        }
        return clientCriteria;
    }
}
