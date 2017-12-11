package com.bt.nextgen.service.avaloq.account;

import ch.lambdaj.Lambda;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.account.AccountBalance;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.on;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class AccountBalanceIntegrationTest extends BaseSecureIntegrationTest {

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    AccountIntegrationService accountIntegrationService;


    @SecureTestContext
    @Test
    public void testLoadAccountBalanceList() {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        Collection<AccountBalance> accountBalanceList = accountIntegrationService.loadAccountBalances(serviceErrors);
        Assert.assertThat(accountBalanceList.size(), is(3));
        Map<String, AccountBalance> accountBalanceMap = new HashMap<>();

        accountBalanceMap = Lambda.index(accountBalanceList, on(AccountBalance.class).getAccountKey());

        AccountBalance accountBalance = accountBalanceMap.get("11263");
        Assert.assertNotNull(accountBalance);
        Assert.assertThat(accountBalance.getAvailableCash(), is(BigDecimal.valueOf(49957101.75)));
        Assert.assertThat(accountBalance.getPortfolioValue(), is(BigDecimal.valueOf(101468594.75)));
    }

    @SecureTestContext
    @Test
    public void testLoadAccountBalanceMap() {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Map<AccountKey, AccountBalance> accountBalanceMap = accountIntegrationService.loadAccountBalancesMap(serviceErrors);
        Assert.assertThat(accountBalanceMap.size(), is(3));
        AccountBalance accountBalance = accountBalanceMap.get(AccountKey.valueOf("11263"));
        Assert.assertNotNull(accountBalance);
        Assert.assertThat(accountBalance.getAvailableCash(), is(BigDecimal.valueOf(49957101.75)));
        Assert.assertThat(accountBalance.getPortfolioValue(), is(BigDecimal.valueOf(101468594.75)));
    }


    @Test
    @SecureTestContext
    public void testLoadAccountBalance() {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        AccountBalance accountBalance = accountIntegrationService.loadAccountBalance(AccountKey.valueOf("85354"), serviceErrors);
        Assert.assertNotNull(accountBalance);
        Assert.assertThat("85354", is(accountBalance.getKey().getId()));
        Assert.assertThat(accountBalance.getAvailableCash(), is(BigDecimal.valueOf(50042223.83)));
        Assert.assertThat(accountBalance.getPortfolioValue(), is(BigDecimal.valueOf(100163684.81)));
    }

    
    @SecureTestContext(username = "explode", customerId = "201101101")
    @Test
    public void testLoadAccountBalanceLisErrort() {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Collection<AccountBalance> accountBalanceList = accountIntegrationService.loadAccountBalances(serviceErrors);
        assertThat(serviceErrors.hasErrors(), Is.is(true));
    }

    @Test
    @SecureTestContext(username = "accountbalance", customerId = "201635682")
    public void testLoadAccountBalancesList() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<AccountKey> accountKeyList = Arrays.asList(AccountKey.valueOf("71319"),
                AccountKey.valueOf("71335"),
                AccountKey.valueOf("71353"));
        List<AccountBalance> accBalanceList= accountIntegrationService.loadAccountBalances(accountKeyList,serviceErrors);
        Assert.assertNotNull(accBalanceList);
        assertThat(serviceErrors.hasErrors(), Is.is(false));
        Assert.assertThat(3, is(accBalanceList.size()));
        Assert.assertThat("71319", is(accBalanceList.get(0).getKey().getId()));
        Assert.assertThat("99.28", is(accBalanceList.get(0).getAvailableCash().toString()));
        Assert.assertThat("1820959.62", is(accBalanceList.get(0).getPortfolioValue().toString()));
        Assert.assertThat("71335", is(accBalanceList.get(1).getKey().getId()));
        Assert.assertThat("151.08", is(accBalanceList.get(1).getAvailableCash().toString()));
        Assert.assertThat("1257897.76", is(accBalanceList.get(1).getPortfolioValue().toString()));
        Assert.assertThat("71353", is(accBalanceList.get(2).getKey().getId()));
        Assert.assertThat("50.6", is(accBalanceList.get(2).getAvailableCash().toString()));
        Assert.assertThat("351198.45", is(accBalanceList.get(2).getPortfolioValue().toString()));
    }

    @Test
    @SecureTestContext(username = "accountbalance", customerId = "201635682")
    public void testLoadAccountBalancesMap() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<AccountKey> accountKeyList = Arrays.asList(AccountKey.valueOf("71319"),
                AccountKey.valueOf("71335"),
                AccountKey.valueOf("71353"));
        Map<com.bt.nextgen.service.integration.account.AccountKey, AccountBalance> accountBalanceMap = accountIntegrationService.loadAccountBalancesMap(accountKeyList, serviceErrors);

        Assert.assertNotNull(accountBalanceMap);
        assertThat(serviceErrors.hasErrors(), Is.is(false));
        Assert.assertThat(3, is(accountBalanceMap.size()));
    }
}

