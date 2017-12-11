package com.bt.nextgen.service.avaloq.modelportfolio.rebalance;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.core.conversion.BigIntegerConverter;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.avaloq.client.AccountKeyConverter;
import com.bt.nextgen.service.avaloq.client.BrokerKeyConverter;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceAccount;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ParsingContext.class })
public class RebalanceAccountsResponseImplTest {

    @InjectMocks
    private DefaultResponseExtractor<RebalanceAccountsResponseImpl> defaultResponseExtractor;
    @Mock
    private ParsingContext parsingContext;
    @Mock
    private ApplicationContext applicationContext;

    private BigIntegerConverter bigIntegerConverter = new BigIntegerConverter();
    private BigDecimalConverter bigDecimalConverter = new BigDecimalConverter();
    private AccountKeyConverter accountKeyConverter = new AccountKeyConverter();
    private BrokerKeyConverter brokerKeyConverter = new BrokerKeyConverter();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ParsingContext.class);
        Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);
        Mockito.when(applicationContext.getBean(BigIntegerConverter.class)).thenReturn(bigIntegerConverter);
        Mockito.when(applicationContext.getBean(BigDecimalConverter.class)).thenReturn(bigDecimalConverter);
        Mockito.when(applicationContext.getBean(AccountKeyConverter.class)).thenReturn(accountKeyConverter);
        Mockito.when(applicationContext.getBean(BrokerKeyConverter.class)).thenReturn(brokerKeyConverter);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testXPathMappings() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource("/webservices/response/RebalanceAccounts_UT.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(RebalanceAccountsResponseImpl.class);
        RebalanceAccountsResponseImpl response = defaultResponseExtractor.extractData(content);

        Assert.assertNotNull(response);
        Assert.assertEquals(1, response.getAccountRebalances().size());
        RebalanceAccount rebalAccount = response.getAccountRebalances().get(0);
        Assert.assertEquals(AccountKey.valueOf("252778"), rebalAccount.getAccount());
        Assert.assertEquals(BrokerKey.valueOf("100793"), rebalAccount.getAdviser());
        Assert.assertEquals(Integer.valueOf(2), rebalAccount.getAssetClassBreach());
        Assert.assertEquals(Integer.valueOf(27), rebalAccount.getToleranceBreach());
        Assert.assertEquals(BigDecimal.valueOf(30000), rebalAccount.getValue());
        Assert.assertEquals(Integer.valueOf(25), rebalAccount.getEstimatedBuys());
        Assert.assertEquals(Integer.valueOf(0), rebalAccount.getEstimatedSells());
    }

}