package com.bt.nextgen.api.account.v3.service;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.account.v3.model.AccountSearchTypeEnum;
import com.bt.nextgen.api.account.v3.util.AccountSearchDtoUtil;
import com.bt.nextgen.api.account.v3.util.AccountUtil;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountBalance;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.account.WrapAccountDetailResponse;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class AccountSearchByAccountDtoServiceImpl implements AccountSearchByAccountDtoService {
    private static final String SEARCH_KEY_REGEX = "[A-Za-z0-9'\\s]+";

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    private static Logger LOGGER = LoggerFactory.getLogger(AccountSearchByAccountDtoServiceImpl.class);
    private static String PORTFOLIO_VALUE_PROPERTY = "portfolioValue";
    private static String AVAILABLE_CASH_PROPERTY = "availableCash";

    @Override
    public List<AccountDto> search(AccountKey key, ServiceErrors serviceErrors) {
        List<AccountDto> accountDtoList = new ArrayList<>();

        String[] keys = key.getAccountId().split(",");
        AccountSearchTypeEnum searchType = AccountSearchTypeEnum.fromValue(keys[0]);
        String searchKey = keys[1].toLowerCase();

        if (Pattern.matches(SEARCH_KEY_REGEX, searchKey)) {
            WrapAccountDetailResponse accountRes = accountIntegrationService.loadWrapAccountDetailByAccountDetails(searchKey, serviceErrors);
            if (accountRes != null && CollectionUtils.isNotEmpty(accountRes.getWrapAccountDetails())) {
                for (WrapAccountDetail account : accountRes.getWrapAccountDetails()) {
                    if (isMatch(account, searchType, searchKey)) {
                        accountDtoList.add(AccountSearchDtoUtil.toAccountDto(account));
                    }
                }
                setProductAndBrokerNames(accountDtoList, serviceErrors);
            }
        }
        return accountDtoList;
    }

    @Override
    public List<AccountDto> search(AccountKey key, List<ApiSearchCriteria> criteria, ServiceErrors serviceErrors) {
        List<AccountDto> accountDtoList = search(key, serviceErrors);
        Map<com.bt.nextgen.service.integration.account.AccountKey, AccountBalance> accountBalanceMap = new HashMap<>();
        if(isSearchByBalance(criteria) && !accountDtoList.isEmpty()) {
            accountBalanceMap = accountIntegrationService.loadAccountBalancesMap(getAccountKeys(accountDtoList), serviceErrors);
        }
        Map<com.bt.nextgen.service.integration.account.AccountKey, AccountDto> accountDtoMap = new HashMap<>();
        for (AccountDto accountDto : accountDtoList) {
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

        return AccountUtil.getAccountFilterUtil(accountDtoMap).search(criteria);
    }

    @Override
    public AccountDto find(AccountKey key, ServiceErrors serviceErrors) {
        return null;
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

    private List<com.bt.nextgen.service.integration.account.AccountKey> getAccountKeys(List<AccountDto> accountDtoList) {
        List<com.bt.nextgen.service.integration.account.AccountKey> accountKeys = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(accountDtoList)) {
            for (AccountDto accountDto : accountDtoList) {
                accountKeys.add(com.bt.nextgen.service.integration.account.AccountKey.valueOf(ConsistentEncodedString.toPlainText(accountDto.getKey().getAccountId())));
            }
        }
        return accountKeys;
    }

    private void setProductAndBrokerNames(List<AccountDto> accountDtoList, ServiceErrors serviceErrors) {
        Map<String, String> productMap = AccountSearchDtoUtil.getProductMap(accountDtoList, productIntegrationService, serviceErrors);
        Map<String, String> brokerMap = AccountSearchDtoUtil.getBrokerMap(accountDtoList, brokerIntegrationService, serviceErrors);
        AccountSearchDtoUtil.setProductAndBrokerNames(accountDtoList, productMap, brokerMap);
    }

    private boolean isMatch(WrapAccountDetail account, AccountSearchTypeEnum searchType, String searchKey) {
        return searchType == AccountSearchTypeEnum.ACCOUNT_ID ? account.getAccountNumber().contains(searchKey)
                : account.getAccountName().toLowerCase().contains(searchKey);
    }

}
