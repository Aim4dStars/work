package com.bt.nextgen.api.account.v2.service;

import com.bt.nextgen.api.account.v2.model.AccountBalanceDto;
import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountBalance;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Deprecated
@Service("AccountBalanceDtoServiceV2")
public class AccountBalanceDtoServiceImpl implements AccountBalanceDtoService {

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Override
    public AccountBalanceDto find(AccountKey key, ServiceErrors serviceErrors) {
        com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));
        AccountBalance accountBalance = accountService.loadAccountBalance(accountKey, serviceErrors);

        return toBalanceDto(accountBalance);
    }

    private AccountBalanceDto toBalanceDto(AccountBalance accountBalance) {
        if (accountBalance != null) {
            return new AccountBalanceDto(new AccountKey(EncodedString.fromPlainText(accountBalance.getAccountKey()).toString()),
                    accountBalance.getAvailableCash(),
                    accountBalance.getPortfolioValue());
        }
        return null;
    }

    @Override
    public List<AccountBalanceDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        final List<com.bt.nextgen.service.integration.account.AccountKey> accountKeyList = new ArrayList<>();
        List<AccountBalance> accountBalanceList = new ArrayList<>();
        for (ApiSearchCriteria criteria : criteriaList) {
            accountKeyList.add(com.bt.nextgen.service.integration.account.AccountKey.valueOf(EncodedString.toPlainText(criteria.getProperty())));
        }
        final Map<com.bt.nextgen.service.integration.account.AccountKey, AccountBalance> accountBalanceMap =
                accountService.loadAccountBalancesMap(accountKeyList, serviceErrors);
        for (com.bt.nextgen.service.integration.account.AccountKey accountKey : accountKeyList) {
            accountBalanceList.add(accountBalanceMap.get(accountKey));
        }
        return toBalanceDtoList(accountBalanceList);
    }

    private List<AccountBalanceDto> toBalanceDtoList(List<AccountBalance> accountBalanceList) {
        final List<AccountBalanceDto> accountBalanceDtoList = new ArrayList<>();
        AccountBalanceDto accountBalanceDto;
        if (accountBalanceList != null) {
            for (AccountBalance accountBalance:accountBalanceList) {
                accountBalanceDto = new AccountBalanceDto(new AccountKey(EncodedString.fromPlainText(accountBalance.getAccountKey()).toString()),
                        accountBalance.getAvailableCash(),
                        accountBalance.getPortfolioValue());
                accountBalanceDtoList.add(accountBalanceDto);
            }
            return accountBalanceDtoList;
        }
        return Collections.emptyList();
    }
}
