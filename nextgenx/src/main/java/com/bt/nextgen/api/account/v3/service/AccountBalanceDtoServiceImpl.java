package com.bt.nextgen.api.account.v3.service;

import com.bt.nextgen.api.account.v3.model.AccountBalanceDto;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountBalanceImpl;
import com.bt.nextgen.service.integration.account.AccountBalance;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service("AccountBalanceDtoServiceV3")
public class AccountBalanceDtoServiceImpl implements AccountBalanceDtoService {

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Override
    public AccountBalanceDto find(AccountKey key, ServiceErrors serviceErrors) {
        com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf(ConsistentEncodedString.toPlainText(key.getAccountId()));
        AccountBalance accountBalance = accountService.loadAccountBalance(accountKey, serviceErrors);

        return toBalanceDto(key, accountBalance);
    }

    private AccountBalanceDto toBalanceDto(AccountKey accountKey, AccountBalance accountBalance) {
        if (accountBalance != null) {
            return new AccountBalanceDto(accountKey, accountBalance.getAvailableCash(), accountBalance.getPortfolioValue());
        } else {
            return new AccountBalanceDto(accountKey, new BigDecimal(0), new BigDecimal(0));
        }
    }

    @Override
    public List<AccountBalanceDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        final List<com.bt.nextgen.service.integration.account.AccountKey> accountKeyList = new ArrayList<>();
        List<AccountBalance> accountBalanceList = new ArrayList<>();
        for (ApiSearchCriteria criteria : criteriaList) {
             accountKeyList.add(com.bt.nextgen.service.integration.account.AccountKey.valueOf(ConsistentEncodedString.toPlainText(criteria.getProperty())));
        }
        final Map<com.bt.nextgen.service.integration.account.AccountKey, AccountBalance> accountBalanceMap =
                accountService.loadAccountBalancesMap(accountKeyList, serviceErrors);
        if (accountBalanceMap != null) {
            for (com.bt.nextgen.service.integration.account.AccountKey accountKey : accountKeyList) {
                if (accountBalanceMap.containsKey(accountKey)) {
                    accountBalanceList.add(accountBalanceMap.get(accountKey));
                } else {
                    // If Avaloq does not return any data for particular account key , then set zero balances
                    AccountBalanceImpl accountBalance = new AccountBalanceImpl();
                    accountBalance.setAccountKey(accountKey.getId());
                    accountBalance.setKey(accountKey);
                    accountBalance.setPortfolioValue(new BigDecimal(0));
                    accountBalance.setAvailableCash(new BigDecimal(0));
                    accountBalanceList.add(accountBalance);
                }
            }
        }
        return toBalanceDtoList(accountBalanceList);
    }

    private List<AccountBalanceDto> toBalanceDtoList(List<AccountBalance> accountBalanceList) {
        final List<AccountBalanceDto> accountBalanceDtoList = new ArrayList<>();
        AccountBalanceDto accountBalanceDto;
        if (accountBalanceList != null) {
            for (AccountBalance accountBalance:accountBalanceList) {
                accountBalanceDto = new AccountBalanceDto(new AccountKey(ConsistentEncodedString.fromPlainText(accountBalance.getAccountKey()).toString()),
                        accountBalance.getAvailableCash(),
                        accountBalance.getPortfolioValue());
                accountBalanceDtoList.add(accountBalanceDto);
            }
            return accountBalanceDtoList;
        }
        return Collections.emptyList();
    }
}
