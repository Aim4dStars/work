package com.bt.nextgen.api.order.service;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.model.InterestRateDto;
import com.bt.nextgen.api.asset.model.ManagedFundAssetDto;
import com.bt.nextgen.api.asset.model.ManagedPortfolioAssetDto;
import com.bt.nextgen.api.asset.model.ShareAssetDto;
import com.bt.nextgen.api.asset.model.TermDepositAssetDto;
import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.api.asset.service.AssetDtoConverterV2;
import com.bt.nextgen.api.draftaccount.model.form.IPersonDetailsForm;
import com.bt.nextgen.api.order.model.GeneralOrderDto;
import com.bt.nextgen.api.order.model.OrderDto;
import com.bt.nextgen.api.order.model.OrderKey;
import com.bt.nextgen.api.order.model.ShareOrderDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.web.model.SearchParameters;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.CacheManagedTermDepositAssetRateIntegrationService;
import com.bt.nextgen.service.avaloq.asset.ManagedFundAssetImpl;
import com.bt.nextgen.service.avaloq.asset.ShareAssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.avaloq.order.OrderDetailImpl;
import com.bt.nextgen.service.avaloq.order.OrderImpl;
import com.bt.nextgen.service.integration.Origin;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.bt.nextgen.service.integration.asset.AssetStatus;
import com.bt.nextgen.service.integration.asset.ManagedFundAsset;
import com.bt.nextgen.service.integration.asset.ShareAsset;
import com.bt.nextgen.service.integration.asset.Term;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.order.ExpiryMethod;
import com.bt.nextgen.service.integration.order.Order;
import com.bt.nextgen.service.integration.order.OrderDetail;
import com.bt.nextgen.service.integration.order.OrderIntegrationService;
import com.bt.nextgen.service.integration.order.OrderStatus;
import com.bt.nextgen.service.integration.order.OrderType;
import com.bt.nextgen.service.integration.order.PriceType;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRate;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRateImpl;
import com.bt.nextgen.termdeposit.service.TermDepositAssetRateSearchKey;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerType;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import org.powermock.api.mockito.PowerMockito;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(MockitoJUnitRunner.class)
public class OrderDtoServiceV2Test {
    @InjectMocks
    private OrderDtoServiceImplV2 orderDtoService;

    @Mock
    private AssetIntegrationService assetService;

    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private AssetDtoConverterV2 converter;

    @Mock
    private OrderIntegrationService orderService;

    @Spy
    private final OrderSearchMapper orderSearchMapper = new OrderSearchMapperImpl();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    private Logger loggerMock;


    List<Order> emptyOrderList;
    OrderImpl order1;
    OrderImpl order2;
    OrderImpl order3;
    OrderImpl order4;
    OrderImpl order5;
    OrderImpl order6;
    OrderImpl order7;
    List<Order> orderList;
    Map<String, WrapAccountDetail> accounts;
    ManagedFundAssetImpl assetMF;
    AssetImpl assetMP;
    TermDepositAssetImpl assetTD;
    List<OrderDetail> details = new ArrayList<>();
    List<TermDepositInterestRate> termDepositInterestRates = new ArrayList<>();
    OrderDetailImpl detail;
    AccountKey accountKey;
    BrokerImpl broker;
    Map<String, Asset> assetMap = new HashMap<String, Asset>();

    Collection<Broker> brokers;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        orderList = new ArrayList<>();
        emptyOrderList = new ArrayList<>();

        Order order1 = mock(Order.class);
        when(order1.getOrderId()).thenReturn("12345");
        when(order1.getCreateDate()).thenReturn(new DateTime().plusDays(2));
        when(order1.getOrigin()).thenReturn(Origin.WEB_UI);
        when(order1.getAccountId()).thenReturn("11112");
        when(order1.getOrderType()).thenReturn(OrderType.PURCHASE);
        when(order1.getAssetId()).thenReturn("28737");
        when(order1.getAmount()).thenReturn(new BigDecimal("1000"));
        when(order1.getStatus()).thenReturn(OrderStatus.IN_PROGRESS);
        when(order1.getCancellable()).thenReturn(true);
        when(order1.getLastTranSeqId()).thenReturn("98756");
        when(order1.getBrokerName()).thenReturn("98756");
        when(order1.getBrokerage()).thenReturn(BigDecimal.TEN);

        order2 = new OrderImpl();
        order2.setOrderId("23456");
        order2.setCreateDate(new DateTime().plusDays(1));
        order2.setOrigin(Origin.WEB_UI);
        order2.setAccountId("11112");
        order2.setOrderType(OrderType.FULL_REDEMPTION);
        order2.setAssetId("28737");
        order2.setAmount(new BigDecimal("2000"));
        order2.setStatus(OrderStatus.COMPLETED);
        order2.setCancellable(true);

        order3 = new OrderImpl();
        order3.setOrderId("52001");
        order3.setCreateDate(new DateTime());
        order3.setOrigin(Origin.WEB_UI);
        order3.setAccountId("11112");
        order3.setOrderType(OrderType.FULL_REDEMPTION);
        order3.setAssetId("54745");
        order3.setAmount(new BigDecimal("3000"));
        order3.setStatus(OrderStatus.COMPLETED);
        order3.setCancellable(true);
        order3.setOriginalQuantity(new BigDecimal("162"));

        order4 = new OrderImpl();
        order4.setOrderId("12346");
        order4.setCreateDate(new DateTime().plusDays(2));
        order4.setOrigin(Origin.WEB_UI);
        order4.setAccountId("11112");
        order4.setOrderType(OrderType.PURCHASE);
        order4.setAssetId("110456");
        order4.setAmount(new BigDecimal("1000"));
        order4.setStatus(OrderStatus.COMPLETED);
        order4.setCancellable(true);
        order4.setLastTranSeqId("2");
        order4.setCancellationCount(BigDecimal.ZERO);
        order4.setMaxCancellationCount(BigDecimal.TEN);

