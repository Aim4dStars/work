package com.bt.nextgen.service.avaloq.modelportfolio.orderstatus;

import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.modelportfolio.orderstatus.ModelOrderSummaryResponse;
import com.bt.nextgen.service.integration.order.ExpiryMethod;
import com.bt.nextgen.service.integration.order.OrderStatus;
import com.bt.nextgen.service.integration.transactionfee.ExecutionType;
import com.bt.nextgen.service.request.AvaloqRequest;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.order.OrderType;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ParsingContext.class })
public class OrderSummaryIntegrationServicesTest {

    @InjectMocks
    OrderSummaryIntegrationServiceImpl orderSummaryService;

    @Mock
    private AvaloqReportService avaloqService;


    @Before
    public void setUp() throws Exception {
        OrderSummaryResponseImpl response = new OrderSummaryResponseImpl();
        response.setOrderDetails(null);
        Mockito.when(
                avaloqService.executeReportRequestToDomain(Mockito.any(AvaloqRequest.class), Mockito.any(Class.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(response);
    }

    @Test
    public void testRetrieveOrderSummary() throws Exception {
        
        ModelOrderSummaryResponse results = orderSummaryService.loadOrderStatusSummary(BrokerKey.valueOf("brokerId"),
                DateTime.now(), new ServiceErrorsImpl());
        
        Assert.assertNotNull(results);
    }


    @Test
    public void testModelSetter() throws Exception {
        ModelOrderDetailsImpl order = new ModelOrderDetailsImpl();
        order.setAssetCode("assetcode");
        order.setAssetId("assetId");
        order.setIpsId("ipsId");
        order.setIpsKey("ipsKey");
        order.setIpsName("ipsName");
        order.setAccountNumber("accountNumber");
        order.setAccountName("accountName");
        order.setDocId("docId");
        order.setOrderType(OrderType.INCREASE);
        order.setExecType(ExecutionType.DIRECT_MARKET_ACCESS);
        order.setExpiryType(ExpiryMethod.GFD);
        order.setOriginalQuantity(BigDecimal.ONE);
        order.setFillQuantity(BigDecimal.ONE);
        order.setRemainingQuantity(BigDecimal.ZERO);
        order.setStatus(OrderStatus.MP_STEX_COMPLETE);
        order.setOrderDate(DateTime.now());
        order.setTransactionDate(DateTime.now());
        order.setExpiryDate(DateTime.now());
        order.setNetAmount(BigDecimal.ONE);
        order.setEstimatedPrice(BigDecimal.ONE);
        order.setBrokerage(BigDecimal.ZERO);
        order.setAdviserName("adviserName");
        order.setDealerName("dealerName");


        Assert.assertEquals("assetcode", order.getAssetCode());
        Assert.assertEquals("assetId", order.getAssetId());
        Assert.assertEquals("ipsId", order.getIpsId());
        Assert.assertEquals("ipsKey", order.getIpsKey());
        Assert.assertEquals("ipsName", order.getIpsName());
        Assert.assertEquals("accountNumber", order.getAccountNumber());
        Assert.assertEquals("accountName", order.getAccountName());
        Assert.assertEquals("docId", order.getDocId());
        Assert.assertEquals(OrderType.INCREASE, order.getOrderType());
        Assert.assertEquals(ExecutionType.DIRECT_MARKET_ACCESS, order.getExecType());
        Assert.assertEquals(ExpiryMethod.GFD, order.getExpiryType());
        Assert.assertEquals(BigDecimal.ONE, order.getOriginalQuantity());
        Assert.assertEquals(BigDecimal.ONE, order.getFillQuantity());
        Assert.assertEquals(BigDecimal.ZERO, order.getRemainingQuantity());
        Assert.assertEquals(OrderStatus.MP_STEX_COMPLETE, order.getStatus());
        Assert.assertNotNull(order.getOrderDate());
        Assert.assertNotNull(order.getTransactionDate());
        Assert.assertNotNull(order.getExpiryDate());
        Assert.assertEquals(BigDecimal.ONE, order.getNetAmount());
        Assert.assertEquals(BigDecimal.ONE, order.getEstimatedPrice());
        Assert.assertEquals(BigDecimal.ZERO, order.getBrokerage());
        Assert.assertEquals("adviserName", order.getAdviserName());
        Assert.assertEquals("dealerName", order.getDealerName());
    }
}