package com.bt.nextgen.api.account.v2.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.account.v2.model.AccountDto;
import com.bt.nextgen.api.account.v2.util.AccountDtoConverter;
import com.bt.nextgen.api.account.v2.util.AccountFilterMatcher;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.broker.BrokerWrapper;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
@Service("AccountDtoServiceV2")
@SuppressWarnings("squid:S1200")
public class AccountDtoServiceImpl implements AccountDtoService
{
	@Autowired
	@Qualifier("avaloqAccountIntegrationService")
	private AccountIntegrationService accountService;

	@Autowired
	private ProductIntegrationService productIntegrationService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Autowired
    private BrokerHelperService brokerHelperService;

    @Override
    public List<AccountDto> findAll(ServiceErrors serviceErrors) {
        List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        Map<WrapAccount, Boolean> directMap = new HashMap<>();
        Map<WrapAccount, BrokerUser> accountBrokerMap = new HashMap<>();
        Map<AccountKey, WrapAccount> accountsMap = accountService.loadWrapAccountWithoutContainers(serviceErrors);
        createAccountAndBrokerMap(accountsMap, directMap, accountBrokerMap, serviceErrors);

        return AccountUtil.getAccountFilterUtil(accountsMap, accountService.loadAccountBalancesMap(serviceErrors),
                productIntegrationService.loadProductsMap(serviceErrors),
                brokerIntegrationService).search(criteriaList, directMap, accountBrokerMap);
    }

    @Override
    public List<AccountDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        Map<WrapAccount, Boolean> directMap = new HashMap<>();
        Map<WrapAccount, BrokerUser> accountBrokerMap = new HashMap<>();
        Map<AccountKey, WrapAccount> accountsMap = accountService.loadWrapAccountWithoutContainers(serviceErrors);
        createAccountAndBrokerMap(accountsMap, directMap, accountBrokerMap, serviceErrors);

        return AccountUtil.getAccountFilterUtil(accountsMap, accountService.loadAccountBalancesMap(serviceErrors),
                productIntegrationService.loadProductsMap(serviceErrors),
                brokerIntegrationService).search(criteriaList, directMap, accountBrokerMap);
    }

    @Override
    public List<AccountDto> getFilteredValue(String queryString, List<ApiSearchCriteria> filterCriteria,
            ServiceErrors serviceErrors) {
        Map<WrapAccount, Boolean> directMap = new HashMap<>();
        Map<WrapAccount, BrokerUser> accountBrokerMap = new HashMap<>();
        Map<AccountKey, WrapAccount> accountsMap = accountService.loadWrapAccountWithoutContainers(serviceErrors);
        createAccountAndBrokerMap(accountsMap, directMap, accountBrokerMap, serviceErrors);
        AccountDtoConverter accountDtoConverter = new AccountDtoConverter(accountsMap,
                productIntegrationService.loadProductsMap(serviceErrors), directMap, accountBrokerMap);
        Map<com.bt.nextgen.service.integration.account.AccountKey, AccountDto> accountDtoMap = accountDtoConverter
                .convert();
        List<AccountDto> accounts = new ArrayList<>();
        if (!filterCriteria.isEmpty()) {
            AccountFilterMatcher accountFilterMatcher = new AccountFilterMatcher(filterCriteria, accountDtoMap);
            accountDtoMap = accountFilterMatcher.filter();
        }

        if (AccountUtil.getAccountSearchUtil(queryString).isSearch()) {
            accounts = Lambda.filter(AccountUtil.getAccountSearchUtil(queryString), accountDtoMap.values());
        }
        return accounts;
    }
    
    private void createAccountAndBrokerMap(Map<AccountKey, WrapAccount> accountsMap, Map<WrapAccount, Boolean> directMap,
            Map<WrapAccount, BrokerUser> accountBrokerMap, ServiceErrors serviceErrors) {
			List<BrokerKey> listOfBrokerKeys = new ArrayList<>();
        for (WrapAccount wrapAccount : accountsMap.values()) {
			final BrokerKey brokerKey = BrokerKey.valueOf(wrapAccount.getAdviserPositionId().getId()); 
			listOfBrokerKeys.add(brokerKey);
        }
		Map<BrokerKey, BrokerWrapper>  advBrokerKeyBrokerWrapperMap = brokerIntegrationService.getAdviserBrokerUser(listOfBrokerKeys, serviceErrors);
		for (WrapAccount wrapAccount : accountsMap.values()) {
			BrokerKey brokerKey = BrokerKey.valueOf(wrapAccount.getAdviserPositionId().getId());
			final BrokerUser brokerUser = advBrokerKeyBrokerWrapperMap.get(brokerKey).getBrokerUser();
			directMap.put(wrapAccount, advBrokerKeyBrokerWrapperMap.get(brokerKey).isDirectInvestment());
			accountBrokerMap.put(wrapAccount, brokerUser);			
        }
		
    }
}
