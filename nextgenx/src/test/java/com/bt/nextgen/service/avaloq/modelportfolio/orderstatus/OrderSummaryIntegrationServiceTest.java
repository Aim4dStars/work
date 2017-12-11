package com.bt.nextgen.service.avaloq.modelportfolio.orderstatus;

import com.bt.nextgen.core.conversion.BigIntegerConverter;
import com.bt.nextgen.integration.xml.converter.CodeConverter;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.avaloq.client.BrokerKeyConverter;
import com.bt.nextgen.service.integration.modelportfolio.orderstatus.ModelOrderDetails;
import com.bt.nextgen.service.integration.modelportfolio.orderstatus.ModelOrderSummaryResponse;
import com.bt.nextgen.service.integration.order.ExpiryMethod;
import com.bt.nextgen.service.integration.order.OrderStatus;
import com.bt.nextgen.service.integration.transactionfee.ExecutionType;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import com.btfin.panorama.service.integration.order.OrderType;
import org.exolab.castor.types.DateTime;
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
public class OrderSummaryIntegrationServiceTest {

    @InjectMocks
    DefaultResponseExtractor<OrderSummaryResponseImpl> defaultResponseExtractor;

    @Mock
    ParsingContext parsingContext;

    @Mock
    ApplicationContext applicationContext;

    @Mock
    CodeConverter codeConverter;

    DateTimeTypeConverter dateTimeTypeConverter = new DateTimeTypeConverter();

    BigIntegerConverter bigIntegerConverter = new BigIntegerConverter();

    BrokerKeyConverter brokerKeyConverter = new BrokerKeyConverter();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ParsingContext.class);
        Mockito.when(ParsingContext.getApplicationContext()).thenReturn(applicationContext);
        Mockito.when(applicationContext.getBean(CodeConverter.class)).thenReturn(codeConverter);
        Mockito.when(applicationContext.getBean(DateTimeTypeConverter.class)).thenReturn(dateTimeTypeConverter);
        Mockito.when(applicationContext.getBean(BigIntegerConverter.class)).thenReturn(bigIntegerConverter);
        Mockito.when(applicationContext.getBean(BrokerKeyConverter.class)).thenReturn(brokerKeyConverter);

        Mockito.when(codeConverter.convert("121", "ORDER_STATUS")).thenReturn("mp_stex_compl");
        Mockito.when(codeConverter.convert("141", "ORDER_STATUS")).thenReturn("unsuccessful");
        Mockito.when(codeConverter.convert("800", "ORDER_TYPE")).thenReturn("stex_buy");
        Mockito.when(codeConverter.convert("1", "EXECUTION_TYPE")).thenReturn("dma");
        Mockito.when(codeConverter.convert("1", "EXPIRY_METHOD")).thenReturn("good_for_day");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testXPathMappings() throws Exception {

        final ClassPathResource responseResource = new ClassPathResource(
                "/webservices/response/modelportfolio/orderstatus/ModelOrderSummaryResponse.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(OrderSummaryResponseImpl.class);
        ModelOrderSummaryResponse response = defaultResponseExtractor.extractData(content);

        Assert.assertNotNull(response);
        List <ModelOrderDetails> detailList = response.getOrderDetails();
        Assert.assertNotNull(detailList);
        Assert.assertTrue(detailList.size() == 116);
        ModelOrderDetails orderDetail = detailList.get(0);

        Assert.assertEquals("ANZ", orderDetail.getAssetCode());
        Assert.assertEquals("110744", orderDetail.getAssetId());
        Assert.assertEquals("ANZ Banking Grp Ltd", orderDetail.getAssetName());
        Assert.assertEquals("1177941", orderDetail.getAccountNumber());
        Assert.assertEquals("120131289", orderDetail.getAccountName());
        Assert.assertEquals("10913569", orderDetail.getDocId());
        Assert.assertEquals(426, orderDetail.getOriginalQuantity().intValue());
        Assert.assertEquals(426, orderDetail.getFillQuantity().intValue());
        Assert.assertEquals(0, orderDetail.getRemainingQuantity().intValue());
        Assert.assertEquals(OrderStatus.MP_STEX_COMPLETE, orderDetail.getStatus());
        Assert.assertEquals(DateTime.parse("2017-07-19T10:36:48+10:00").toDate(), orderDetail.getOrderDate().toDate());
        Assert.assertEquals(DateTime.parse("2017-07-19T00:00:00+10:00").toDate(), orderDetail.getTransactionDate().toDate());
        Assert.assertEquals(DateTime.parse("2017-07-19T15:59:59+10:00").toDate(), orderDetail.getExpiryDate().toDate());
        Assert.assertEquals("1039950", orderDetail.getIpsId());
        Assert.assertEquals("Lanyon Black Core", orderDetail.getIpsName());
        Assert.assertEquals("CORE", orderDetail.getIpsKey());

        // static field mapping
        Assert.assertEquals(OrderType.STEX_BUY, orderDetail.getOrderType());
        Assert.assertEquals(ExecutionType.DIRECT_MARKET_ACCESS, orderDetail.getExecType());
        Assert.assertEquals(ExpiryMethod.GFD, orderDetail.getExpiryType());

        Assert.assertEquals(BigDecimal.valueOf(-12549.65d), orderDetail.getNetAmount());
        Assert.assertEquals(BigDecimal.valueOf(29.4269d), orderDetail.getEstimatedPrice());
        Assert.assertEquals(BigDecimal.valueOf(-13.79d), orderDetail.getBrokerage());
        Assert.assertEquals("Scott  Fisher", orderDetail.getAdviserName());
        Assert.assertEquals("Lanyon Partners Private Wealth P/L", orderDetail.getDealerName());
        
        
        ModelOrderDetails orderDetailUnsuccessful = detailList.get(1);
        Assert.assertEquals(OrderStatus.UNSUCCESSFUL, orderDetailUnsuccessful.getStatus());
    }
}