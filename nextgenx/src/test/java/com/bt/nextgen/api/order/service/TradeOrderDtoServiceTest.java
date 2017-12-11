package com.bt.nextgen.api.order.service;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.api.order.model.OrderKey;
import com.bt.nextgen.api.order.model.TradeOrderDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.order.OrderDetailImpl;
import com.bt.nextgen.service.avaloq.order.OrderTransactionImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.order.OrderDetail;
import com.bt.nextgen.service.integration.order.OrderIntegrationService;
import com.bt.nextgen.service.integration.order.OrderTransaction;
import com.bt.nextgen.service.integration.order.OrderType;
import com.bt.nextgen.service.integration.order.PriceType;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class TradeOrderDtoServiceTest {
    @InjectMocks
    private TradeOrderDtoServiceImpl tradeOrderDtoService;

    @Mock
    private OrderIntegrationService orderService;

    @Mock
    private AssetIntegrationService assetService;

    @Mock
    private AssetDtoConverter assetConverter;

    OrderTransactionImpl orderTransaction1;
    OrderTransactionImpl orderTransaction2;
    OrderTransactionImpl orderTransaction3;
    OrderTransactionImpl orderTransaction4;

    List<OrderTransaction> orderTransactions;

    @Before
    public void setup() throws Exception {
        orderTransaction1 = new OrderTransactionImpl();
        orderTransaction1.setAccountId("12345");
        orderTransaction1.setConsideration(new BigDecimal(1200));
        orderTransaction1.setLimitPrice(new BigDecimal(1000));
        orderTransaction1.setFilledQuantity(Long.valueOf(100));
        orderTransaction1.setTransactionFee(new BigDecimal(15));
        orderTransaction1.setPriceType(PriceType.MARKET);
        orderTransaction1.setTradeDate(new DateTime());
        orderTransaction1.setSettlementDate(new DateTime());
        orderTransaction1.setAssetId("102233");
        orderTransaction1.setOrderType(OrderType.PARTIAL_REDEMPTION);
        orderTransaction1.setOriginalQuantity(BigDecimal.valueOf(100));
        orderTransaction1.setOrderId("103411");
        List<OrderDetail> details = new ArrayList<>();
        OrderDetailImpl detail1 = new OrderDetailImpl();
        detail1.setKey("price");
        detail1.setValue(new BigDecimal(40));
        OrderDetailImpl detail2 = new OrderDetailImpl();
        detail2.setKey("qty");
        detail2.setValue(new BigDecimal(100));
        details.add(detail1);
        details.add(detail2);
        orderTransaction1.setDetails(details);
        List<OrderTransaction> orderTransactions = new ArrayList<OrderTransaction>();
        orderTransactions.add(orderTransaction1);
        Mockito.when(orderService.loadTransactionData(Mockito.anyString(), Mockito.any(ServiceErrors.class)))
                .thenReturn(orderTransactions);
        AssetImpl asset = new AssetImpl();
        asset.setAssetId("102345");
        asset.setAssetCode("BHP");
        asset.setAssetName("BHP Billiton");
        asset.setAssetType(AssetType.SHARE);
        Mockito.when(assetService.loadAsset(Mockito.anyString(), Mockito.any(ServiceErrors.class))).thenReturn(asset);
        AssetDto assetDto = new AssetDto(asset, "BHP Billiton", AssetType.SHARE.getDisplayName());
        Mockito.when(assetConverter.toAssetDto(Mockito.any(Asset.class), Mockito.any(TermDepositAssetDetail.class)))
                .thenReturn(assetDto);
    }

    @Test
    public void testToTradeOrderDto() {
        OrderKey key = new OrderKey("103411");
        TradeOrderDto tradeOrderDto = tradeOrderDtoService.find(key, new ServiceErrorsImpl());
        Assert.assertEquals("Partial redemption", tradeOrderDto.getOrderType());
        Assert.assertEquals(PriceType.MARKET.getDisplayName(), tradeOrderDto.getPriceType());
        Assert.assertEquals(100, tradeOrderDto.getOriginalQuantity().longValue());
        Assert.assertEquals(1, tradeOrderDto.getOrderTransactions().size());
        Assert.assertEquals(new BigDecimal(1200), tradeOrderDto.getOrderTransactions().get(0).getConsideration());
        Assert.assertEquals(new BigDecimal(15), tradeOrderDto.getOrderTransactions().get(0).getTransactionFee());
        Assert.assertEquals(new BigDecimal(1200), tradeOrderDto.getSumTotalConsideration());
        Assert.assertEquals(new BigDecimal(100), tradeOrderDto.getTotalFilledUnits());
    }

    @Test
    public void testGetFilledQuantity_whenNoOrderDetail_thenZero() {
        BigDecimal filledQuantity = tradeOrderDtoService.getFilledQuantity(new OrderTransactionImpl());
        Assert.assertEquals(BigDecimal.ZERO, filledQuantity);
    }

    @Test
    public void testGetFilledQuantity_whenNoQty_thenZero() {
        OrderTransactionImpl transaction = new OrderTransactionImpl();
        List<OrderDetail> details = new ArrayList<>();
        details.add(new OrderDetailImpl());
        transaction.setDetails(details);
        BigDecimal filledQuantity = tradeOrderDtoService.getFilledQuantity(transaction);
        Assert.assertEquals(BigDecimal.ZERO, filledQuantity);
    }

    @Test
    public void testGetAveragePrice_whenNoOrderDetail_thenZero() {
        BigDecimal filledQuantity = tradeOrderDtoService.getAveragePrice(new OrderTransactionImpl());
        Assert.assertEquals(BigDecimal.ZERO, filledQuantity);
    }

    @Test
    public void testGetAveragePrice_whenNoPrice_thenZero() {
        OrderTransactionImpl transaction = new OrderTransactionImpl();
        List<OrderDetail> details = new ArrayList<>();
        details.add(new OrderDetailImpl());
        transaction.setDetails(details);
        BigDecimal filledQuantity = tradeOrderDtoService.getAveragePrice(transaction);
        Assert.assertEquals(BigDecimal.ZERO, filledQuantity);
    }
}
