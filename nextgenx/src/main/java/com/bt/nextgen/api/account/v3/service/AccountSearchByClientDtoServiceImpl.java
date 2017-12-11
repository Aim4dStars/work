package com.bt.nextgen.api.account.v3.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.api.account.v3.model.AccountSearchDto;
import com.bt.nextgen.api.account.v3.model.AccountSearchKey;
import com.bt.nextgen.api.account.v3.util.AccountSearchDtoUtil;
import com.bt.nextgen.api.account.v3.util.AccountUtil;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.domain.existingclient.AccountDataForIndividual;
import com.bt.nextgen.service.avaloq.domain.existingclient.IndividualWithAccountDataImpl;
import com.bt.nextgen.service.integration.account.AccountBalance;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.btfin.panorama.service.avaloq.domain.existingclient.Client;
import org.apache.commons.collections.CollectionUtils;
import org.hamcrest.core.IsNot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class AccountSearchByClientDtoServiceImpl implements AccountSearchByClientDtoService {
    private static final String SEARCH_KEY_REGEX = "[A-Za-z0-9'\\s]+";

    @Autowired
    @Qualifier("avaloqClientIntegrationService")
    private ClientIntegrationService clientIntegrationService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    private static Logger LOGGER = LoggerFactory.getLogger(AccountSearchByClientDtoServiceImpl.class);
    private static String PORTFOLIO_VALUE_PROPERTY = "portfolioValue";
    private static String AVAILABLE_CASH_PROPERTY = "availableCash";
    private static String EMPTY_STRING= "";

    @Override
    public List<AccountSearchDto> search(AccountSearchKey key, ServiceErrors serviceErrors) {
        List<AccountSearchDto> accountSearchDtoList = new ArrayList<>();
        if (EMPTY_STRING.equals(key.getClientId()) || Pattern.matches(SEARCH_KEY_REGEX, key.getClientId())) {
            Collection<Client> clients = clientIntegrationService.loadClientsForExistingClientSearch(serviceErrors, key.getClientId());
            if (CollectionUtils.isNotEmpty(clients)) {
                List<AccountDto> allAccounts = new ArrayList<>();
                for (Client client : clients) {
                    if (client.getLegalForm() == InvestorType.INDIVIDUAL) {
                        IndividualWithAccountDataImpl individual = (IndividualWithAccountDataImpl) client;
                        AccountSearchDto accountSearchDto = getAccountSearchDto(individual, serviceErrors);
                        allAccounts.addAll(accountSearchDto.getAccounts());
                        accountSearchDtoList.add(accountSearchDto);
                    }
                }
                setProductAndBrokerNames(allAccounts, accountSearchDtoList, serviceErrors);
            }
        }
        return accountSearchDtoList;
    }

    @Override
    public List<AccountSearchDto> search(AccountSearchKey key, List<ApiSearchCriteria> criteria, ServiceErrors serviceErrors) {
        List<AccountSearchDto> accountSearchDtoList = search(key, serviceErrors);
        Map<com.bt.nextgen.service.integration.account.AccountKey, AccountBalance> accountBalanceMap = new HashMap<>();
        if(isSearchByBalance(criteria) && !accountSearchDtoList.isEmpty()) {
            accountBalanceMap = accountIntegrationService.loadAccountBalancesMap(getAccountKeys(accountSearchDtoList), serviceErrors);
        }
        for (AccountSearchDto accountSearchDto : accountSearchDtoList) {
            Map<com.bt.nextgen.service.integration.account.AccountKey, AccountDto> accountDtoMap = new HashMap<>();
            for (AccountDto accountDto : accountSearchDto.getAccounts()) {
                AccountBalance accountBalance = null;
                if (accountBalanceMap != null && !accountBalanceMap.isEmpty() && isSearchByBalance(criteria)) {
                    accountBalance = accountBalanceMap.get(com.bt.nextgen.service.integration.account.AccountKey.valueOf
                        (ConsistentEncodedString.toPlainText(accountDto.getKey().getAccountId())));
                    if (accountBalance != null) {
                        accountDto.setAvailableCash(accountBalance.getAvailableCash());
                        accountDto.setPortfolioValue(accountBalance.getPortfolioValue());
                    } else {
                        accountDto.setAvailableCash(new BigDecimal("0.00"));
                        accountDto.setPortfolioValue(new BigDecimal("0.00"));
                    }
                }
                accountDtoMap.put(com.bt.nextgen.service.integration.account.AccountKey.valueOf(accountDto.getKey().getAccountId()), accountDto);
            }
            accountSearchDto.setAccounts(AccountUtil.getAccountFilterUtil(accountDtoMap).search(criteria));
        }
        return Lambda.filter(Lambda.having(Lambda.on(AccountSearchDto.class).getAccounts().size(), IsNot.not(0)), accountSearchDtoList);
    }
    
    @Override
    public AccountSearchDto find(AccountSearchKey key, ServiceErrors serviceErrors) {
        return null;
    }

    private void setProductAndBrokerNames(List<AccountDto> allAccounts, List<AccountSearchDto> accountSearchDtoList, ServiceErrors serviceErrors) {
        Map<String, String> productMap = AccountSearchDtoUtil.getProductMap(allAccounts, productIntegrationService, serviceErrors);
        Map<String, String> brokerMap = AccountSearchDtoUtil.getBrokerMap(allAccounts, brokerIntegrationService, serviceErrors);
        for (AccountSearchDto accountSearchDto : accountSearchDtoList) {
            AccountSearchDtoUtil.setProductAndBrokerNames(accountSearchDto.getAccounts(), productMap, brokerMap);
        }
    }

    private AccountSearchDto getAccountSearchDto(IndividualWithAccountDataImpl individual, ServiceErrors serviceErrors) {
        AccountSearchDto accountSearchDto = new AccountSearchDto();
        accountSearchDto.setDisplayName(individual.getLastName() + ", " + individual.getFirstName());
        accountSearchDto.setKey(new AccountSearchKey(EncodedString.fromPlainText(individual.getClientKey().getId()).toString(), null));
        List<AccountDto> accountDtoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(individual.getAccountData())) {
            accountDtoList = getAccountDtoList(individual, serviceErrors);
        }
        accountSearchDto.setAccounts(accountDtoList);
        return accountSearchDto;
    }

    private List<AccountDto> getAccountDtoList(IndividualWithAccountDataImpl individual, ServiceErrors serviceErrors) {
        List<AccountDto> accountDtoList = new ArrayList<>();
        for (AccountDataForIndividual accountData : individual.getAccountData()) {
            WrapAccountDetail account = accountIntegrationService.loadWrapAccountDetail(AccountKey.valueOf(accountData.getAccountId()), serviceErrors);
            if (account != null) {
                AccountDto accountDto = AccountSearchDtoUtil.toAccountDto(account);
                accountDtoList.add(accountDto);
            }
        }
        return accountDtoList;
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

    private List<com.bt.nextgen.service.integration.account.AccountKey> getAccountKeys(List<AccountSearchDto> accountSearchDtos) {
        List<com.bt.nextgen.service.integration.account.AccountKey> accountKeys = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(accountSearchDtos)) {
            for (AccountSearchDto accountSearchDto : accountSearchDtos) {
                if(CollectionUtils.isNotEmpty(accountSearchDto.getAccounts())) {
                    for(AccountDto accountDto : accountSearchDto.getAccounts()) {
                        accountKeys.add(com.bt.nextgen.service.integration.account.AccountKey.valueOf(ConsistentEncodedString.toPlainText(accountDto.getKey().getAccountId())));
                    }
                }
            }
        }
        return accountKeys;
    }

}
