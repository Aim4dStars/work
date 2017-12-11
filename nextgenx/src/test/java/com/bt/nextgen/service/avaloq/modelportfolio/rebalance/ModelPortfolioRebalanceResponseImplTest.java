package com.bt.nextgen.service.avaloq.modelportfolio.rebalance;

import com.bt.nextgen.integration.xml.converter.CodeConverter;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.avaloq.ips.IpsKeyConverter;
import com.bt.nextgen.service.avaloq.modelportfolio.TriggerStatus;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalance;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalanceTrigger;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalanceTriggerDetails;
import org.joda.time.DateTime;
import org.junit.After;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ParsingContext.class })
public class ModelPortfolioRebalanceResponseImplTest {

    @InjectMocks
    DefaultResponseExtractor<ModelPortfolioRebalanceResponseImpl> defaultResponseExtractor;
    @Mock
    ParsingContext parsingContext;
    @Mock
    ApplicationContext applicationContext;

    IpsKeyConverter investmentPolicyStatementConverter = new IpsKeyConverter();

    DateTimeTypeConverter dateTimeTypeConverter = new DateTimeTypeConverter();

    @Mock
    CodeConverter codeConverter;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ParsingContext.class);
        Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);
        Mockito.when(applicationContext.getBean(DateTimeTypeConverter.class)).thenReturn(dateTimeTypeConverter);
        Mockito.when(applicationContext.getBean(IpsKeyConverter.class))
                .thenReturn(investmentPolicyStatementConverter);
        Mockito.when(applicationContext.getBean(CodeConverter.class)).thenReturn(codeConverter);
        Mockito.when(codeConverter.convert("1", "IPS_TRIG_REBAL_STATUS")).thenReturn("btfg$compl");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testXPathMappings() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource(
                "/webservices/response/modelportfolio/RebalanceSummaryResponse.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(ModelPortfolioRebalanceResponseImpl.class);
        ModelPortfolioRebalanceResponseImpl response = defaultResponseExtractor.extractData(content);

        assertNotNull(response);
        assertEquals(1, response.getModelPortfolioRebalances().size());

        ModelPortfolioRebalance rebalance = response.getModelPortfolioRebalances().get(0);
        assertNotNull(rebalance);
        assertEquals("370801", rebalance.getIpsKey().getId());
        assertEquals("9511", rebalance.getIpsStatus());
        assertEquals(new DateTime("2016-05-28"), rebalance.getLastRebalanceDate());
        assertEquals(Integer.valueOf(1), rebalance.getTotalAccountsCount());
        assertEquals(Integer.valueOf(0), rebalance.getTotalRebalancesCount());
        assertEquals("Young, Adam", rebalance.getUserName());
        assertEquals(3, rebalance.getRebalanceTriggers().size());

        ModelPortfolioRebalanceTrigger rebalanceTrigger = rebalance.getRebalanceTriggers().get(0);
        assertEquals(new DateTime("2016-05-28"), rebalanceTrigger.getMostRecentTriggerDate());
        assertEquals("3", rebalanceTrigger.getTriggerType());
        assertEquals(Integer.valueOf(1), rebalanceTrigger.getTotalAccountsCount());
        assertEquals(Integer.valueOf(0), rebalanceTrigger.getTotalRebalancesCount());
        assertEquals(1, rebalanceTrigger.getRebalanceTriggerDetails().size());
        assertEquals(TriggerStatus.ORDERS_READY, rebalanceTrigger.getStatus());

        ModelPortfolioRebalanceTriggerDetails rebalanceTriggerDetails = rebalanceTrigger.getRebalanceTriggerDetails().get(0);
        assertEquals(Integer.valueOf(1), rebalanceTriggerDetails.getTotalAccountsCount());
        assertEquals(Integer.valueOf(0), rebalanceTriggerDetails.getTotalRebalancesCount());
        assertEquals(new DateTime("2016-05-28"), rebalanceTriggerDetails.getTranasactionDate());
        assertEquals("10", rebalanceTriggerDetails.getTrigger());
    }

    @Test
    public void testEmptyData() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource(
                "/webservices/response/modelportfolio/RebalanceSummaryResponseDataEmpty.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(ModelPortfolioRebalanceResponseImpl.class);
        ModelPortfolioRebalanceResponseImpl response = defaultResponseExtractor.extractData(content);

        assertNotNull(response);
        assertNotNull(response.getModelPortfolioRebalances());
        assertEquals(0, response.getModelPortfolioRebalances().size());
    }

    @Test
    public void testEmptyIpsList() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource(
                "/webservices/response/modelportfolio/RebalanceSummaryResponseIPSListEmpty.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(ModelPortfolioRebalanceResponseImpl.class);
        ModelPortfolioRebalanceResponseImpl response = defaultResponseExtractor.extractData(content);

        assertNotNull(response);
        assertNotNull(response.getModelPortfolioRebalances());
        assertEquals(0, response.getModelPortfolioRebalances().size());

    }

}