        detail = new OrderDetailImpl();
        detail.setKey("qty");
        detail.setValue(BigDecimal.valueOf(162));
        details.add(detail);
        detail = new OrderDetailImpl();
        detail.setKey("price");
        detail.setValue(BigDecimal.valueOf(18.5185));
        details.add(detail);
        order3.setDetails(details);

        order5 = new OrderImpl();
        order5.setOrderId("85236");
        order5.setCreateDate(new DateTime().plusDays(1));
        order5.setOrigin(Origin.WEB_UI);
        order5.setAccountId("11112");
        order5.setOrderType(OrderType.FULL_REDEMPTION_F);
        order5.setAssetId("28737");
        order5.setAmount(new BigDecimal("2000"));
        order5.setStatus(OrderStatus.IN_PROGRESS);
        order5.setCancellable(true);

        order6 = new OrderImpl();
        order6.setOrderId("483481");
        order6.setCreateDate(new DateTime().plusDays(1));
        order6.setOrigin(Origin.IPO);
        order6.setAccountId("11112");
        order6.setOrderType(OrderType.STEX_BUY);
        order6.setAssetId("28737");
        order6.setNetAmount(new BigDecimal("2000"));
        order6.setStatus(OrderStatus.IN_PROGRESS);
        order6.setCancellable(false);

        order7 = new OrderImpl();
        order7.setOrderId("483490");
        order7.setAccountId("11112");
        order7.setCreateDate(new DateTime().minusDays(10));
        order7.setTradeDate(new DateTime().minusDays(9));
        order7.setOrigin(Origin.REGULAR_INVESTMENT);
        order7.setOrderType(OrderType.STEX_BUY);
        order7.setAssetId("28100");
        order7.setNetAmount(new BigDecimal("10000"));
        order7.setStatus(OrderStatus.COMPLETED);
        order7.setCancellable(false);
        order7.setAmount(new BigDecimal(10000));


        orderList.add(order1);
        orderList.add(order2);
        orderList.add(order3);
        orderList.add(order5);
        orderList.add(order6);
        orderList.add(getJbhShareOrder());
        orderList.add(getJbhShareOrderWithNulls());
        orderList.add(getFailedJbhShareOrder());
        orderList.add(getPBShareOrder());
        orderList.add(order7);

