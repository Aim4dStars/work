package com.bt.nextgen.api.account.v3.service;

import ch.lambdaj.Lambda;
import ch.lambdaj.collection.LambdaCollections;
import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.api.account.v3.util.AccountDtoConverter;
import com.bt.nextgen.api.account.v3.util.AccountFilterMatcher;
import com.bt.nextgen.api.account.v3.util.AccountUtil;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountBalance;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.broker.BrokerWrapper;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("AccountDtoServiceV3")
public class AccountDtoServiceImpl implements AccountDtoService {
	@Autowired
	@Qualifier("avaloqAccountIntegrationService")
	private AccountIntegrationService accountService;

	@Autowired
	private ProductIntegrationService productIntegrationService;

	@Autowired
	private BrokerIntegrationService brokerIntegrationService;

	private static Logger LOGGER = LoggerFactory.getLogger(AccountDtoServiceImpl.class);
	private static String PORTFOLIO_VALUE_PROPERTY = "portfolioValue";
	private static String AVAILABLE_CASH_PROPERTY = "availableCash";

	@Override
	public List<AccountDto> findAll(ServiceErrors serviceErrors) {
		final List<ApiSearchCriteria> criteriaList = new ArrayList<>();
		final Map<WrapAccount, Boolean> directMap = new HashMap<>();
		final Map<WrapAccount, BrokerUser> accountBrokerMap = new HashMap<>();
		final Map<AccountKey, WrapAccount> accountsMap = accountService.loadWrapAccountWithoutContainers(serviceErrors);
		createAccountAndBrokerMap(accountsMap, directMap, accountBrokerMap, serviceErrors);
		Map<AccountKey, AccountBalance> accountBalanceMap = new HashMap<>();
		if(isSearchByBalance(criteriaList)) {
			if(accountsMap != null && !accountsMap.keySet().isEmpty()) {
				List<AccountKey> accountKeys = Arrays.asList(accountsMap.keySet().toArray(new AccountKey[]{}));
				accountBalanceMap = accountService.loadAccountBalancesMap(accountKeys, serviceErrors);
			}
		}
		return AccountUtil.getAccountFilterUtil(accountsMap, accountBalanceMap, productIntegrationService.loadProductsMap(serviceErrors),
				brokerIntegrationService).search(criteriaList, directMap, accountBrokerMap);
	}

	@Override
	public List<AccountDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
		final Map<WrapAccount, Boolean> directMap = new HashMap<>();
		final Map<WrapAccount, BrokerUser> accountBrokerMap = new HashMap<>();
		final Map<AccountKey, WrapAccount> accountsMap = accountService.loadWrapAccountWithoutContainers(serviceErrors);
		createAccountAndBrokerMap(accountsMap, directMap, accountBrokerMap, serviceErrors);
		Map<AccountKey, AccountBalance> accountBalanceMap = new HashMap<>();
		if(isSearchByBalance(criteriaList)) {
			if(accountsMap != null && !accountsMap.keySet().isEmpty()) {
				List<AccountKey> accountKeys = Arrays.asList(accountsMap.keySet().toArray(new AccountKey[]{}));
				accountBalanceMap = accountService.loadAccountBalancesMap(accountKeys, serviceErrors);
			}
		}
		return AccountUtil.getAccountFilterUtil(accountsMap, accountBalanceMap, productIntegrationService.loadProductsMap(serviceErrors),
				brokerIntegrationService).search(criteriaList, directMap, accountBrokerMap);
	}

	@Override
	public List<AccountDto> getFilteredValue(String queryString, List<ApiSearchCriteria> filterCriteria,
			ServiceErrors serviceErrors) {
		final Map<WrapAccount, Boolean> directMap = new HashMap<>();
		final Map<WrapAccount, BrokerUser> accountBrokerMap = new HashMap<>();
		final Map<AccountKey, WrapAccount> accountsMap = accountService.loadWrapAccountWithoutContainers(serviceErrors);
		createAccountAndBrokerMap(accountsMap, directMap, accountBrokerMap, serviceErrors);
		AccountDtoConverter accountDtoConverter = null;
		Map<AccountKey, AccountBalance> accountBalanceMap = new HashMap<>();
		if(isSearchByBalance(filterCriteria)) {
			if(accountsMap != null && !accountsMap.keySet().isEmpty()) {
				List<AccountKey> accountKeys = Arrays.asList(accountsMap.keySet().toArray(new AccountKey[]{}));
				accountBalanceMap = accountService.loadAccountBalancesMap(accountKeys, serviceErrors);
			}
		}
		accountDtoConverter = new AccountDtoConverter(accountsMap, accountBalanceMap,
				productIntegrationService.loadProductsMap(serviceErrors), directMap, accountBrokerMap);
		Map<AccountKey, AccountDto> accountDtoMap = accountDtoConverter
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

		//Extract the BrokerKeys from WrapAccount and remove duplicates
		Set<BrokerKey> brokerKeySet = LambdaCollections.with(accountsMap.values()).
				extract(Lambda.on(WrapAccount.class).getAdviserPositionId()).distinct();
		List<BrokerKey> brokerKeys = new ArrayList<>(brokerKeySet);

		//This call wraps brokerHelperService.isDirectInvestor() and brokerIntegrationService.getAdviserBrokerUser(brokerKey, serviceErrors)
		// into one call and set response data into BrokerWrapper.
		if (CollectionUtils.isNotEmpty(brokerKeySet))
		{
			final Map<BrokerKey, BrokerWrapper> brokerWrapperMap =
					brokerIntegrationService.getAdviserBrokerUser(brokerKeys, serviceErrors);
			BrokerWrapper brokerWrapper = null;
			for (WrapAccount wrapAccount : accountsMap.values()) {
				brokerWrapper = brokerWrapperMap.get(wrapAccount.getAdviserPositionId());
				accountBrokerMap.put(wrapAccount, brokerWrapper.getBrokerUser());
				directMap.put(wrapAccount, brokerWrapper.isDirectInvestment());

			}
		}
	}

	private boolean isSearchByBalance(List<ApiSearchCriteria> criteriaList) {
		if(criteriaList != null) {
			for( ApiSearchCriteria criteria : criteriaList){
				if(PORTFOLIO_VALUE_PROPERTY.equalsIgnoreCase(criteria.getProperty()) ||
						AVAILABLE_CASH_PROPERTY.equalsIgnoreCase(criteria.getProperty()) ) {
					LOGGER.info("Searching with {} {} {}", criteria.getProperty(), criteria.getOperation(), criteria.getValue());
					return true;
				}
			}
		}
		return false;
	}
}
