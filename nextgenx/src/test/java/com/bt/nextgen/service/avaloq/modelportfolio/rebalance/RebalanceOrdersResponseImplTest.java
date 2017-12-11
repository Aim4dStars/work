package com.bt.nextgen.service.avaloq.modelportfolio.rebalance;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.avaloq.client.BrokerKeyConverter;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceOrderDetails;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceOrderGroup;
import org.joda.time.DateTime;
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
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ParsingContext.class })
public class RebalanceOrdersResponseImplTest {

    @InjectMocks
    DefaultResponseExtractor<RebalanceOrdersResponseImpl> defaultResponseExtractor;

    @Mock
    ParsingContext parsingContext;

    @Mock
    ApplicationContext applicationContext;

    BigDecimalConverter bigDecimalConverter = new BigDecimalConverter();
    BrokerKeyConverter brokerKeyConverter = new BrokerKeyConverter();
    DateTimeTypeConverter dateTimeTypeConverter = new DateTimeTypeConverter();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ParsingContext.class);
        Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);
        Mockito.when(applicationContext.getBean(BigDecimalConverter.class)).thenReturn(bigDecimalConverter);
        Mockito.when(applicationContext.getBean(BrokerKeyConverter.class)).thenReturn(brokerKeyConverter);
        Mockito.when(applicationContext.getBean(DateTimeTypeConverter.class)).thenReturn(dateTimeTypeConverter);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testXPathMappings() throws Exception {

        final ClassPathResource responseResource = new ClassPathResource(
                "/webservices/response/modelportfolio/RebalanceOrdersResponse.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(RebalanceOrdersResponseImpl.class);
        RebalanceOrdersResponseImpl response = defaultResponseExtractor.extractData(content);

        Assert.assertNotNull(response);
        
        List<RebalanceOrderGroup> rebalanceGroupList = response.getRebalanceOrders();
        
        Assert.assertEquals(2, rebalanceGroupList.size());

        RebalanceOrderGroup rebalanceGroup = rebalanceGroupList.get(0);
        
        Assert.assertEquals("TMP Test", rebalanceGroup.getModelName());
        Assert.assertEquals("TMPTEST100", rebalanceGroup.getModelSymbol());
        Assert.assertEquals(BrokerKey.valueOf("100793"), rebalanceGroup.getAdviser());
        Assert.assertEquals(new DateTime("2016-03-07"), rebalanceGroup.getRebalanceDate());
        Assert.assertEquals("2930837", rebalanceGroup.getRebalDetDocId());
        
        List<RebalanceOrderDetails> orderDetailsList = rebalanceGroup.getOrderDetails();
                
        Assert.assertEquals(1, orderDetailsList.size());
        
        RebalanceOrderDetails orderDetails = orderDetailsList.get(0);
        
        Assert.assertEquals("134442", orderDetails.getAccount());
        Assert.assertEquals("110523", orderDetails.getAsset());
        Assert.assertEquals("test preference", orderDetails.getPreference());
        Assert.assertEquals(BigDecimal.valueOf(90), orderDetails.getModelWeight());
        Assert.assertEquals(BigDecimal.valueOf(90), orderDetails.getTargetWeight());
        Assert.assertEquals(BigDecimal.ZERO, orderDetails.getCurrentWeight());
        Assert.assertEquals(BigDecimal.valueOf(90), orderDetails.getDiffWeight());
        Assert.assertEquals(BigDecimal.valueOf(44999.98), orderDetails.getTargetValue());
        Assert.assertEquals(BigDecimal.ZERO, orderDetails.getCurrentValue());
        Assert.assertEquals(BigDecimal.valueOf(44999.98), orderDetails.getDiffValue());
        Assert.assertEquals(BigDecimal.valueOf(2709.21), orderDetails.getTargetQuantity());
        Assert.assertEquals(BigDecimal.ZERO, orderDetails.getCurrentQuantity());
        Assert.assertEquals(BigDecimal.valueOf(2709.21), orderDetails.getDiffQuantity());
        Assert.assertEquals(BigDecimal.valueOf(0.90), orderDetails.getFinalWeight());
        Assert.assertEquals(BigDecimal.valueOf(44996.49), orderDetails.getFinalValue());
        Assert.assertEquals(BigDecimal.valueOf(2709), orderDetails.getFinalQuantity());
        Assert.assertEquals("Buy", orderDetails.getOrderType());
        Assert.assertEquals(BigDecimal.valueOf(44996.49), orderDetails.getOrderValue());
        Assert.assertEquals(BigDecimal.valueOf(2709), orderDetails.getOrderQuantity());
        Assert.assertEquals("Good reason", orderDetails.getReasonForExclusion());
    }
}