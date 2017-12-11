package com.bt.nextgen.reports.account;

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
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.avaloq.asset.ShareAssetImpl;
import com.bt.nextgen.service.avaloq.order.OrderTransactionImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.integration.asset.AssetCluster;
import com.bt.nextgen.service.integration.asset.AssetStatus;
import com.btfin.panorama.service.integration.asset.AssetType;
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
import org.mockito.runners.MockitoJUnitRunner;

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
    private List<OrderTransaction> transactionOrders;
    private TradeOrderDto tradeOrderDto;

    @InjectMocks
    private TradeConfirmationReport tradeConfirmationReport;

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

    private ShareAssetImpl shareAsset;

    @Before
    public void setup() {
        accountId = "975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0";
        orderId = "12356";
        key = new OrderKey("12356");

        shareAsset = new ShareAssetImpl();
        shareAsset.setAssetId("12345");
        shareAsset.setAssetName("BHP Billiton");
        shareAsset.setAssetType(AssetType.SHARE);
        shareAsset.setAssetCluster(AssetCluster.AUSTRALIAN_LISTED_SECURITIES);
        shareAsset.setStatus(AssetStatus.OPEN);

        // Mock content service
        ContentDto content = new ContentDto(new ContentKey("MockKey"), "MockString");
        when(contentService.find((any(ContentKey.class)), any(ServiceErrorsImpl.class))).thenReturn(content);

        tradeOrderDto = mockTradeOrderDtoService();
        accountDetailDto = mockWrapAccountDto();
        transactionOrders = mockTransactionOrders();
        when(tradeOrderDtoService.find((Matchers.any(OrderKey.class)), any(ServiceErrorsImpl.class))).thenReturn(tradeOrderDto);
        when(accountDetailService.search((Matchers.any(List.class)), any(ServiceErrorsImpl.class)))
                .thenReturn(accountDetailDto);
        when(orderIntegrationService.loadTransactionData(Mockito.anyString(), any(ServiceErrorsImpl.class)))
                .thenReturn(transactionOrders);
    }

    public TradeOrderDto mockTradeOrderDtoService() {
        List<OrderTransactionDto> orderTransactions = new ArrayList<OrderTransactionDto>();
        AssetDto assetDto = new AssetDto(shareAsset, shareAsset.getAssetName(), AssetType.SHARE.getDisplayName());
        OrderTransactionDto orderTransaction = new OrderTransactionDto(new DateTime(), new BigDecimal(20), new BigDecimal(25.90),
                new BigDecimal(15), new BigDecimal(2100), new DateTime());
        orderTransactions.add(orderTransaction);
        List<OrderTransaction> transactionOrders = mockTransactionOrders();
        TradeOrderDto tradeOrderDto = new TradeOrderDto(key, "buy", assetDto, true, transactionOrders.get(0), orderTransactions);
        return tradeOrderDto;
    }

    public List<OrderTransaction> mockTransactionOrders() {
        List<OrderTransaction> transactionOrders = new ArrayList<>();
        OrderTransactionImpl transactionOrder = new OrderTransactionImpl();
        transactionOrder.setAccountId(accountId);
        transactionOrder.setAssetId("12345");
        transactionOrder.setFilledQuantity(60L);
        transactionOrder.setConsideration(new BigDecimal(1000));
        transactionOrder.setOrderType(OrderType.PARTIAL_REDEMPTION);
        transactionOrder.setOrderId(orderId);
        transactionOrder.setTransactionFee(new BigDecimal(15));
        transactionOrder.setTradeDate(new DateTime());
        transactionOrder.setPriceType(PriceType.MARKET);
        transactionOrder.setCancellable(true);
        transactionOrder.setSettlementDate(new DateTime());
        transactionOrder.setOriginalQuantity(BigDecimal.valueOf(200));
        transactionOrders.add(transactionOrder);
        return transactionOrders;
    }

    public WrapAccountDetailDto mockWrapAccountDto() {
        WrapAccountDetailDto accountDetailDto = new WrapAccountDetailDto(new AccountKey(accountId), new DateTime(),
                new DateTime());
        accountDetailDto.setAccountName("account name");
        accountDetailDto.setAccountNumber(new String("45767677"));
        accountDetailDto.setBsb("010567");
        accountDetailDto.setAccountType("savings");
        return accountDetailDto;
    }

    @Test
    public void testTradeConfirmationReport() {

        Map<String, String> params = new HashMap<>();
        params.put(UriMappingConstants.ORDER_ID_URI_MAPPING, orderId);

        TradeOrderDto resultDto = tradeConfirmationReport.getOrder(params);
        Assert.assertNotNull(resultDto);
        Assert.assertFalse(resultDto.getOrderTransactions().isEmpty());
        Assert.assertEquals(resultDto.getOrderTransactions().size(), 1);

    }

    @Test
    public void testDisclaimer() 
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        
        Map<String, String> params = new HashMap<>();

        final Collection<AccountDto> accountDtos = new ArrayList<AccountDto>();
        AccountKey accountKey = new AccountKey("accountId");
        AccountDto accountDto = new AccountDto(accountKey);
        accountDto.setAdviserName("adviser");
        accountDtos.add(accountDto);

        TradeConfirmationReport trade = new TradeConfirmationReport() {
            @Override
            public Collection<AccountDto> getAccount(Map<String, String> params) {
                return accountDtos;
            }
        };

        UserProfile activeProfile = mock(UserProfile.class);
        when(activeProfile.getJobRole()).thenReturn(JobRole.INVESTOR);
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        
        Mockito.when(cmsService.getDynamicContent(Mockito.any(String.class), Mockito.any(String[].class)))
                .thenReturn("MockString");

        Field userField = TradeConfirmationReport.class.getDeclaredField("userProfileService");
        userField.setAccessible(true);
        userField.set(trade, userProfileService);

        Field cmsField = TradeConfirmationReport.class.getSuperclass().getSuperclass().getDeclaredField("cmsService");
        cmsField.setAccessible(true);
        cmsField.set(trade, cmsService);

        String content = trade.getDisclaimer(params);

        assertEquals("MockString", content);
    }

    @Test
    public void testAccountInformationWithAccount() {
        Map<String, String> params = new HashMap<>();
        params.put(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, accountId);
        params.put(UriMappingConstants.ORDER_ID_URI_MAPPING, orderId);

        Collection<AccountDto> accounts = tradeConfirmationReport.getAccount(params);
        Assert.assertEquals(1, accounts.size());
    }

    @Test
    public void testAccountInformationWithoutAccount() {
        Map<String, String> params = new HashMap<>();
        params.put(UriMappingConstants.ORDER_ID_URI_MAPPING, orderId);

        Collection<AccountDto> accounts = tradeConfirmationReport.getAccount(params);
        Assert.assertEquals(1, accounts.size());
    }

    @Test
    public void testReportName() {
        Map<String, String> params = new HashMap<>();
        String reportName = tradeConfirmationReport.getReportName(params);
        Assert.assertEquals("Transaction confirmation", reportName);
    }

}
