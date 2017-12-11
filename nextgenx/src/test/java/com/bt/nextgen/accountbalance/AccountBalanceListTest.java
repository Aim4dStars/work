package com.bt.nextgen.accountbalance;


import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.account.AccountBalanceHolder;
import com.bt.nextgen.service.avaloq.account.AccountBalanceImpl;
import com.bt.nextgen.service.avaloq.client.AccountKeyConverter;
import com.bt.nextgen.service.integration.account.AccountBalance;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ParsingContext.class})
public class AccountBalanceListTest
{
    @InjectMocks
    DefaultResponseExtractor defaultResponseExtractor;
    @Mock
    ParsingContext parsingContext;
    @Mock
    ApplicationContext applicationContext;
    @Mock
    AccountKeyConverter accountKeyConverter;
    @Mock
    BigDecimalConverter bigDecimalConverter;

    @Mock
    AccountIntegrationService accountService;

    @Test
    public void testAccountBalanceListResponse() throws Exception
    {
        final ClassPathResource classPathResource = new ClassPathResource("/webservices/response/accountbalance/AccountBalancesListResponse.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(classPathResource.getInputStream()));

       PowerMockito.mockStatic(ParsingContext.class);
       Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);

        Mockito.when(applicationContext.getBean(AccountKeyConverter.class)).thenReturn(accountKeyConverter);
        Mockito.when(accountKeyConverter.convert("71319")).thenReturn(AccountKey.valueOf("71319"));
        Mockito.when(accountKeyConverter.convert("71335")).thenReturn(AccountKey.valueOf("71335"));
        Mockito.when(accountKeyConverter.convert("71353")).thenReturn(AccountKey.valueOf("71353"));

        Mockito.when(applicationContext.getBean(BigDecimalConverter.class)).thenReturn(bigDecimalConverter);

        Mockito.when(bigDecimalConverter.convert("99.28")).thenReturn(BigDecimal.valueOf(99.28));
        Mockito.when(bigDecimalConverter.convert("151.08")).thenReturn(BigDecimal.valueOf(151.08));
        Mockito.when(bigDecimalConverter.convert("50.6")).thenReturn(BigDecimal.valueOf(50.6));
        Mockito.when(bigDecimalConverter.convert("1820959.62")).thenReturn(BigDecimal.valueOf(1820959.62));
        Mockito.when(bigDecimalConverter.convert("1257897.76")).thenReturn(BigDecimal.valueOf(1257897.76));
        Mockito.when(bigDecimalConverter.convert("351198.45")).thenReturn(BigDecimal.valueOf(351198.45));


        DefaultResponseExtractor<AccountBalanceHolder> defaultResponseExtractor = new DefaultResponseExtractor<>(AccountBalanceHolder.class);

        AccountBalanceHolder accountBalanceHolder = defaultResponseExtractor.extractData(content);

        assertThat(accountBalanceHolder, is(notNullValue()));


        assertThat(accountBalanceHolder.getAccountBalances(), is(notNullValue()));
        assertThat(accountBalanceHolder.getAccountBalances().size(), is(not(0)));
        assertThat(accountBalanceHolder.getAccountBalances().size(), is(3));

        assertThat(accountBalanceHolder.getAccountBalances().get(0), is(notNullValue()));
        assertThat(accountBalanceHolder.getAccountBalances().get(0).getKey(), is(AccountKey.valueOf("71319")));
        assertThat(accountBalanceHolder.getAccountBalances().get(0).getAvailableCash(), is(BigDecimal.valueOf(99.28)));
        assertThat(accountBalanceHolder.getAccountBalances().get(0).getPortfolioValue(), is(BigDecimal.valueOf(1820959.62)));

        assertThat(accountBalanceHolder.getAccountBalances().get(1), is(notNullValue()));
        assertThat(accountBalanceHolder.getAccountBalances().get(1).getKey(), is(AccountKey.valueOf("71335")));
        assertThat(accountBalanceHolder.getAccountBalances().get(1).getAvailableCash(), is(BigDecimal.valueOf(151.08)));
        assertThat(accountBalanceHolder.getAccountBalances().get(1).getPortfolioValue(), is(BigDecimal.valueOf(1257897.76)));

        assertThat(accountBalanceHolder.getAccountBalances().get(2), is(notNullValue()));
        assertThat(accountBalanceHolder.getAccountBalances().get(2).getKey(), is(AccountKey.valueOf("71353")));
        assertThat(accountBalanceHolder.getAccountBalances().get(2).getAvailableCash(), is(BigDecimal.valueOf(50.6)));
        assertThat(accountBalanceHolder.getAccountBalances().get(2).getPortfolioValue(), is(BigDecimal.valueOf(351198.45)));

       }

    @Test
    public void testAccountBalancesMapResponse() throws Exception
    {

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<AccountBalance> acctBalanceList=new ArrayList<>();
        AccountBalanceImpl accountBalance = new AccountBalanceImpl();
        Map<AccountKey,AccountBalance> accountBalMapMock=new HashMap<>();
        accountBalance.setAccountKey("31352");
        accountBalance.setAvailableCash(BigDecimal.valueOf(2000));
        accountBalance.setPortfolioValue(BigDecimal.valueOf(200));
        acctBalanceList.add(accountBalance);
        accountBalMapMock.put(AccountKey.valueOf("31352"),accountBalance);
        accountBalance = new AccountBalanceImpl();
        accountBalance.setAccountKey("32049");
        accountBalance.setAvailableCash(BigDecimal.valueOf(1000));
        accountBalance.setPortfolioValue(BigDecimal.valueOf(100));
        acctBalanceList.add(accountBalance);
        accountBalMapMock.put(AccountKey.valueOf("32049"),accountBalance);
        accountBalance = new AccountBalanceImpl();
        accountBalance.setAccountKey("31747");
        accountBalance.setAvailableCash(BigDecimal.valueOf(0));
        accountBalance.setPortfolioValue(BigDecimal.valueOf(0));
        acctBalanceList.add(accountBalance);
        accountBalMapMock.put(AccountKey.valueOf("31747"),accountBalance);

        List<AccountKey> accountKeyList = Arrays.asList(AccountKey.valueOf("31352"),
                AccountKey.valueOf("32049"),
                AccountKey.valueOf("31747"));

        Mockito.when(accountService.loadAccountBalancesMap(Mockito.anyList(),
                Mockito.any(ServiceErrors.class))).thenReturn(accountBalMapMock);
        Map<AccountKey,AccountBalance> accountBalMap= accountService.loadAccountBalancesMap(accountKeyList, serviceErrors);
        assertThat(accountBalMap, is(notNullValue()));
        assertThat(accountBalMap.size(), is(3));

       }




}
