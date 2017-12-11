package com.bt.nextgen.reports.account;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.model.InterestRateDto;
import com.bt.nextgen.api.asset.model.ManagedFundAssetDto;
import com.bt.nextgen.api.asset.model.ManagedPortfolioAssetDto;
import com.bt.nextgen.api.asset.model.ShareAssetDto;
import com.bt.nextgen.api.asset.model.TermDepositAssetDto;
import com.bt.nextgen.api.asset.model.TermDepositAssetDtoV2;
import com.bt.nextgen.api.asset.service.AssetDtoConverterV2;
import com.bt.nextgen.api.order.model.OrderGroupDto;
import com.bt.nextgen.api.order.model.OrderItemDto;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.reports.account.investmentorders.ordercapture.OrderReportTest;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.ManagedFundAssetImpl;
import com.bt.nextgen.service.avaloq.asset.ManagedPortfolioAssetImpl;
import com.bt.nextgen.service.avaloq.asset.ShareAssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.bt.nextgen.service.integration.asset.ManagedFundAsset;
import com.bt.nextgen.service.integration.asset.PaymentFrequency;
import com.bt.nextgen.service.integration.asset.ShareAsset;
import com.bt.nextgen.service.integration.asset.Term;
import com.bt.nextgen.service.integration.asset.TermDepositAsset;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRate;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRateImpl;
import com.bt.nextgen.termdeposit.service.TermDepositAssetRateSearchKey;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.service.integration.broker.BrokerType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderReportV2Test {

    @InjectMocks
    OrderReportV2 orderReportV2;

    @Mock
    private BrokerIntegrationService brokerService;

    @Mock
    private AssetDtoConverterV2 assetConverter;

    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private AssetIntegrationService assetService;

    @Mock
    private CmsService cmsService;

    private final Map<String, Asset> assets = new HashMap<>();
    private Map<String, TermDepositAssetDetail> termDepositDetailMap;
    private TermDepositAssetDto termDepositAssetDto;
    private ManagedFundAssetDto managedFundAssetDto;
    private ShareAssetDto shareAssetDto;
    private ManagedPortfolioAssetDto mpAssetDto;
    private WrapAccountDetailImpl wrapAccountDetail;
    private Answer<Map<String,AssetDto>> assetAnswer;
    private Answer<AssetDto> assetAnswerTD;

    @Before
    public void setup() {
        orderReportV2 = new OrderReportV2("orders","orders_subreport");
        MockitoAnnotations.initMocks(this);
        mockAccountService();
        mockBrokerService();
        mockAssetService();
    }

    private void mockBrokerService() {
        BrokerImpl broker = new BrokerImpl(BrokerKey.valueOf("23333"), BrokerType.ADVISER);
        broker.setDealerKey(BrokerKey.valueOf("99971"));
        when(brokerService.getBroker((Matchers.any(BrokerKey.class)), any(ServiceErrorsImpl.class))).thenReturn(broker);
    }

    private void mockAccountService() {
        wrapAccountDetail = new WrapAccountDetailImpl();
        wrapAccountDetail.setAccountName("test1");
        wrapAccountDetail.setProductKey(ProductKey.valueOf("10285"));
        when(accountService.loadWrapAccountDetail(Mockito.any(AccountKey.class), any(ServiceErrorsImpl.class)))
                .thenReturn(wrapAccountDetail);
    }

    private void mockAssetService() {
        AssetImpl asset1 = new TermDepositAssetImpl();
        asset1.setAssetClass(AssetClass.CASH);
        asset1.setAssetType(AssetType.TERM_DEPOSIT);
        asset1.setAssetName("BT");
        asset1.setAssetId("28100");
        asset1.setBrand("BT");
        assets.put("28100", asset1);
        AssetImpl asset2 = new TermDepositAssetImpl();
        asset2.setAssetClass(AssetClass.CASH);
        asset2.setAssetType(AssetType.TERM_DEPOSIT);
        asset2.setAssetName("BT Term deposit 4 months");
        asset2.setAssetId("28117");
        asset2.setBrand("BT");
        assets.put("28117", asset2);

        when(assetService.loadAssets(Mockito.anyCollection(), any(ServiceErrorsImpl.class))).thenReturn(assets);

        List<TermDepositInterestRate> termDepositInterestRates = new ArrayList<>();
        TermDepositInterestRate termDepositInterestRate = new TermDepositInterestRateImpl.TermDepositInterestRateBuilder().withTerm(new Term("3M")).withPaymentFrequency(PaymentFrequency.AT_MATURITY)
                .withIssuerName("BT").withIssuerId("800000152").withAssetKey(AssetKey.valueOf("28100")).withAccountStructureType(AccountStructureType.Individual)
                .withRate(new BigDecimal(0.015)).withLowerLimit(new BigDecimal(0)).withUpperLimit(new BigDecimal(5000)).buildTermDepositRate();
        termDepositInterestRates.add(termDepositInterestRate);

        when(assetService.loadTermDepositRates(Mockito.any(TermDepositAssetRateSearchKey.class), Mockito.any(ServiceErrors.class))).thenReturn(termDepositInterestRates);
        assetAnswer = new Answer<Map<String, AssetDto>>() {
            @Override
            public Map<String, AssetDto> answer(InvocationOnMock invocationOnMock) throws Throwable {
                Map<String, Asset> assets = (Map<String, Asset>) invocationOnMock.getArguments()[0];
                List<TermDepositInterestRate> termDepositInterestRates = (List<TermDepositInterestRate>)invocationOnMock.getArguments()[1];
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
                        if(CollectionUtils.isNotEmpty(termDepositInterestRates) && isTDAssetPresent(termDepositInterestRates,asset.getAssetId())){
                            List<InterestRateDto> interestBands = Collections.emptyList();
                            result.put(asset.getAssetId(), new TermDepositAssetDtoV2(asset, asset.getAssetName(), null, null, null,
                                    null, null, null, interestBands, null));
                        }
                    }
                }
                return result;
            }
        };
        assetAnswerTD = new Answer<AssetDto>() {
            @Override
            public AssetDto answer(InvocationOnMock invocationOnMock) throws Throwable {
                Asset asset = (Asset)invocationOnMock.getArguments()[0];
                TreeSet<TermDepositInterestRate> termDepositInterestRates = (TreeSet<TermDepositInterestRate>)invocationOnMock.getArguments()[1];
                if (asset.getAssetType() == AssetType.MANAGED_PORTFOLIO) {
                    return new ManagedPortfolioAssetDto(asset);
                } else if (asset.getAssetType() == AssetType.MANAGED_FUND) {
                    ManagedFundAsset mfAsset = (ManagedFundAsset) asset;
                    return new ManagedFundAssetDto(mfAsset);
                } else if (asset.getAssetType() == AssetType.SHARE) {
                    ShareAsset shareAsset = (ShareAsset) asset;
                    return new ShareAssetDto(shareAsset);
                }else{
                    if(CollectionUtils.isNotEmpty(termDepositInterestRates) && isTDAssetPresentForSingleAsset(termDepositInterestRates, asset.getAssetId())){
                        List<InterestRateDto> interestBands = Collections.emptyList();
                        return new TermDepositAssetDtoV2(asset, asset.getAssetName(), termDepositInterestRates.first().getIssuerId(), termDepositInterestRates.first().getTerm().getMonths(),
                                new DateTime(),termDepositInterestRates.first().getPaymentFrequency().getDisplayName(), null, null, interestBands, null);
                    }
                    else{
                        return new TermDepositAssetDtoV2(asset, asset.getAssetName(), null, null,
                                new DateTime(),null, null, null, new ArrayList<InterestRateDto>(), null);
                    }
                }
            }
        };

        when(cmsService.getContent(Mockito.anyString())).thenReturn("disclaimer");
    }

    @Test
    public void testGetTermDepositOrders() throws IOException {
        Mockito.when(assetConverter.toAssetDto(anyMap(),anyList())).thenAnswer(assetAnswer);
        Mockito.when(assetConverter.toAssetDto(any(TermDepositAsset.class),any(TreeSet.class))).thenAnswer(assetAnswerTD);
        when(cmsService.getContent("DS-IP-0044")).thenReturn("contentDisclaimer");
        Map<String, String> params = new HashMap<>();
        String ordersFile = "/webservices/response/ordersV2.json";
        InputStream is = new FileInputStream(new File(OrderReportV2Test.class.getResource(ordersFile).getFile()));
        String orderjson = IOUtils.toString(is);
        params.put("order-group-details", orderjson);
        params.put("order-summary", "{\"totalBuys\":10000,\"totalSells\":0,\"netCashMovement\":-10000}");
        params.put("account-id", "D673F52FD27F0BC59A589FC2795BA6707EAEE563347509EC");

        OrderGroupDto orderGroupDto = orderReportV2.getTermDepositOrders(params);

        List<OrderItemDto> orderItems = orderGroupDto.getOrders();
        Assert.assertEquals(1, orderItems.size());
        Assert.assertEquals(orderReportV2.getPageLevelWarnings(params).size(), 1);
        Assert.assertNotNull(orderReportV2.getOrderSummary(params));
        Assert.assertEquals(new BigDecimal(10000), orderReportV2.getOrderSummary(params).getTotalBuys());

        OrderItemDto orderItem = orderItems.get(0);
        Assert.assertEquals("BT", orderItem.getAsset().getAssetName());
        Assert.assertEquals(new BigDecimal(5000), orderItem.getAmount());
        Assert.assertEquals("buy", orderItem.getOrderType());
        Assert.assertEquals("Term deposit", orderItem.getAssetType());
        Assert.assertNotNull(orderItem.getAsset());
        Assert.assertEquals(orderItem.getAsset().getAssetId(),"28100");

    }

    @Test
    public void testPageLevelWwarnings_WithEmptyList() throws IOException {

        Map<String, String> params = new HashMap<>();
        String ordersFile = "/webservices/response/ordersV2_EmptyWarnings.json";
        InputStream is = new FileInputStream(new File(OrderReportV2Test.class.getResource(ordersFile).getFile()));
        String orderjson = IOUtils.toString(is);
        params.put("order-group-details", orderjson);
        Assert.assertEquals(orderReportV2.getPageLevelWarnings(params).size(), 0);
    }


    @Test
    public void testOrderPreviewReport_Managedfunds() throws IOException {

        when(cmsService.getContent("DS-IP-0168")).thenReturn("orderDisclaimer");
        when(cmsService.getContent("DS-IP-0194")).thenReturn("contentDisclaimer");
        Map<String, String> params = new HashMap<>();
        String ordersFile = "/webservices/response/orders_mf.json";
        InputStream is = new FileInputStream(new File(OrderReportV2Test.class.getResource(ordersFile).getFile()));
        String orderjson = IOUtils.toString(is);
        params.put("order-group-details", orderjson);
        params.put("order-summary", "{\"totalBuys\":10000,\"totalSells\":0,\"netCashMovement\":-10000}");
        params.put("account-id", "D673F52FD27F0BC59A589FC2795BA6707EAEE563347509EC");

        ManagedFundAssetImpl asset3 = new ManagedFundAssetImpl();
        asset3.setAssetClass(AssetClass.CASH);
        asset3.setAssetType(AssetType.MANAGED_FUND);
        asset3.setAssetName("Aberdeen Actively Hedged International Equities Fund");
        asset3.setAssetId("28119");
        assets.put("28119", asset3);
        Mockito.when(assetConverter.toAssetDto(assets,null)).thenAnswer(assetAnswer);
        Mockito.when(assetConverter.toAssetDto(any(Asset.class),any(TreeSet.class))).thenAnswer(assetAnswerTD);

        OrderGroupDto orderGroupDto = orderReportV2.getManagedFundOrders(params);

        List<OrderItemDto> orderItems = orderGroupDto.getOrders();
        Assert.assertEquals(3, orderItems.size());
        Assert.assertEquals(orderReportV2.getPageLevelWarnings(params).size(), 1);
        Assert.assertNotNull(orderReportV2.getOrderSummary(params));
        Assert.assertEquals(new BigDecimal(10000), orderReportV2.getOrderSummary(params).getTotalBuys());
        OrderItemDto orderItem = orderItems.get(0);
        Assert.assertEquals(new BigDecimal(1000), orderItem.getAmount());
        Assert.assertEquals("buy", orderItem.getOrderType());
        Assert.assertEquals("Managed fund", orderItem.getAssetType());
        Assert.assertNotNull(orderItem.getAsset());
        Assert.assertEquals(orderItem.getAsset().getAssetId(),"28119");


    }

    @Test
    public void testOrderPreviewReport_Managedportfolio() throws IOException {
        when(cmsService.getContent("DS-IP-0168")).thenReturn("orderDisclaimer");
        when(cmsService.getContent("DS-IP-0194")).thenReturn("contentDisclaimer");
        Map<String,String> params = new HashMap<>();
        String ordersFile = "/webservices/response/orders_mp.json";
        InputStream is = new FileInputStream(new File(OrderReportV2Test.class.getResource(ordersFile).getFile()));
        String orderjson = IOUtils.toString(is);
        params.put("order-group-details", orderjson);
        params.put("order-summary", "{\"totalBuys\":10000,\"totalSells\":0,\"netCashMovement\":-10000}");
        params.put("account-id", "D673F52FD27F0BC59A589FC2795BA6707EAEE563347509EC");

        ManagedPortfolioAssetImpl asset3 = new ManagedPortfolioAssetImpl();
        asset3.setAssetClass(AssetClass.AUSTRALIAN_SHARES);
        asset3.setAssetType(AssetType.MANAGED_PORTFOLIO);
        asset3.setAssetName("BHP Billiton");
        asset3.setAssetCode("BHP");
        asset3.setAssetId("28118");
        assets.put("28118", asset3);

        Mockito.when(assetConverter.toAssetDto(assets,null)).thenAnswer(assetAnswer);
        Mockito.when(assetConverter.toAssetDto(any(Asset.class),any(TreeSet.class))).thenAnswer(assetAnswerTD);

        OrderGroupDto orderGroupDto = orderReportV2.getManagedPortfolioOrders(params);

        List<OrderItemDto> orderItems = orderGroupDto.getOrders();
        Assert.assertEquals(1, orderItems.size());
        Assert.assertEquals(orderReportV2.getPageLevelWarnings(params).size(), 1);
        Assert.assertNotNull(orderReportV2.getOrderSummary(params));
        Assert.assertEquals(new BigDecimal(10000), orderReportV2.getOrderSummary(params).getTotalBuys());
        OrderItemDto orderItem = orderItems.get(0);
        Assert.assertEquals(new BigDecimal(1000), orderItem.getAmount());
        Assert.assertEquals("buy", orderItem.getOrderType());
        Assert.assertEquals("Managed portfolio", orderItem.getAssetType());
        Assert.assertNotNull(orderItem.getAsset());
        Assert.assertEquals(orderItem.getAsset().getAssetId(),"28118");
        Assert.assertEquals(orderItem.getAsset().getAssetClass(),"Australian shares");

    }

    @Test
    public void testOrderPreviewReport_ListedSecurity() throws IOException {
        when(cmsService.getContent("DS-IP-0168")).thenReturn("orderDisclaimer");
        when(cmsService.getContent("DS-IP-0085")).thenReturn("feeDisclaimer");
        when(cmsService.getContent("DS-IP-0194")).thenReturn("contentDisclaimer");
        Map<String, String> params = new HashMap<>();
        String ordersFile = "/webservices/response/orders_ls.json";
        InputStream is = new FileInputStream(new File(OrderReportTest.class.getResource(ordersFile).getFile()));
        String orderjson = IOUtils.toString(is);
        params.put("order-group-details", orderjson);
        params.put("order-summary", "{\"totalBuys\":10000,\"totalSells\":0,\"netCashMovement\":-10000}");
        params.put("account-id", "D673F52FD27F0BC59A589FC2795BA6707EAEE563347509EC");
        Map<String, Object> dataCollections = new HashMap<>();

        ShareAssetImpl asset3 = new ShareAssetImpl();
        asset3.setAssetClass(AssetClass.AUSTRALIAN_SHARES);
        asset3.setAssetType(AssetType.SHARE);
        asset3.setAssetName("BHP Billiton");
        asset3.setAssetCode("BHP");
        asset3.setAssetId("28115");
        assets.put("28115", asset3);

        ShareAssetImpl asset4 = new ShareAssetImpl();
        asset4.setAssetClass(AssetClass.CASH);
        asset4.setAssetType(AssetType.SHARE);
        asset4.setAssetName("Aberdeen Actively Hedged International Equities Share");
        asset4.setAssetId("110486");
        assets.put("110486", asset4);

        Mockito.when(assetConverter.toAssetDto(assets,null)).thenAnswer(assetAnswer);
        Mockito.when(assetConverter.toAssetDto(any(Asset.class),any(TreeSet.class))).thenAnswer(assetAnswerTD);

        OrderGroupDto listedSecurityOrders = orderReportV2.getListedSecurityOrders(params);
        OrderGroupDto orderGroupDto = listedSecurityOrders;

        List<OrderItemDto> orderItems = orderGroupDto.getOrders();
        Assert.assertEquals(4, orderItems.size());

        Assert.assertEquals(orderReportV2.getPageLevelWarnings(params).size(), 1);
        Assert.assertNotNull(orderReportV2.getOrderSummary(params));
        Assert.assertEquals(new BigDecimal(10000), orderReportV2.getOrderSummary(params).getTotalBuys());
        OrderItemDto orderItem = orderItems.get(0);
        Assert.assertEquals(new BigInteger("1000"), orderItem.getUnits());
        Assert.assertEquals("buy", orderItem.getOrderType());
        Assert.assertEquals("Listed security", orderItem.getAssetType());
        Assert.assertNotNull(orderItem.getAsset());
        Assert.assertEquals(orderItem.getAsset().getAssetId(),"28115");
        Assert.assertEquals(orderItem.getAsset().getAssetType(),"Listed security");
    }
    private boolean isTDAssetPresent(List<TermDepositInterestRate> termDepositInterestRates, final String assetId) {
        TermDepositInterestRate termDepositInterestRate =  Lambda.selectFirst(termDepositInterestRates, new LambdaMatcher<TermDepositInterestRate>() {
            @Override
            protected boolean matchesSafely(TermDepositInterestRate termDepositInterestRate) {
                return assetId.equalsIgnoreCase(termDepositInterestRate.getAssetKey().getId());
            }
        });
        return termDepositInterestRate != null;
    }

    private boolean isTDAssetPresentForSingleAsset(TreeSet<TermDepositInterestRate> termDepositInterestRates, final String assetId) {
        TermDepositInterestRate termDepositInterestRate =  Lambda.selectFirst(termDepositInterestRates, new LambdaMatcher<TermDepositInterestRate>() {
            @Override
            protected boolean matchesSafely(TermDepositInterestRate termDepositInterestRate) {
                return assetId.equalsIgnoreCase(termDepositInterestRate.getAssetKey().getId());
            }
        });
        return termDepositInterestRate != null;
    }

}
