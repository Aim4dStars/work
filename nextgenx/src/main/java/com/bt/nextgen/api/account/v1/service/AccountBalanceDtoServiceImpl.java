package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.api.account.v1.model.AccountBalanceDto;
import com.bt.nextgen.api.account.v1.model.AccountKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountBalance;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @deprecated Use V2
 */
@Deprecated
@Service("AccountBalanceDtoServiceV1")
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
}