        accountKey = AccountKey.valueOf("11112");
        accounts = new HashMap<String, WrapAccountDetail>();
        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        WrapAccountImpl account = new WrapAccountImpl();
        account.setAccountKey(accountKey);
        account.setAccountName("Robert Gilby");
        accountMap.put(accountKey, account);
        WrapAccountDetailImpl sampleAccountDetail = new WrapAccountDetailImpl();
        sampleAccountDetail.setAccountKey(accountKey);
        sampleAccountDetail.setAccountName("Robert Gilby");
        accounts.put("11112", sampleAccountDetail);
        Mockito.when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(sampleAccountDetail);
        Mockito.when(accountService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(accountMap);
        Mockito.when(accountService.loadWrapAccountWithoutContainers(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(account);

        /*PowerMockito.mockStatic(LoggerFactory.class);

        when(LoggerFactory.getLogger(OrderDtoServiceImpl.class)).
                thenReturn(loggerMock);*/

        assetMF = new ManagedFundAssetImpl();
        assetMF.setAssetId("54745");
        assetMF.setAssetCode("ADV0133AU");
        assetMF.setAssetClass(AssetClass.DIVERSIFIED);
        assetMF.setAssetType(AssetType.MANAGED_FUND);
        assetMF.setAssetName("Advance Australian Fixed Interest Index Fund");
        assetMF.setPrice(new BigDecimal(.53));
        assetMF.setStatus(AssetStatus.OPEN);
        assetMF.setDistributionMethod("Cash Only");
        assetMap.put("54745", assetMF);

        assetMP = new AssetImpl();
        assetMP.setAssetId("28737");
        assetMP.setAssetCode("BR0001");
        assetMP.setAssetType(AssetType.MANAGED_PORTFOLIO);
        assetMP.setAssetName("BlackRock International");
        assetMP.setStatus(AssetStatus.OPEN);
        assetMap.put("28737", assetMP);

        ShareAssetImpl jbhAsset = new ShareAssetImpl();
        jbhAsset.setAssetId("110456");
        jbhAsset.setAssetCode("JBH");
        jbhAsset.setAssetType(AssetType.SHARE);
        jbhAsset.setAssetName("JB Hifi");
        jbhAsset.setStatus(AssetStatus.OPEN);
        assetMap.put("110456", jbhAsset);


        assetTD = new TermDepositAssetImpl();
        assetTD.setAssetId("28100");
        assetTD.setAssetCode("TD0001");
        assetTD.setAssetType(AssetType.TERM_DEPOSIT);
        assetTD.setAssetName("St George 6 monthts");
        assetTD.setStatus(AssetStatus.OPEN);
        assetMap.put("28100", assetTD);
        broker = new BrokerImpl(BrokerKey.valueOf("99971"), BrokerType.ADVISER);
        broker.setDealerKey(BrokerKey.valueOf("45677"));

        TermDepositInterestRate termDepositInterestRate = new TermDepositInterestRateImpl.TermDepositInterestRateBuilder().withAssetKey(AssetKey.valueOf("28100")).withRate(new BigDecimal(1.5))
                .withTerm(new Term("6M")).withAccountStructureType(AccountStructureType.Individual).withIssuerId("800000152").withIssuerName("St George").withDealerGroupKey(BrokerKey.valueOf("299971")).buildTermDepositRate();
        termDepositInterestRates.add(termDepositInterestRate);

        Mockito.when(assetService.loadAssets(Mockito.anyList(), any(ServiceErrors.class))).thenReturn(assetMap);

        Mockito.when(userProfileService.getUserId()).thenReturn("201601388");
        Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(broker);

        Mockito.when(assetService.loadTermDepositRates(any(TermDepositAssetRateSearchKey.class), any(ServiceErrors.class)))
                .thenReturn(termDepositInterestRates);

        Answer<Map<String, AssetDto>> assetAnswer = new Answer<Map<String, AssetDto>>() {
            @Override
            public Map<String, AssetDto> answer(InvocationOnMock invocation) throws Throwable {
                Map<String, Asset> assets = (Map<String, Asset>) invocation.getArguments()[0];
                Map<String, AssetDto> result = new HashMap<>();
                for (Asset asset : assets.values()) {
                    if (asset.getAssetType() == AssetType.MANAGED_PORTFOLIO) {
                        result.put(asset.getAssetId(), new ManagedPortfolioAssetDto(asset));
                    } else if (asset.getAssetType() == AssetType.MANAGED_FUND) {
                        ManagedFundAsset mfAsset = (ManagedFundAsset) asset;
                        result.put(asset.getAssetId(), new ManagedFundAssetDto(mfAsset));
                    } else if (asset.getAssetType() == AssetType.SHARE) {
                        ShareAsset shareAsset = (ShareAsset) asset;
                        result.put(asset.getAssetId(), new ShareAssetDto(shareAsset));
                    } else {
                        List<InterestRateDto> interestBands = Collections.emptyList();
                        result.put(asset.getAssetId(), new TermDepositAssetDto(asset, asset.getAssetName(), null, null, null,
                                null, null, null, interestBands, null));
                    }
                }

                return result;
            }
        };

        Mockito.when(converter.toAssetDto(Mockito.anyMap(), Mockito.anyList())).thenAnswer(assetAnswer);
        Mockito.when(converter.toAssetDto(Mockito.anyMap(), Mockito.anyList(), Mockito.anyBoolean())).thenAnswer(assetAnswer);
        Mockito.when(orderService.loadOrder(any(String.class), any(ServiceErrors.class))).thenReturn(orderList);
        Mockito.when(orderService.loadOrders(any(SearchParameters.class), any(ServiceErrors.class)))
                .thenReturn(orderList);
        Mockito.when(orderService.searchOrders(any(String.class), any(ServiceErrors.class)))
                .thenReturn(orderList);
    }

    private Order getJbhShareOrder() {
        OrderImpl jbhShareOrder;

        jbhShareOrder = new OrderImpl();
        jbhShareOrder.setOrderId("781088");
        jbhShareOrder.setCreateDate(new DateTime().minusDays(1));
        jbhShareOrder.setOrigin(Origin.IPO);
        jbhShareOrder.setAccountId("11112");
        jbhShareOrder.setOrderType(OrderType.STEX_BUY);
        jbhShareOrder.setAssetId("110456");
        jbhShareOrder.setAmount(new BigDecimal("1111"));
        jbhShareOrder.setNetAmount(new BigDecimal("1111"));
        jbhShareOrder.setStatus(OrderStatus.IN_PROGRESS);
        jbhShareOrder.setPriceType(PriceType.LIMIT);
        jbhShareOrder.setExpiryType(ExpiryMethod.GFD);
        jbhShareOrder.setCancellable(true);
        jbhShareOrder.setFilledQuantity(235);
        jbhShareOrder.setLimitPrice(new BigDecimal("2.35"));
        jbhShareOrder.setOriginalQuantity(new BigDecimal("2000"));
        jbhShareOrder.setContractNotes(false);
        jbhShareOrder.setCancellationCount(BigDecimal.ZERO);
        jbhShareOrder.setMaxCancellationCount(BigDecimal.TEN);

        List<OrderDetail> jbhShareOrderDetails = new ArrayList<>();
        jbhShareOrder.setDetails(jbhShareOrderDetails);
        OrderDetailImpl jbhShareOrderDetail = new OrderDetailImpl();
        jbhShareOrderDetail.setKey("qty");
        jbhShareOrderDetail.setValue(new BigDecimal("2000"));
        jbhShareOrderDetails.add(jbhShareOrderDetail);
        jbhShareOrderDetail = new OrderDetailImpl();
        jbhShareOrderDetail.setKey("price");
        jbhShareOrderDetail.setValue(new BigDecimal("1.05"));
        jbhShareOrderDetails.add(jbhShareOrderDetail);
        return jbhShareOrder;
    }

    private Order getJbhShareOrderWithNulls() {
        OrderImpl jbhShareOrder = (OrderImpl) getJbhShareOrder();
        jbhShareOrder.setCreateDate(new DateTime().minusDays(2));
        jbhShareOrder.setFilledQuantity(null);
        jbhShareOrder.setExpiryType(null);
        jbhShareOrder.setPriceType(null);
        return jbhShareOrder;
    }

    private Order getFailedJbhShareOrder() {
        OrderImpl jbhShareOrder = (OrderImpl) getJbhShareOrder();
        jbhShareOrder.setCreateDate(new DateTime().minusDays(3));
        jbhShareOrder.setFilledQuantity(10);
        jbhShareOrder.setStatus(OrderStatus.FAILED);
        jbhShareOrder.setContractNotes(false);
        jbhShareOrder.setRejectionReason("Order rejected by broker.");
        return jbhShareOrder;
    }

    private Order getPBShareOrder() {
        OrderImpl jbhShareOrder = (OrderImpl) getJbhShareOrder();
        jbhShareOrder.setOrderId("781099");
        jbhShareOrder.setCreateDate(new DateTime().minusDays(4));
        jbhShareOrder.setTradeDate(new DateTime().minusDays(5));
        jbhShareOrder.setOrigin(Origin.PANEL_BROKER);
        jbhShareOrder.setStatus(OrderStatus.COMPLETED);
        jbhShareOrder.setPriceType(PriceType.MARKET);
        jbhShareOrder.setExpiryType(ExpiryMethod.GFD);
        jbhShareOrder.setCancellable(false);
        jbhShareOrder.setBrokerName("Macquarie");
        jbhShareOrder.setExternalOrderId("32323232");
        jbhShareOrder.setCancellationCount(BigDecimal.ZERO);
        jbhShareOrder.setMaxCancellationCount(BigDecimal.TEN);
        return jbhShareOrder;
    }

    @Test
    public void testToOrderDto_orderListEmpty() {
        List<OrderDto> managedOrder = orderDtoService.toOrderDtos(emptyOrderList, new ServiceErrorsImpl());
        Assert.assertEquals(0, managedOrder.size());
    }

    @Test
    public void testToOrderDto_sizeMatches() {
        List<OrderDto> managedOrder = orderDtoService.toOrderDtos(orderList, new ServiceErrorsImpl());
        assertNotNull(managedOrder);
        Assert.assertEquals(10, managedOrder.size());
    }

    @Test
    public void testToOrderDto_valueMatches_whenAccountMap_passed() {
        List<OrderDto> managedOrder = orderDtoService.toOrderDtos(orderList, new ServiceErrorsImpl());
        assertNotNull(managedOrder);
        Assert.assertEquals(10, managedOrder.size());
        Assert.assertEquals(orderList.get(0).getOrderId(), managedOrder.get(0).getKey().getOrderId());
        Assert.assertEquals(orderList.get(0).getDisplayOrderId(), managedOrder.get(0).getDisplayOrderId());
        Assert.assertEquals(assetMP.getAssetName(), managedOrder.get(0).getAsset().getAssetName());
        Assert.assertEquals(orderList.get(0).getOrigin().getName(), managedOrder.get(0).getOrigin());
        Assert.assertEquals(orderList.get(0).getOrderType().getDisplayName(), managedOrder.get(0).getOrderType());
        Assert.assertEquals(assetMP.getAssetType().getDisplayName(), managedOrder.get(0).getAsset().getAssetType());
        Assert.assertEquals(orderList.get(0).getStatus().getDisplayName(), managedOrder.get(0).getStatus());
        // disabled pending account service mscreen mappiong
        // Assert.assertEquals(accounts.get(managedOrder.get(0).getAccountNumber()).getAccountName(),
        // managedOrder.get(0)
        // .getAccountName());
        Assert.assertEquals(accountKey.getId(), EncodedString.toPlainText(managedOrder.get(0).getAccountKey()));
        Assert.assertEquals(orderList.get(0).getCancellable(), managedOrder.get(0).getCancellable());
        Assert.assertEquals(orderList.get(0).getCreateDate(), managedOrder.get(0).getSubmitDate());
        Assert.assertEquals(orderList.get(0).getAmount().abs(), managedOrder.get(0).getAmount());
        // disabled pending account service mscreen mappiong
        // Assert.assertEquals(orderList.get(0).getAccountId(),
        // managedOrder.get(0).getAccountNumber());
        Assert.assertEquals(2, orderList.get(2).getDetails().size());
        OrderDetail orderDetail = orderList.get(2).getDetails().get(0);
        Assert.assertEquals("qty", orderDetail.getKey());
        Assert.assertEquals("162", orderDetail.getValue().toString());
        OrderDetail orderDetail1 = orderList.get(2).getDetails().get(1);
        Assert.assertEquals("price", orderDetail1.getKey());
        Assert.assertEquals("18.5185", orderDetail1.getValue().toString());
        Assert.assertEquals(orderList.get(0).getOrigin().isExternal(), managedOrder.get(0).getExternal());
        Assert.assertEquals(managedOrder.get(0).getBrokerage(), orderList.get(0).getBrokerage().setScale(2, RoundingMode.HALF_UP));
        Assert.assertNull(managedOrder.get(1).getBrokerage());

        Assert.assertEquals(accountKey.getId(), EncodedString.toPlainText(managedOrder.get(9).getAccountKey()));
        Assert.assertEquals(orderList.get(9).getCancellable(), managedOrder.get(9).getCancellable());
        Assert.assertEquals(orderList.get(9).getCreateDate(), managedOrder.get(9).getSubmitDate());
        Assert.assertEquals(orderList.get(9).getNetAmount().abs(), managedOrder.get(9).getAmount());
    }

    @Test
    public void testToOrderDto_whenManagedPortfolioCompletedAndCancellable_thenStatusInProgress() {
        List<OrderDto> managedOrder = orderDtoService.toOrderDtos(orderList, new ServiceErrorsImpl());
        Assert.assertEquals(OrderStatus.COMPLETED, orderList.get(1).getStatus());
        Assert.assertTrue(orderList.get(1).getCancellable());
        Assert.assertEquals(OrderStatus.IN_PROGRESS.getDisplayName(), managedOrder.get(1).getStatus());
    }


    @Test
    public void testSearch() {
        List<ApiSearchCriteria> criteriaList = new ArrayList<ApiSearchCriteria>();

        ApiSearchCriteria fromDate = new ApiSearchCriteria(Attribute.LAST_UPDATE_DATE, SearchOperation.NEG_GREATER_THAN,
                new DateTime().toString(), OperationType.DATE);
        ApiSearchCriteria toDate = new ApiSearchCriteria(Attribute.LAST_UPDATE_DATE, SearchOperation.NEG_LESS_THAN,
                new DateTime().toString(), OperationType.DATE);

        criteriaList.add(fromDate);
        criteriaList.add(toDate);

        List<OrderDto> orderDtos = orderDtoService.search(criteriaList, null);

        Assert.assertNotNull(orderDtos);
        Assert.assertEquals(orderDtos.get(0).getKey().getOrderId(), "12345");

    }


    @Test
    public void testSearch_ForMaxDate() {
        List<ApiSearchCriteria> criteriaList = new ArrayList<ApiSearchCriteria>();

        ApiSearchCriteria fromDate = new ApiSearchCriteria(Attribute.LAST_UPDATE_DATE, SearchOperation.NEG_GREATER_THAN,
                new DateTime().withTimeAtStartOfDay().toString(), OperationType.DATE);
        ApiSearchCriteria toDate = new ApiSearchCriteria(Attribute.LAST_UPDATE_DATE, SearchOperation.NEG_LESS_THAN,
                new DateTime().withTimeAtStartOfDay().toString(), OperationType.DATE);

        criteriaList.add(fromDate);
        criteriaList.add(toDate);

        List<OrderDto> orderDtos = orderDtoService.search(criteriaList, null);

        Assert.assertNotNull(orderDtos);
        Assert.assertEquals(orderDtos.get(0).getKey().getOrderId(), "12345");

    }

    @Test
    public void testFindOrder() {
        OrderKey orderKey = new OrderKey("12345");
        OrderDto orderDto = orderDtoService.find(orderKey, null);
        Assert.assertNotNull(orderDto);
        Assert.assertEquals(orderDto.getKey().getOrderId(), "12345");
    }



    @Test
    public void testSearchOrders() {
        OrderKey orderKey = new OrderKey("12345");
        List<OrderDto> orderDtos = orderDtoService.search(orderKey, null);
        Assert.assertNotNull(orderDtos);
        Assert.assertEquals(orderDtos.get(0).getKey().getOrderId(), "12345");

    }

    @Test
    public void testUpdateOrder() {

        OrderKey orderKey = new OrderKey("12345");
        OrderDto orderDto = orderDtoService.find(orderKey, null);
        ((GeneralOrderDto) orderDto).setStatus("Cancelled");

        OrderIntegrationService orderServiceMock = mock(OrderIntegrationService.class);
        Mockito.doNothing().when(orderServiceMock).cancelOrder(any(BigInteger.class), any(BigInteger.class),
                any(ServiceErrors.class));

        orderDto = orderDtoService.update(orderDto, null);

        // Assert.assertEquals(orderDto.getStatus(),"Rejected" );

    }

    @Test
    public void testFindAll() {
        List<OrderDto> orderDtos = orderDtoService.findAll(null);
        Assert.assertNotNull(orderDtos);
        Assert.assertEquals(orderDtos.get(0).getKey().getOrderId(), "12345");

    }

    @Test
    public void testToOrderDto_whenShareAssetOrderConverted_thenDetailsMatch() {
        List<OrderDto> orderDtos = orderDtoService.toOrderDtos(orderList, new ServiceErrorsImpl());
        for (Order jbhShareOrder : orderList.subList(5, 9)) {
            ShareOrderDto jbhShareOrderDto = (ShareOrderDto) orderDtos.get(orderList.indexOf(jbhShareOrder));
            Integer filledQuantity = jbhShareOrder.getFilledQuantity() == null ? 0 : jbhShareOrder.getFilledQuantity();
            String priceType = jbhShareOrder.getPriceType() == null ? null : jbhShareOrder.getPriceType().getDisplayName();
            Assert.assertEquals(priceType, jbhShareOrderDto.getPriceType());
            String expiryType = jbhShareOrder.getExpiryType() == null ? null : jbhShareOrder.getExpiryType().name();
            Assert.assertEquals(expiryType, jbhShareOrderDto.getExpiryType());
            Assert.assertEquals(jbhShareOrder.getLimitPrice(), jbhShareOrderDto.getLimitPrice());
            Assert.assertEquals(jbhShareOrder.getRejectionReason(), jbhShareOrderDto.getRejectionReason());
            Assert.assertEquals(jbhShareOrder.getStatus().getDisplayName(), jbhShareOrderDto.getStatus());
            Assert.assertEquals(jbhShareOrder.getOriginalQuantity(), jbhShareOrderDto.getQuantity());
            Assert.assertEquals(jbhShareOrder.getContractNotes(), jbhShareOrderDto.getContractNotes());
            Assert.assertEquals(jbhShareOrder.getBrokerName(), jbhShareOrderDto.getBrokerName());
            Assert.assertEquals(jbhShareOrder.getExternalOrderId(), jbhShareOrderDto.getExternalOrderId());
            Assert.assertEquals(jbhShareOrder.getCancellationCount(), jbhShareOrderDto.getCancellationCount());
            Assert.assertEquals(jbhShareOrder.getMaxCancellationCount(), jbhShareOrderDto.getMaxCancellationCount());
            Assert.assertEquals(jbhShareOrder.getAmount().abs(), jbhShareOrderDto.getAmount());

            DateTime submitDate = jbhShareOrder.getOrigin().equals(Origin.PANEL_BROKER)
                    ? jbhShareOrder.getTradeDate().withTimeAtStartOfDay() : jbhShareOrderDto.getSubmitDate();
            Assert.assertEquals(submitDate, jbhShareOrderDto.getSubmitDate());
        }

    }

    // TODO: new test cases with order status driving service invocation
    @Test
    public void testCancelOrder_whenNotShareOrder_thenCancelOrderIsCalled() {
        OrderKey orderKey = new OrderKey("12346");
        OrderDto orderDto = orderDtoService.find(orderKey, null);
        ((GeneralOrderDto) orderDto).setStatus("Cancelled");
        ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
        OrderIntegrationService orderServiceMock = mock(OrderIntegrationService.class);
        Mockito.doNothing().when(orderServiceMock).updateStexOrder(any(Order.class), any(ServiceErrors.class));
        orderDto = orderDtoService.update(orderDto, serviceErrors);
        Mockito.verify(orderService).cancelOrder(any(BigInteger.class), any(BigInteger.class),
                any(ServiceErrors.class));
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertNotNull(orderDto);

    }

    @Test
    public void testCancelLSOrder_whenCancellableIsFalse_thenCancelOrderIsCalled() {
        OrderKey orderKey = new OrderKey("12346");
        ShareOrderDto orderDto = new ShareOrderDto();
        orderDto.setKey(orderKey);
        orderDto.setStatus("Cancelled");
        orderDto.setLastTranSeqId("13245");
        orderDto.setCancellable(false);

        Order shareOrder = getJbhShareOrder();
        ((OrderImpl) shareOrder).setStatus(OrderStatus.IN_PROGRESS);

        Mockito.when(orderService.loadOrder(any(String.class), any(ServiceErrors.class)))
                .thenReturn(Collections.singletonList(shareOrder));

        ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
        OrderDto returnedOrderDto = orderDtoService.update(orderDto, serviceErrors);
        Mockito.verify(orderService).cancelOrder(any(BigInteger.class), any(BigInteger.class),
                any(ServiceErrors.class));

        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertNotNull(returnedOrderDto);

        ((OrderImpl) shareOrder).setStatus(OrderStatus.QUEUED);

        returnedOrderDto = orderDtoService.update(orderDto, serviceErrors);
        Mockito.verify(orderService, Mockito.times(2)).cancelOrder(any(BigInteger.class), any(BigInteger.class),
                any(ServiceErrors.class));

        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertNotNull(returnedOrderDto);

    }

    @Test
    public void testCancelLSOrder_whenStatusIsInProgressSentToFundManager_thenUpdateStexOrderIsCalled() {
        OrderKey orderKey = new OrderKey("12346");
        ShareOrderDto orderDto = new ShareOrderDto();
        orderDto.setKey(orderKey);
        orderDto.setStatus("Cancelled");
        orderDto.setLastTranSeqId("13245");
        orderDto.setCancellable(true);

        ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();

        Order shareOrder = getJbhShareOrder();
        ((OrderImpl) shareOrder).setStatus(OrderStatus.IN_PROGRESS_SENT_TO_FUND_MANAGER);

        Mockito.when(orderService.loadOrder(any(String.class), any(ServiceErrors.class)))
                .thenReturn(Collections.singletonList(shareOrder));

        OrderDto returnedOrderDto = orderDtoService.update(orderDto, serviceErrors);
        Mockito.verify(orderService).updateStexOrder(any(Order.class), any(ServiceErrors.class));

        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertNotNull(returnedOrderDto);

    }

    @Test
    public void testUpdateLSOrder_whenOrderIsOpen_thenUpdateStexOrderIsCalled() {
        Order shareOrder = getJbhShareOrder();

        Mockito.when(orderService.loadOrder(any(String.class), any(ServiceErrors.class)))
                .thenReturn(Collections.singletonList(shareOrder));

        OrderKey orderKey = new OrderKey("781088");
        OrderDto shareOrderDto = orderDtoService.find(orderKey, null);
        ((ShareOrderDto) shareOrderDto).setLimitPrice(new BigDecimal("2.435"));

        ((ShareOrderDto) shareOrderDto).setExpiryType(ExpiryMethod.GTC.name());
        ((ShareOrderDto) shareOrderDto).setPriceType(PriceType.LIMIT.getDisplayName());
        ((GeneralOrderDto) shareOrderDto).setQuantity(new BigDecimal("453"));

        ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
        OrderIntegrationService orderServiceMock = mock(OrderIntegrationService.class);
        Mockito.doNothing().when(orderServiceMock).updateStexOrder(any(Order.class), any(ServiceErrors.class));

        shareOrderDto = orderDtoService.update(shareOrderDto, serviceErrors);

        Mockito.verify(orderService).updateStexOrder(any(Order.class), any(ServiceErrors.class));
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertNotNull(shareOrderDto);

        ((OrderImpl) shareOrder).setStatus(OrderStatus.QUEUED);

        shareOrderDto = orderDtoService.update(shareOrderDto, serviceErrors);

        Mockito.verify(orderService, Mockito.times(2)).updateStexOrder(any(Order.class),
                any(ServiceErrors.class));
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertNotNull(shareOrderDto);

        ((OrderImpl) shareOrder).setStatus(OrderStatus.IN_PROGRESS_SENT_TO_FUND_MANAGER);

        shareOrderDto = orderDtoService.update(shareOrderDto, serviceErrors);

        Mockito.verify(orderService, Mockito.times(3)).updateStexOrder(any(Order.class),
                any(ServiceErrors.class));
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertNotNull(shareOrderDto);
    }

    @Test
    public void testSetAmount_whenIPO_thenNetAmountSet() {
        GeneralOrderDto orderDto = new GeneralOrderDto();
        orderDto.setStatus(OrderStatus.IN_PROGRESS.getDisplayName());
        OrderImpl order = new OrderImpl();
        order.setOrigin(Origin.IPO);
        order.setNetAmount(new BigDecimal("2000"));

        orderDtoService.setAmount(orderDto, order);
        Assert.assertEquals(order.getNetAmount(), orderDto.getAmount());
    }

    @Test
    public void testSetAmount_whenFullRedemptionAndNotCompleted_thenAmountNull() {
        GeneralOrderDto orderDto = new GeneralOrderDto();
        orderDto.setStatus(OrderStatus.IN_PROGRESS.getDisplayName());
        OrderImpl order = new OrderImpl();
        order.setOrderType(OrderType.FULL_REDEMPTION);
        order.setAmount(new BigDecimal("2000"));

        orderDtoService.setAmount(orderDto, order);
        Assert.assertNull(orderDto.getAmount());
    }

    @Test
    public void testSetAmount_whenFullRedemptionAndCompleted_thenAmountSet() {
        GeneralOrderDto orderDto = new GeneralOrderDto();
        orderDto.setStatus(OrderStatus.COMPLETED.getDisplayName());
        OrderImpl order = new OrderImpl();
        order.setOrderType(OrderType.FULL_REDEMPTION);
        order.setAmount(new BigDecimal("2000"));

        orderDtoService.setAmount(orderDto, order);
        Assert.assertEquals(order.getAmount(), orderDto.getAmount());
    }

    @Test
    public void testSetAmount_whenFullRedemptionFAndNotCompleted_thenAmountNull() {
        GeneralOrderDto orderDto = new GeneralOrderDto();
        orderDto.setStatus(OrderStatus.IN_PROGRESS.getDisplayName());
        OrderImpl order = new OrderImpl();
        order.setOrderType(OrderType.FULL_REDEMPTION_F);
        order.setAmount(new BigDecimal("2000"));

        orderDtoService.setAmount(orderDto, order);
        Assert.assertNull(orderDto.getAmount());
    }

    @Test
    public void testSetAmount_whenFullRedemptionFAndCompleted_thenAmountSet() {
        GeneralOrderDto orderDto = new GeneralOrderDto();
        orderDto.setStatus(OrderStatus.COMPLETED.getDisplayName());
        OrderImpl order = new OrderImpl();
        order.setOrderType(OrderType.FULL_REDEMPTION_F);
        order.setAmount(new BigDecimal("-2000"));

        orderDtoService.setAmount(orderDto, order);
        Assert.assertEquals(order.getAmount(), orderDto.getAmount());
    }

    @Test
    public void testSetAmount_whenNotFullRedemption_thenAmountSet() {
        GeneralOrderDto orderDto = new GeneralOrderDto();
        orderDto.setStatus(OrderStatus.IN_PROGRESS.getDisplayName());
        OrderImpl order = new OrderImpl();
        order.setOrderType(OrderType.PURCHASE);
        order.setAmount(new BigDecimal("-2000"));

        orderDtoService.setAmount(orderDto, order);
        Assert.assertEquals(order.getAmount().abs(), orderDto.getAmount());
    }

    @Test
    public void testSetAmount_whenAmountNull_thenAmountNotSet() {
        GeneralOrderDto orderDto = new GeneralOrderDto();
        orderDto.setStatus(OrderStatus.COMPLETED.getDisplayName());
        OrderImpl order = new OrderImpl();

        orderDtoService.setAmount(orderDto, order);
        Assert.assertNull(orderDto.getAmount());
    }

    @Test
    public void testSetStatus_whenCompletedCancellableManagedPortfolio_thenStatusInProgress() {
        GeneralOrderDto orderDto = new GeneralOrderDto();
        OrderImpl order = new OrderImpl();
        order.setCancellable(Boolean.TRUE);
        order.setStatus(OrderStatus.COMPLETED);
        AssetImpl asset = new AssetImpl();
        asset.setAssetType(AssetType.MANAGED_PORTFOLIO);
        AssetDto assetDto = new AssetDto(asset, "", AssetType.MANAGED_PORTFOLIO.getDisplayName());

        orderDtoService.setStatus(orderDto, order, assetDto);
        Assert.assertEquals(OrderStatus.IN_PROGRESS.getDisplayName(), orderDto.getStatus());
    }

    @Test
    public void testSetStatus_whenCompletedCancellableManagedFund_thenStatusInProgress() {
        GeneralOrderDto orderDto = new GeneralOrderDto();
        OrderImpl order = new OrderImpl();
        order.setCancellable(Boolean.TRUE);
        order.setStatus(OrderStatus.COMPLETED);
        AssetImpl asset = new AssetImpl();
        asset.setAssetType(AssetType.MANAGED_FUND);
        AssetDto assetDto = new AssetDto(asset, "", AssetType.MANAGED_FUND.getDisplayName());

        orderDtoService.setStatus(orderDto, order, assetDto);
        Assert.assertEquals(OrderStatus.IN_PROGRESS.getDisplayName(), orderDto.getStatus());
    }

    @Test
    public void testSetStatus_whenCompletedAndNotCancellable_thenStatusMatches() {
        GeneralOrderDto orderDto = new GeneralOrderDto();
        OrderImpl order = new OrderImpl();
        order.setStatus(OrderStatus.COMPLETED);
        AssetImpl asset = new AssetImpl();
        asset.setAssetType(AssetType.MANAGED_FUND);
        AssetDto assetDto = new AssetDto(asset, "", AssetType.MANAGED_FUND.getDisplayName());

        orderDtoService.setStatus(orderDto, order, assetDto);
        Assert.assertEquals(order.getStatus().getDisplayName(), orderDto.getStatus());
    }

    @Test
    public void testSetStatus_whenCancellableAndNotCompleted_thenStatusMatches() {
        GeneralOrderDto orderDto = new GeneralOrderDto();
        OrderImpl order = new OrderImpl();
        order.setCancellable(Boolean.TRUE);
        order.setStatus(OrderStatus.IN_PROGRESS);
        AssetImpl asset = new AssetImpl();
        asset.setAssetType(AssetType.MANAGED_PORTFOLIO);
        AssetDto assetDto = new AssetDto(asset, "", AssetType.MANAGED_PORTFOLIO.getDisplayName());

        orderDtoService.setStatus(orderDto, order, assetDto);
        Assert.assertEquals(order.getStatus().getDisplayName(), orderDto.getStatus());
    }

    @Test
    public void testSetStatus_whenManagedPortfolioAndNotCancellable_thenStatusMatches() {
        GeneralOrderDto orderDto = new GeneralOrderDto();
        OrderImpl order = new OrderImpl();
        order.setStatus(OrderStatus.COMPLETED);
        AssetImpl asset = new AssetImpl();
        asset.setAssetType(AssetType.MANAGED_PORTFOLIO);
        AssetDto assetDto = new AssetDto(asset, "", AssetType.MANAGED_PORTFOLIO.getDisplayName());

        orderDtoService.setStatus(orderDto, order, assetDto);
        Assert.assertEquals(order.getStatus().getDisplayName(), orderDto.getStatus());
    }

    @Test
    public void testSetStatus_whenManagedFundAndNotCancellable_thenStatusMatches() {
        GeneralOrderDto orderDto = new GeneralOrderDto();
        OrderImpl order = new OrderImpl();
        order.setStatus(OrderStatus.COMPLETED);
        AssetImpl asset = new AssetImpl();
        asset.setAssetType(AssetType.MANAGED_FUND);
        AssetDto assetDto = new AssetDto(asset, "", AssetType.MANAGED_FUND.getDisplayName());

        orderDtoService.setStatus(orderDto, order, assetDto);
        Assert.assertEquals(order.getStatus().getDisplayName(), orderDto.getStatus());
    }

    @Test
    public void testSetStatus_whenAssetNull_thenStatusMatches() {
        GeneralOrderDto orderDto = new GeneralOrderDto();
        OrderImpl order = new OrderImpl();
        order.setStatus(OrderStatus.COMPLETED);

        orderDtoService.setStatus(orderDto, order, null);
        Assert.assertEquals(order.getStatus().getDisplayName(), orderDto.getStatus());
    }

    @Test
    public void testSetStatus_whenStatusNull_thenStatusNotSet() {
        GeneralOrderDto orderDto = new GeneralOrderDto();
        OrderImpl order = new OrderImpl();

        orderDtoService.setStatus(orderDto, order, null);
        Assert.assertNull(orderDto.getStatus());
    }

    @Test
    public void testSetPrice_whenIPO_thenEstimatedPriceSet() {
        GeneralOrderDto orderDto = new GeneralOrderDto();
        OrderImpl order = new OrderImpl();
        order.setOrigin(Origin.IPO);
        order.setEstimatedPrice(new BigDecimal("2000"));

        orderDtoService.setPrice(orderDto, order);
        Assert.assertEquals(order.getEstimatedPrice(), orderDto.getPrice());
    }

    @Test
    public void testSetPrice_whenIPOAndNoEstimatedPrice_thenPriceZero() {
        GeneralOrderDto orderDto = new GeneralOrderDto();
        OrderImpl order = new OrderImpl();
        order.setOrigin(Origin.IPO);

        orderDtoService.setPrice(orderDto, order);
        Assert.assertEquals(BigDecimal.ZERO, orderDto.getPrice());
    }

    @Test
    public void testFindOrder_With_InvalidOrderKey() {
        OrderKey orderKey = new OrderKey("12345");
        Mockito.when(orderService.loadOrder(anyString(),any(ServiceErrors.class))).thenReturn(new ArrayList<Order>());
        OrderDto orderDto = orderDtoService.find(orderKey, null);
        Assert.assertNull(orderDto);
    }

    @Test
    public void testUpdateOrderForInvalidStatus() {

        List<Order> tdOrders = new ArrayList<>();
        tdOrders.add(order7);
        Mockito.when(orderService.loadOrder(anyString(),any(ServiceErrors.class))).thenReturn(tdOrders);
        OrderKey orderKey = new OrderKey("12345");
        OrderDto orderDto = orderDtoService.find(orderKey, null);
        ((GeneralOrderDto) orderDto).setStatus("xyz");

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("invalid status xyz");
        orderDtoService.update(orderDto, null);

    }

    @Test
    public void testToOrderDto_AssetNotFound() {

        OrderImpl order01 = new OrderImpl();
        order01.setOrderId("23456");
        order01.setCreateDate(new DateTime().plusDays(1));
        order01.setOrigin(Origin.WEB_UI);
        order01.setAccountId("11112");
        order01.setOrderType(OrderType.FULL_REDEMPTION);
        order01.setAssetId("00000");
        order01.setAmount(new BigDecimal("2000"));
        order01.setStatus(OrderStatus.COMPLETED);
        order01.setCancellable(true);

        OrderImpl order02 = new OrderImpl();
        order02.setOrderId("52001");
        order02.setCreateDate(new DateTime());
        order02.setOrigin(Origin.WEB_UI);
        order02.setAccountId("11112");
        order02.setOrderType(OrderType.FULL_REDEMPTION);
        order02.setAssetId("54745");
        order02.setAmount(new BigDecimal("3000"));
        order02.setStatus(OrderStatus.COMPLETED);
        order02.setCancellable(true);
        order02.setOriginalQuantity(new BigDecimal("162"));


        List<Order> newOrderList = new ArrayList<>();

        newOrderList.add(order01);
        newOrderList.add(order02);

        List<OrderDto> managedOrder = orderDtoService.toOrderDtos(newOrderList, new ServiceErrorsImpl());
        Assert.assertNotNull(managedOrder);

    }

    @Test
    public void testToOrderDto_AccountKey() {
        List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        ApiSearchCriteria criteria = new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS,
                "F246E010577065778F3079FB22DFF92167828987629F36F4", OperationType.STRING);
        criteriaList.add(criteria);
        List<OrderDto> orderDtos = orderDtoService.search(criteriaList, null);
        Assert.assertNotNull(orderDtos);
        Assert.assertEquals(orderDtos.get(0).getKey().getOrderId(), "12345");
    }

}