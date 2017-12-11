package com.bt.nextgen.reports.account.investmentorders.amendorder;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.order.model.OrderKey;
import com.bt.nextgen.api.order.model.ShareOrderDto;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.service.avaloq.asset.ShareAssetImpl;
import com.bt.nextgen.service.integration.asset.AssetCluster;
import com.bt.nextgen.service.integration.asset.AssetStatus;
import com.bt.nextgen.service.integration.order.Order;
import com.bt.nextgen.service.integration.order.OrderIntegrationService;
import com.bt.nextgen.service.integration.order.PriceType;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AmendOrderReportTest {

    @InjectMocks
    private AmendOrderReport amendOrderReport;

    @Mock
    private OrderIntegrationService orderIntegrationService;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private CmsService cmsService;

    List<Order> orderList;
    private String accountId;
    private String orderId;
    private OrderKey key;
    private ShareOrderDto shareOrderDto;
    private ShareOrderDto shareOrderDto2;

    private ShareAssetImpl shareAsset;
    private ShareAssetImpl shareAsset2;

    @Before
    public void setup() {
        accountId = "975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0";
        orderId = "12356";
        key = new OrderKey("12356");

        shareAsset = new ShareAssetImpl();
        shareAsset.setAssetId("12345");
        shareAsset.setAssetCode("bhp");
        shareAsset.setAssetName("BHP Billiton");
        shareAsset.setAssetType(AssetType.SHARE);
        shareAsset.setAssetCluster(AssetCluster.AUSTRALIAN_LISTED_SECURITIES);
        shareAsset.setStatus(AssetStatus.OPEN);

        shareAsset2 = new ShareAssetImpl();
        shareAsset2.setAssetId("456");
        shareAsset2.setAssetName("Westpac");
        shareAsset2.setAssetType(AssetType.SHARE);
        shareAsset2.setAssetCluster(AssetCluster.AUSTRALIAN_LISTED_SECURITIES);
        shareAsset2.setStatus(AssetStatus.OPEN);

        Mockito.when(cmsService.getContent(Mockito.anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return (String) invocation.getArguments()[0];
            }
        });

        Order order1 = mock(Order.class);
        when(order1.getAccountId()).thenReturn("12345");
        orderList = new ArrayList<>();
        orderList.add(order1);

        shareOrderDto = mockShareOrderDtoService(shareAsset, PriceType.MARKET, "GFD");
        shareOrderDto2 = mockShareOrderDtoService(shareAsset2, PriceType.LIMIT, "GTC");

        when(orderIntegrationService.loadOrder(Mockito.anyString(), any(ServiceErrorsImpl.class))).thenReturn(orderList);
    }

    private ShareOrderDto mockShareOrderDtoService(ShareAssetImpl asset, PriceType priceType, String expiryType) {
        AssetDto assetDto = new AssetDto(asset, asset.getAssetName(), asset.getAssetType().getDisplayName());
        ShareOrderDto shareOrderDto = new ShareOrderDto();
        shareOrderDto.setKey(key);
        shareOrderDto.setStatus("Cancelled");
        shareOrderDto.setLastTranSeqId("13245");
        shareOrderDto.setCancellable(true);
        shareOrderDto.setOrderType("Buy");
        shareOrderDto.setAsset(assetDto);
        shareOrderDto.setDisplayOrderId("12144321");
        shareOrderDto.setQuantity(BigDecimal.valueOf(50));
        shareOrderDto.setFilledQuantity(Integer.valueOf(20));
        shareOrderDto.setPriceType(priceType.getDisplayName());
        shareOrderDto.setLimitPrice(BigDecimal.valueOf(23.456));
        shareOrderDto.setExpiryType(expiryType);

        return shareOrderDto;
    }

    @Test
    public void testGetData_whenMarketShareTrade_thenValuesMatch() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("order", "");
        Map<String, Object> dataCollections = new HashMap();
        Mockito.when(mapper.readValue(Mockito.any(String.class), Mockito.any(Class.class))).thenReturn(shareOrderDto);

        List<AmendOrderReportData> resultDto = new ArrayList(amendOrderReport.getData(params, dataCollections));
        Assert.assertNotNull(resultDto);
        Assert.assertFalse(resultDto.isEmpty());
        Assert.assertEquals(resultDto.size(), 1);

        AmendOrderReportData reportData = resultDto.get(0);
        Assert.assertEquals(shareOrderDto.getAsset().getAssetCode() + " &#183 " + shareOrderDto.getAsset().getAssetName(),
                reportData.getAssetName());
        Assert.assertEquals(shareOrderDto.getDisplayOrderId(), reportData.getDisplayOrderId());
        Assert.assertEquals(shareOrderDto.getOrderType(), reportData.getOrderType());
        Assert.assertEquals(
                ReportFormatter.format(ReportFormat.INTEGER,
                        shareOrderDto.getQuantity().subtract(BigDecimal.valueOf(shareOrderDto.getFilledQuantity()))) + " units",
                reportData.getUnfilledAmount());
        Assert.assertEquals(
                "(" + ReportFormatter.format(ReportFormat.INTEGER, shareOrderDto.getFilledQuantity()) + " of "
                        + ReportFormatter.format(ReportFormat.INTEGER, shareOrderDto.getQuantity()) + " units already filled)",
                reportData.getFilledAmount());
        Assert.assertEquals(shareOrderDto.getPriceType(), reportData.getPriceType());
        Assert.assertEquals("-", reportData.getLimitPrice());
        Assert.assertEquals("Good for day (GFD)", reportData.getExpiry());
    }

    @Test
    public void testGetData_whenLimitShareTrade_thenValuesMatch() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("order", "");
        Map<String, Object> dataCollections = new HashMap();
        Mockito.when(mapper.readValue(Mockito.any(String.class), Mockito.any(Class.class))).thenReturn(shareOrderDto2);

        List<AmendOrderReportData> resultDto = new ArrayList(amendOrderReport.getData(params, dataCollections));
        Assert.assertNotNull(resultDto);
        Assert.assertFalse(resultDto.isEmpty());
        Assert.assertEquals(resultDto.size(), 1);

        AmendOrderReportData reportData = resultDto.get(0);
        Assert.assertEquals(shareOrderDto2.getAsset().getAssetName(), reportData.getAssetName());
        Assert.assertEquals(shareOrderDto2.getDisplayOrderId(), reportData.getDisplayOrderId());
        Assert.assertEquals(shareOrderDto2.getOrderType(), reportData.getOrderType());
        Assert.assertEquals(
                ReportFormatter.format(ReportFormat.INTEGER,
                        shareOrderDto2.getQuantity().subtract(BigDecimal.valueOf(shareOrderDto2.getFilledQuantity()))) + " units",
                reportData.getUnfilledAmount());
        Assert.assertEquals(
                "(" + ReportFormatter.format(ReportFormat.INTEGER, shareOrderDto2.getFilledQuantity()) + " of "
                        + ReportFormatter.format(ReportFormat.INTEGER, shareOrderDto2.getQuantity()) + " units already filled)",
                reportData.getFilledAmount());
        Assert.assertEquals(shareOrderDto2.getPriceType(), reportData.getPriceType());
        Assert.assertEquals(ReportFormatter.format(ReportFormat.LS_PRICE, shareOrderDto2.getLimitPrice()),
                reportData.getLimitPrice());
        Assert.assertEquals("Good till cancel (GTC)", reportData.getExpiry());
    }

    @Test
    public void testGetData_whenLimitTypeAndNoLimitPrice_thenNoLimitPriceReturned() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("order", "");
        Map<String, Object> dataCollections = new HashMap();
        ShareOrderDto limitOrderDto = new ShareOrderDto();
        limitOrderDto.setPriceType(PriceType.LIMIT.getDisplayName());
        Mockito.when(mapper.readValue(Mockito.any(String.class), Mockito.any(Class.class))).thenReturn(limitOrderDto);

        List<AmendOrderReportData> resultDto = new ArrayList(amendOrderReport.getData(params, dataCollections));
        AmendOrderReportData reportData = resultDto.get(0);
        Assert.assertEquals("-", reportData.getLimitPrice());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetData_whenJsonError_thenIllegalArgumentExceptionThrown() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("order", "{{{{");
        Map<String, Object> dataCollections = new HashMap();
        Mockito.when(mapper.readValue(Mockito.any(String.class), Mockito.any(Class.class)))
                .thenThrow(new IOException("Intentional exception"));

        List<AmendOrderReportData> resultDto = new ArrayList(amendOrderReport.getData(params, dataCollections));
    }

    @Test
    public void testGetDisclaimer() {
        String disclaimer = amendOrderReport.getDisclaimer(null, null);
        Assert.assertEquals("DS-IP-0146", disclaimer);
    }

    @Test
    public void testGetDeclaration() {
        String declaration = amendOrderReport.getDeclaration(null, null);
        Assert.assertEquals("DS-IP-0084", declaration);
    }

    @Test
    public void testAccountInformationWithAccount() {
        Map<String, Object> params = new HashMap<>();
        params.put("account-id", accountId);
        params.put("order-id", orderId);

        com.bt.nextgen.service.integration.account.AccountKey accountKey = amendOrderReport.getAccountKey(params);
        Assert.assertEquals(EncodedString.toPlainText(accountId), accountKey.getId());
    }

    @Test
    public void testAccountInformationWithoutAccount() {
        Map<String, Object> params = new HashMap<>();
        params.put("order-id", orderId);

        com.bt.nextgen.service.integration.account.AccountKey accountKey = amendOrderReport.getAccountKey(params);
        Assert.assertEquals(orderList.get(0).getAccountId(), accountKey.getId());
    }

    @Test
    public void testReportName() {
        Map<String, String> params = new HashMap<>();
        String reportName = amendOrderReport.getReportName(params);
        Assert.assertEquals("Your client authorisation for an amended order", reportName);
    }

    @Test
    public void testReportTitle() {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> dataCollections = new HashMap<>();
        String reportTitle = amendOrderReport.getReportTitle(params, dataCollections);
        Assert.assertEquals("Your client authorisation for an amended order", reportTitle);
    }

    @Test
    public void testReportType() {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> dataCollections = new HashMap<>();
        String reportType = amendOrderReport.getReportType(params, dataCollections);
        Assert.assertEquals("Your client authorisation for an amended order", reportType);
    }
}
