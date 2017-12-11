package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.api.account.v1.model.AccountBalanceDto;
import com.bt.nextgen.api.account.v1.model.AccountKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.account.AccountBalanceImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

@RunWith(MockitoJUnitRunner.class)
public class AccountBalanceDtoServiceTest {

    @InjectMocks
    private AccountBalanceDtoServiceImpl accountBalanceService;

    @Mock
    AccountIntegrationService accountService;
    AccountKey key;
    ServiceErrors serviceErrors;

    @Before
    public void setup() throws Exception {
        key = new AccountKey(EncodedString.fromPlainText("36846").toString());
        serviceErrors = new ServiceErrorsImpl();

        AccountBalanceImpl accountBalance = new AccountBalanceImpl();
        accountBalance.setAccountKey("36846");
        accountBalance.setAvailableCash(BigDecimal.valueOf(10000));
        accountBalance.setPortfolioValue(BigDecimal.valueOf(1000));

        Mockito.when(accountService.loadAccountBalance(Mockito.any(com.bt.nextgen.service.integration.account.AccountKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(accountBalance);
    }

    @Test
    public void testFind() throws Exception {
        AccountBalanceDto accountBalanceDto = accountBalanceService.find(key, serviceErrors);
        Assert.assertNotNull(accountBalanceDto);
        Assert.assertEquals(accountBalanceDto.getAvailableCash(), BigDecimal.valueOf(10000));
        Assert.assertEquals(accountBalanceDto.getPortfolioValue(), BigDecimal.valueOf(1000));
    }
}

