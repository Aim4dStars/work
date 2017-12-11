package com.bt.nextgen.api.account.v2.service;

import com.bt.nextgen.api.account.v2.model.AccountBalanceDto;
import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.account.AccountBalanceImpl;
import com.bt.nextgen.service.integration.account.AccountBalance;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class AccountBalanceDtoServiceTest {

    @InjectMocks
    private AccountBalanceDtoServiceImpl accountBalanceService;

    @Mock
    AccountIntegrationService accountService;
    AccountKey key;
    ServiceErrors serviceErrors;

    List<AccountKey> keyList;
    List<AccountBalance> acctBalanceList;
    List<ApiSearchCriteria> criteriaList;
    Map<com.bt.nextgen.service.integration.account.AccountKey, AccountBalance> accountBalanceMap;

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

        keyList = Arrays.asList(new AccountKey(EncodedString.fromPlainText("31352").toString()),
                new AccountKey(EncodedString.fromPlainText("32049").toString()),
                new AccountKey(EncodedString.fromPlainText("31747").toString()));
        accountBalanceMap = new HashMap<>();
        acctBalanceList = new ArrayList<>();
        accountBalance = new AccountBalanceImpl();
        accountBalance.setAccountKey("31352");
        accountBalance.setAvailableCash(BigDecimal.valueOf(2000));
        accountBalance.setPortfolioValue(BigDecimal.valueOf(200));
        accountBalanceMap.put(com.bt.nextgen.service.integration.account.AccountKey.valueOf("31352"),accountBalance);
        acctBalanceList.add(accountBalance);
        accountBalance = new AccountBalanceImpl();
        accountBalance.setAccountKey("32049");
        accountBalance.setAvailableCash(BigDecimal.valueOf(1000));
        accountBalance.setPortfolioValue(BigDecimal.valueOf(100));
        accountBalanceMap.put(com.bt.nextgen.service.integration.account.AccountKey.valueOf("32049"),accountBalance);
        acctBalanceList.add(accountBalance);
        accountBalance = new AccountBalanceImpl();
        accountBalance.setAccountKey("31747");
        accountBalance.setAvailableCash(BigDecimal.valueOf(0));
        accountBalance.setPortfolioValue(BigDecimal.valueOf(0));
        accountBalanceMap.put(com.bt.nextgen.service.integration.account.AccountKey.valueOf("31747"),accountBalance);
        acctBalanceList.add(accountBalance);


        Mockito.when(accountService.loadAccountBalances(Mockito.anyList(),
                Mockito.any(ServiceErrors.class))).thenReturn(acctBalanceList);

        Mockito.when(accountService.loadAccountBalancesMap(Mockito.anyList(),
                Mockito.any(ServiceErrors.class))).thenReturn(accountBalanceMap);
        criteriaList=new ArrayList<>();
        for(AccountKey key:keyList){
             criteriaList.add(new ApiSearchCriteria(key.getAccountId(), key.getAccountId()));
        }
     }

    @Test
    public void testFind() throws Exception {
        AccountBalanceDto accountBalanceDto = accountBalanceService.find(key, serviceErrors);
        Assert.assertNotNull(accountBalanceDto);
        Assert.assertEquals(accountBalanceDto.getAvailableCash(), BigDecimal.valueOf(10000));
        Assert.assertEquals(accountBalanceDto.getPortfolioValue(), BigDecimal.valueOf(1000));
    }

    @Test
    @Ignore
    public void testSearch() throws Exception {
        List<AccountBalanceDto> accountBalanceDtoList = accountBalanceService.search(criteriaList, serviceErrors);
        Assert.assertNotNull(accountBalanceDtoList);
        Assert.assertEquals(accountBalanceDtoList.size(), 3);
        Assert.assertEquals(accountBalanceDtoList.get(0).getAvailableCash(), BigDecimal.valueOf(2000));
        Assert.assertEquals(accountBalanceDtoList.get(0).getPortfolioValue(), BigDecimal.valueOf(200));
        Assert.assertEquals(accountBalanceDtoList.get(1).getAvailableCash(), BigDecimal.valueOf(1000));
        Assert.assertEquals(accountBalanceDtoList.get(1).getPortfolioValue(), BigDecimal.valueOf(100));
        Assert.assertEquals(accountBalanceDtoList.get(2).getAvailableCash(), BigDecimal.valueOf(0));
        Assert.assertEquals(accountBalanceDtoList.get(2).getPortfolioValue(), BigDecimal.valueOf(0));
    }

    @Test
    public void testSearch_calls_balancesMap() throws Exception {
        List<AccountBalanceDto> accountBalanceDtoList = accountBalanceService.search(criteriaList, serviceErrors);
        Assert.assertNotNull(accountBalanceDtoList);
        Assert.assertEquals(accountBalanceDtoList.size(), 3);
        Assert.assertEquals(accountBalanceDtoList.get(0).getAvailableCash(), BigDecimal.valueOf(2000));
        Assert.assertEquals(accountBalanceDtoList.get(0).getPortfolioValue(), BigDecimal.valueOf(200));
        Assert.assertEquals(accountBalanceDtoList.get(1).getAvailableCash(), BigDecimal.valueOf(1000));
        Assert.assertEquals(accountBalanceDtoList.get(1).getPortfolioValue(), BigDecimal.valueOf(100));
        Assert.assertEquals(accountBalanceDtoList.get(2).getAvailableCash(), BigDecimal.valueOf(0));
        Assert.assertEquals(accountBalanceDtoList.get(2).getPortfolioValue(), BigDecimal.valueOf(0));
    }
}

