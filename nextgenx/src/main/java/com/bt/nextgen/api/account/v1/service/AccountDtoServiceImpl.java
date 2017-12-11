package com.bt.nextgen.api.account.v1.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.account.v1.model.AccountDto;
import com.bt.nextgen.api.client.util.AccountDtoConverter;
import com.bt.nextgen.api.client.util.AccountFilterMatcher;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @deprecated Use V2
 */
@Deprecated
@Service("AccountDtoServiceV1")
public class AccountDtoServiceImpl implements AccountDtoService
{
	@Autowired
	@Qualifier("avaloqAccountIntegrationService")
	private AccountIntegrationService accountService;

	@Autowired
	private ProductIntegrationService productIntegrationService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Override
    public List<AccountDto> findAll(ServiceErrors serviceErrors) {
        List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        return AccountUtil
                .getAccountFilterUtil(accountService.loadWrapAccountWithoutContainers(serviceErrors),
                accountService.loadAccountBalancesMap(serviceErrors), productIntegrationService.loadProductsMap(serviceErrors),
                brokerIntegrationService).search(criteriaList, serviceErrors);
    }

    @Override
    public List<AccountDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        return AccountUtil
                .getAccountFilterUtil(accountService.loadWrapAccountWithoutContainers(serviceErrors),
                accountService.loadAccountBalancesMap(serviceErrors), productIntegrationService.loadProductsMap(serviceErrors),
                brokerIntegrationService).search(criteriaList, serviceErrors);
    }

    @Override
    public List<AccountDto> getFilteredValue(String queryString, List<ApiSearchCriteria> filterCriteria,
            ServiceErrors serviceErrors) {
        AccountDtoConverter accountDtoConverter = new AccountDtoConverter(accountService.loadWrapAccountWithoutContainers(serviceErrors), null,
                productIntegrationService.loadProductsMap(serviceErrors), brokerIntegrationService);
        Map<com.bt.nextgen.service.integration.account.AccountKey, AccountDto> accountDtoMap = accountDtoConverter
                .convert(serviceErrors);
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
}
