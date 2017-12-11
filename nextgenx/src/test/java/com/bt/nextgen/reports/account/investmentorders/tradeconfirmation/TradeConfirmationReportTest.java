package com.bt.nextgen.reports.account.investmentorders.tradeconfirmation;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.account.v3.model.WrapAccountDetailDto;
import com.bt.nextgen.api.account.v3.service.WrapAccountDetailDtoService;
import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.order.model.OrderKey;
import com.bt.nextgen.api.order.model.OrderTransactionDto;
import com.bt.nextgen.api.order.model.TradeOrderDto;
import com.bt.nextgen.api.order.service.TradeOrderDtoService;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.asset.ShareAssetImpl;
import com.bt.nextgen.service.avaloq.order.OrderDetailImpl;
import com.bt.nextgen.service.avaloq.order.OrderTransactionImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.integration.asset.AssetCluster;
import com.bt.nextgen.service.integration.asset.AssetStatus;
import com.btfin.panorama.service.integration.asset.AssetType;
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
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TradeConfirmationReportTest {

    private String accountId;
    private String orderId;
    private OrderKey key;
    private WrapAccountDetailDto accountDetailDto;
    private OrderTransactionImpl transactionOrder;
    private List<OrderTransaction> transactionOrders;
    private TradeOrderDto tradeOrderDto;
    private TradeOrderDto tradeOrderDto2;
    private TradeOrderDto mfTradeOrderDto;

    @InjectMocks
    private TradeConfirmationReportLS tradeConfirmationReport;

    @InjectMocks
    private TradeConfirmationReportMF tradeConfirmationReportMF;

    @Mock
    private WrapAccountDetailDtoService accountDetailService;

    @Mock
    private OrderIntegrationService orderIntegrationService;

    @Mock
    private TradeOrderDtoService tradeOrderDtoService;

    @Mock
    private ContentDtoService contentService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private CmsService cmsService;

    private ShareAssetImpl mfAsset;
    private ShareAssetImpl shareAsset;
    private ShareAssetImpl shareAsset2;

    @Before
    public void setup() {
        accountId = "975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0";
        orderId = "12356";
        key = new OrderKey("12356");

        mfAsset = new ShareAssetImpl();
        mfAsset.setAssetId("789");
        mfAsset.setAssetCode("abc");
        mfAsset.setAssetName("Aberdeen something or other");
        mfAsset.setAssetType(AssetType.MANAGED_FUND);
        mfAsset.setAssetCluster(AssetCluster.MANAGED_FUND);
        mfAsset.setStatus(AssetStatus.OPEN);

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

        // Mock content service
        ContentDto content = new ContentDto(new ContentKey("MockKey"), "MockString");
        when(contentService.find((any(ContentKey.class)), any(ServiceErrorsImpl.class))).thenReturn(content);

        accountDetailDto = mockWrapAccountDto();
        transactionOrders = mockTransactionOrders(PriceType.MARKET);

        tradeOrderDto = mockTradeOrderDtoService(shareAsset, PriceType.MARKET);
        tradeOrderDto2 = mockTradeOrderDtoService(shareAsset2, PriceType.LIMIT);
        mfTradeOrderDto = mockTradeOrderDtoService(mfAsset, PriceType.MARKET);

        when(tradeOrderDtoService.find((Matchers.any(OrderKey.class)), any(ServiceErrorsImpl.class)))
                .thenAnswer(new Answer<TradeOrderDto>() {
                    @Override
                    public TradeOrderDto answer(InvocationOnMock invocation) throws Throwable {
                        Object[] args = invocation.getArguments();
                        if ("45678".equals(((OrderKey) args[0]).getOrderId())) {
                            return tradeOrderDto2;
                        } else if ("mf123789".equals(((OrderKey) args[0]).getOrderId())) {
                            return mfTradeOrderDto;
                        }
                        return tradeOrderDto;
                    }
                });
        when(accountDetailService.search((Matchers.any(List.class)), any(ServiceErrorsImpl.class))).thenReturn(accountDetailDto);
        when(orderIntegrationService.loadTransactionData(Mockito.anyString(), any(ServiceErrorsImpl.class)))
                .thenReturn(transactionOrders);
    }

    public TradeOrderDto mockTradeOrderDtoService(ShareAssetImpl asset, PriceType priceType) {
        List<OrderTransactionDto> orderTransactions = new ArrayList<OrderTransactionDto>();
        AssetDto assetDto = new AssetDto(asset, asset.getAssetName(), asset.getAssetType().getDisplayName());
        OrderTransactionDto orderTransaction = new OrderTransactionDto(new DateTime(), new BigDecimal(20), new BigDecimal(25.90),
                new BigDecimal(15), new BigDecimal(2100), new DateTime());
        orderTransactions.add(orderTransaction);
        List<OrderTransaction> transactionOrders = mockTransactionOrders(priceType);
        TradeOrderDto tradeOrderDto = new TradeOrderDto(key, "buy", assetDto, true, transactionOrders.get(0), orderTransactions);
        return tradeOrderDto;
    }

    public List<OrderTransaction> mockTransactionOrders(PriceType priceType) {
        List<OrderTransaction> transactionOrders = new ArrayList<>();
        transactionOrder = new OrderTransactionImpl();
        transactionOrder.setAccountId(accountId);
        transactionOrder.setAssetId("12345");
        transactionOrder.setFilledQuantity(60L);
        transactionOrder.setConsideration(new BigDecimal(1000));
        transactionOrder.setOrderType(OrderType.PARTIAL_REDEMPTION);
        transactionOrder.setOrderId(orderId);
        transactionOrder.setTransactionFee(new BigDecimal(15));
        transactionOrder.setTradeDate(new DateTime());
        transactionOrder.setPriceType(priceType);
        transactionOrder.setLimitPrice(BigDecimal.valueOf(23.456));
        transactionOrder.setCancellable(true);
        transactionOrder.setSettlementDate(new DateTime());
        transactionOrder.setOriginalQuantity(BigDecimal.valueOf(200));
        List<OrderDetail> details = new ArrayList<>();
        OrderDetailImpl quantity = new OrderDetailImpl();
        quantity.setKey("qty");
        quantity.setValue(BigDecimal.valueOf(538));
        OrderDetailImpl price = new OrderDetailImpl();
        price.setKey("price");
        price.setValue(BigDecimal.valueOf(24.678));
        details.add(quantity);
        transactionOrder.setDetails(details);
        transactionOrders.add(transactionOrder);
        return transactionOrders;
    }

    public WrapAccountDetailDto mockWrapAccountDto() {
        WrapAccountDetailDto accountDetailDto = new WrapAccountDetailDto(new AccountKey(accountId), new DateTime(),
                new DateTime());
        accountDetailDto.setAccountName("account name");
        accountDetailDto.setAccountNumber("45767677");
        accountDetailDto.setBsb("010567");
        accountDetailDto.setAccountType("savings");
        return accountDetailDto;
    }

    @Test
    public void testGetData_whenManagedFundTrade_thenValuesMatch() {
        Map<String, Object> params = new HashMap<>();
        params.put(UriMappingConstants.ORDER_ID_URI_MAPPING, "mf123789");
        Map<String, Object> dataCollections = new HashMap();

        List<TradeConfirmationReportData> resultDto = new ArrayList(tradeConfirmationReportMF.getData(params, dataCollections));
        Assert.assertNotNull(resultDto);
        Assert.assertFalse(resultDto.isEmpty());
        Assert.assertEquals(resultDto.size(), 1);

        TradeConfirmationReportData reportData = resultDto.get(0);
        Assert.assertEquals(mfTradeOrderDto.getAsset().getAssetCode() + " &#183 " + mfTradeOrderDto.getAsset().getAssetName(),
                reportData.getAssetName());
        Assert.assertEquals(mfTradeOrderDto.getKey().getOrderId(), reportData.getOrderItemNumber());
        Assert.assertEquals(mfTradeOrderDto.getOrderType(), reportData.getOrderType());
        Assert.assertEquals(ReportFormatter.format(ReportFormat.INTEGER, mfTradeOrderDto.getOriginalQuantity()),
                reportData.getOriginalQuantity());
        Assert.assertEquals(ReportFormatter.format(ReportFormat.INTEGER, mfTradeOrderDto.getOutstandingUnits()),
                reportData.getOutstandingUnits());
        Assert.assertEquals(mfTradeOrderDto.getPriceType(), reportData.getPriceOption());
        Assert.assertEquals(ReportFormatter.format(ReportFormat.CURRENCY, mfTradeOrderDto.getSumTotalConsideration()),
                reportData.getSumTotalConsideration());
        Assert.assertEquals(ReportFormatter.format(ReportFormat.INTEGER, mfTradeOrderDto.getTotalFilledUnits()),
                reportData.getTotalFilledUnits());
        Assert.assertEquals(mfTradeOrderDto.getOrderTransactions().size(), reportData.getChildren().size());
        TradeConfirmationItemData child = reportData.getChildren().get(0);
        Assert.assertEquals(
                ReportFormatter.format(ReportFormat.CURRENCY, mfTradeOrderDto.getOrderTransactions().get(0).getConsideration()),
                child.getConsideration());
        Assert.assertEquals(
                ReportFormatter.format(ReportFormat.MANAGED_FUND_PRICE, mfTradeOrderDto.getOrderTransactions().get(0).getPrice()),
                child.getPrice());
        Assert.assertEquals(ReportFormatter.format(ReportFormat.SHORT_DATE,
                mfTradeOrderDto.getOrderTransactions().get(0).getSettlementDate()), child.getSettlementDate());
        Assert.assertEquals(
                ReportFormatter.format(ReportFormat.SHORT_DATE, mfTradeOrderDto.getOrderTransactions().get(0).getTradeDate()),
                child.getTradeDate());
        Assert.assertEquals(
                ReportFormatter.format(ReportFormat.CURRENCY, mfTradeOrderDto.getOrderTransactions().get(0).getTransactionFee()),
                child.getTransactionFee());
        Assert.assertEquals(
                ReportFormatter.format(ReportFormat.MANAGED_FUND_UNIT, mfTradeOrderDto.getOrderTransactions().get(0).getUnits()),
                child.getUnits());
    }

    @Test
    public void testGetData_whenMarketShareTrade_thenValuesMatch() {
        Map<String, Object> params = new HashMap<>();
        params.put(UriMappingConstants.ORDER_ID_URI_MAPPING, orderId);
        Map<String, Object> dataCollections = new HashMap();

        List<TradeConfirmationReportData> resultDto = new ArrayList(tradeConfirmationReport.getData(params, dataCollections));
        Assert.assertNotNull(resultDto);
        Assert.assertFalse(resultDto.isEmpty());
        Assert.assertEquals(resultDto.size(), 1);

        TradeConfirmationReportData reportData = resultDto.get(0);
        Assert.assertEquals(tradeOrderDto.getAsset().getAssetCode() + " &#183 " + tradeOrderDto.getAsset().getAssetName(),
                reportData.getAssetName());
        Assert.assertEquals(tradeOrderDto.getKey().getOrderId(), reportData.getOrderItemNumber());
        Assert.assertEquals(tradeOrderDto.getOrderType(), reportData.getOrderType());
        Assert.assertEquals(ReportFormatter.format(ReportFormat.INTEGER, tradeOrderDto.getOriginalQuantity()),
                reportData.getOriginalQuantity());
        Assert.assertEquals(ReportFormatter.format(ReportFormat.INTEGER, tradeOrderDto.getOutstandingUnits()),
                reportData.getOutstandingUnits());
        Assert.assertEquals(tradeOrderDto.getPriceType(), reportData.getPriceOption());
        Assert.assertEquals(ReportFormatter.format(ReportFormat.CURRENCY, tradeOrderDto.getSumTotalConsideration()),
                reportData.getSumTotalConsideration());
        Assert.assertEquals(ReportFormatter.format(ReportFormat.INTEGER, tradeOrderDto.getTotalFilledUnits()),
                reportData.getTotalFilledUnits());
        Assert.assertEquals(tradeOrderDto.getOrderTransactions().size(), reportData.getChildren().size());
        TradeConfirmationItemData child = reportData.getChildren().get(0);
        Assert.assertEquals(
                ReportFormatter.format(ReportFormat.CURRENCY, tradeOrderDto.getOrderTransactions().get(0).getConsideration()),
                child.getConsideration());
        Assert.assertEquals(ReportFormatter.format(ReportFormat.LS_PRICE, tradeOrderDto.getOrderTransactions().get(0).getPrice()),
                child.getPrice());
        Assert.assertEquals(
                ReportFormatter.format(ReportFormat.SHORT_DATE, tradeOrderDto.getOrderTransactions().get(0).getSettlementDate()),
                child.getSettlementDate());
        Assert.assertEquals(
                ReportFormatter.format(ReportFormat.SHORT_DATE, tradeOrderDto.getOrderTransactions().get(0).getTradeDate()),
                child.getTradeDate());
        Assert.assertEquals(
                ReportFormatter.format(ReportFormat.CURRENCY, tradeOrderDto.getOrderTransactions().get(0).getTransactionFee()),
                child.getTransactionFee());
        Assert.assertEquals(ReportFormatter.format(ReportFormat.INTEGER, tradeOrderDto.getOrderTransactions().get(0).getUnits()),
                child.getUnits());
    }

    @Test
    public void testGetData_whenLimitShareTrade_thenValuesMatch() {
        Map<String, Object> params = new HashMap<>();
        params.put(UriMappingConstants.ORDER_ID_URI_MAPPING, "45678");
        Map<String, Object> dataCollections = new HashMap();

        List<TradeConfirmationReportData> resultDto = new ArrayList(tradeConfirmationReport.getData(params, dataCollections));
        Assert.assertNotNull(resultDto);
        Assert.assertFalse(resultDto.isEmpty());
        Assert.assertEquals(resultDto.size(), 1);

        TradeConfirmationReportData reportData = resultDto.get(0);
        Assert.assertEquals(tradeOrderDto2.getAsset().getAssetName(), reportData.getAssetName());
        Assert.assertEquals(tradeOrderDto2.getKey().getOrderId(), reportData.getOrderItemNumber());
        Assert.assertEquals(tradeOrderDto2.getOrderType(), reportData.getOrderType());
        Assert.assertEquals(ReportFormatter.format(ReportFormat.INTEGER, tradeOrderDto2.getOriginalQuantity()),
                reportData.getOriginalQuantity());
        Assert.assertEquals(ReportFormatter.format(ReportFormat.INTEGER, tradeOrderDto2.getOutstandingUnits()),
                reportData.getOutstandingUnits());
        Assert.assertEquals("Limit - " + ReportFormatter.format(ReportFormat.LS_PRICE, tradeOrderDto2.getLimitPrice()),
                reportData.getPriceOption());
        Assert.assertEquals(ReportFormatter.format(ReportFormat.CURRENCY, tradeOrderDto2.getSumTotalConsideration()),
                reportData.getSumTotalConsideration());
        Assert.assertEquals(ReportFormatter.format(ReportFormat.INTEGER, tradeOrderDto2.getTotalFilledUnits()),
                reportData.getTotalFilledUnits());
        Assert.assertEquals(tradeOrderDto2.getOrderTransactions().size(), reportData.getChildren().size());
        TradeConfirmationItemData child = reportData.getChildren().get(0);
        Assert.assertEquals(
                ReportFormatter.format(ReportFormat.CURRENCY, tradeOrderDto2.getOrderTransactions().get(0).getConsideration()),
                child.getConsideration());
        Assert.assertEquals(
                ReportFormatter.format(ReportFormat.LS_PRICE, tradeOrderDto2.getOrderTransactions().get(0).getPrice()),
                child.getPrice());
        Assert.assertEquals(
                ReportFormatter.format(ReportFormat.SHORT_DATE, tradeOrderDto2.getOrderTransactions().get(0).getSettlementDate()),
                child.getSettlementDate());
        Assert.assertEquals(
                ReportFormatter.format(ReportFormat.SHORT_DATE, tradeOrderDto2.getOrderTransactions().get(0).getTradeDate()),
                child.getTradeDate());
        Assert.assertEquals(
                ReportFormatter.format(ReportFormat.CURRENCY, tradeOrderDto2.getOrderTransactions().get(0).getTransactionFee()),
                child.getTransactionFee());
        Assert.assertEquals(ReportFormatter.format(ReportFormat.INTEGER, tradeOrderDto2.getOrderTransactions().get(0).getUnits()),
                child.getUnits());
    }

    @Test
    public void testGetDisclaimerContentLS() {
        String disclaimer = tradeConfirmationReport.getDisclaimerContent();
        Assert.assertEquals("DS-IP-0077", disclaimer);
    }

    @Test
    public void testGetDisclaimerContentMF() {
        String disclaimer = tradeConfirmationReportMF.getDisclaimerContent();
        Assert.assertEquals("DS-IP-0188", disclaimer);
    }

    @Test
    public void testDisclaimer_whenInvestor_thenCmsReturned()
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

        Map<String, Object> params = new HashMap<>();
        params.put(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, accountId);
        Map<String, Object> dataCollections = new HashMap<>();
        dataCollections.put("AccountReportV2.accountData." + EncodedString.toPlainText(accountId), new WrapAccountDetailImpl());

        final Collection<AccountDto> accountDtos = new ArrayList<AccountDto>();
        AccountKey accountKey = new AccountKey("accountId");
        AccountDto accountDto = new AccountDto(accountKey);
        accountDto.setAdviserName("adviser");
        accountDtos.add(accountDto);

        TradeConfirmationReportLS trade = new TradeConfirmationReportLS() {

            @Override
            public String getDisclaimerContent() {
                return "disclaimer";
            }
        };

        UserProfile activeProfile = mock(UserProfile.class);
        when(activeProfile.getJobRole()).thenReturn(JobRole.INVESTOR);
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(activeProfile);

        Mockito.when(cmsService.getDynamicContent(Mockito.any(String.class), Mockito.any(String[].class)))
                .thenReturn("MockString");

        Field userField = TradeConfirmationReportLS.class.getSuperclass().getDeclaredField("userProfileService");
        userField.setAccessible(true);
        userField.set(trade, userProfileService);

        Field cmsField = TradeConfirmationReportLS.class.getSuperclass().getSuperclass().getSuperclass()
                .getDeclaredField("cmsService");
        cmsField.setAccessible(true);
        cmsField.set(trade, cmsService);

        String content = trade.getDisclaimer(params, dataCollections);

        assertEquals("MockString", content);
    }

    @Test
    public void testDisclaimer_whenNotInvestor_thenYourAdviserReturned()
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

        Map<String, Object> params = new HashMap<>();
        params.put(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, accountId);
        Map<String, Object> dataCollections = new HashMap<>();
        dataCollections.put("AccountReportV2.accountData." + EncodedString.toPlainText(accountId), new WrapAccountDetailImpl());

        final Collection<AccountDto> accountDtos = new ArrayList<AccountDto>();
        AccountKey accountKey = new AccountKey("accountId");
        AccountDto accountDto = new AccountDto(accountKey);
        accountDto.setAdviserName("adviser");
        accountDtos.add(accountDto);

        TradeConfirmationReportLS trade = new TradeConfirmationReportLS() {

            @Override
            public String getDisclaimerContent() {
                return "disclaimer";
            }
        };

        UserProfile activeProfile = mock(UserProfile.class);
        when(activeProfile.getJobRole()).thenReturn(JobRole.ADVISER);
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(activeProfile);

        when(cmsService.getDynamicContent(Mockito.any(String.class), Mockito.any(String[].class)))
                .thenAnswer(new Answer<String>() {
                    @Override
                    public String answer(InvocationOnMock invocation) throws Throwable {
                        Object[] args = invocation.getArguments();
                        return ((String[]) args[1])[0];
                    }
                });

        Field userField = TradeConfirmationReportLS.class.getSuperclass().getDeclaredField("userProfileService");
        userField.setAccessible(true);
        userField.set(trade, userProfileService);

        Field cmsField = TradeConfirmationReportLS.class.getSuperclass().getSuperclass().getSuperclass()
                .getDeclaredField("cmsService");
        cmsField.setAccessible(true);
        cmsField.set(trade, cmsService);

        String content = trade.getDisclaimer(params, dataCollections);

        assertEquals("your adviser", content);
    }

    @Test
    public void testAccountInformationWithAccount() {
        Map<String, Object> params = new HashMap<>();
        params.put(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, accountId);
        params.put(UriMappingConstants.ORDER_ID_URI_MAPPING, orderId);

        com.bt.nextgen.service.integration.account.AccountKey accountKey = tradeConfirmationReport.getAccountKey(params);
        Assert.assertEquals(EncodedString.toPlainText(accountId), accountKey.getId());
    }

    @Test
    public void testAccountInformationWithoutAccount() {
        Map<String, Object> params = new HashMap<>();
        params.put(UriMappingConstants.ORDER_ID_URI_MAPPING, orderId);

        com.bt.nextgen.service.integration.account.AccountKey accountKey = tradeConfirmationReport.getAccountKey(params);
        Assert.assertEquals(transactionOrders.get(0).getAccountId(), accountKey.getId());
    }

    @Test
    public void testReportName() {
        Map<String, String> params = new HashMap<>();
        String reportName = tradeConfirmationReport.getReportName(params);
        Assert.assertEquals("Transaction confirmation", reportName);
    }

    @Test
    public void testReportTitle() {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> dataCollections = new HashMap<>();
        String reportTitle = tradeConfirmationReport.getReportTitle(params, dataCollections);
        Assert.assertEquals("Transaction confirmation", reportTitle);
    }

    @Test
    public void testReportType() {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> dataCollections = new HashMap<>();
        String reportType = tradeConfirmationReport.getReportType(params, dataCollections);
        Assert.assertEquals("Transaction confirmation", reportType);
    }
}
