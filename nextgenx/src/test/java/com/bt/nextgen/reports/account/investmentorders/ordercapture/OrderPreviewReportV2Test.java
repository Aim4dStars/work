package com.bt.nextgen.reports.account.investmentorders.ordercapture;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.model.InterestRateDto;
import com.bt.nextgen.api.asset.model.ManagedFundAssetDto;
import com.bt.nextgen.api.asset.model.ManagedPortfolioAssetDto;
import com.bt.nextgen.api.asset.model.ShareAssetDto;
import com.bt.nextgen.api.asset.model.TermDepositAssetDto;
import com.bt.nextgen.api.asset.model.TermDepositAssetDtoV2;
import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.api.asset.service.AssetDtoConverterV2;
import com.bt.nextgen.api.termdeposit.model.TermDepositAssetRate;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.CacheManagedTermDepositAssetRateIntegrationService;
import com.bt.nextgen.service.avaloq.asset.ManagedFundAssetImpl;
import com.bt.nextgen.service.avaloq.asset.ManagedPortfolioAssetImpl;
import com.bt.nextgen.service.avaloq.asset.ShareAssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationServiceFactory;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.bt.nextgen.service.integration.asset.ManagedFundAsset;
import com.bt.nextgen.service.integration.asset.ShareAsset;
import com.bt.nextgen.service.integration.asset.TermDepositAsset;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRate;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRateImpl;
import com.bt.nextgen.termdeposit.service.TermDepositAssetRateSearchKey;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.PaymentFrequency;
import com.bt.nextgen.service.integration.asset.Term;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.integration.broker.BrokerType;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import net.sf.jasperreports.engine.Renderable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.anyString;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class OrderPreviewReportV2Test {

    @InjectMocks
    private OrderPreviewReportV2 orderPreviewReportV2;

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

    @Mock
    private Configuration configuration;

    @Mock
    private AccountIntegrationServiceFactory accountIntegrationServiceFactory;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();


    private static final String REPORT_TITLE = "Your client authorisation for an investment order";
    private static final String REPORT_SUBTITLE = "Order instructions";
    private static final String REPORT_PORTFOLIO_FEE = " and portfolio management fee";
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
        mockAccountService();
        mockBrokerService();
        mockAssetService();

        when(accountIntegrationServiceFactory.getInstance(anyString())).thenReturn(accountService);
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
        wrapAccountDetail.setAccountNumber("123456789");
        wrapAccountDetail.setAccountKey(AccountKey.valueOf("123456789"));
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
                   return null;
                }
        }
        };

        when(cmsService.getContent(Mockito.anyString())).thenReturn("disclaimer");
    }

    @Test
    public void testOrderPreviewReport() throws IOException {
        Mockito.when(assetConverter.toAssetDto(anyMap(),anyList())).thenAnswer(assetAnswer);
        Mockito.when(assetConverter.toAssetDto(any(TermDepositAsset.class),any(TreeSet.class))).thenAnswer(assetAnswerTD);
        when(cmsService.getContent("DS-IP-0194")).thenReturn("contentDisclaimer");
        Map<String, Object> params = new HashMap<>();
        String ordersFile = "/webservices/response/ordersV2.json";
        InputStream is = new FileInputStream(new File(OrderReportTest.class.getResource(ordersFile).getFile()));
        String orderjson = IOUtils.toString(is);
        params.put("order-group-details", orderjson);
        params.put("order-summary", "{\"totalBuys\":10000,\"totalSells\":0,\"netCashMovement\":-10000}");
        params.put("account-id", "D673F52FD27F0BC59A589FC2795BA6707EAEE563347509EC");
        Map<String, Object> dataCollections = new HashMap<>();

        Collection<OrderGroupReportData> orderGroups = (Collection<OrderGroupReportData>) orderPreviewReportV2.getData(params,
                dataCollections);
        OrderGroupReportData orderGroupData = orderGroups.iterator().next();

        List<OrderItemData> orderItems = orderGroupData.getChildren();
        Assert.assertEquals(1, orderItems.size());

        OrderItemData orderItem = orderItems.get(0);
        Assert.assertEquals("BT 3 months term deposit", orderItem.getAssetName());
        Assert.assertEquals("$5,000.00", orderItem.getAmount());
        Assert.assertEquals("Buy", orderItem.getOrderType());
        Assert.assertEquals("at maturity", orderItem.getInterestPaymentFrequency());
        Assert.assertEquals("0.00%", orderItem.getRate());
        Assert.assertEquals(1, orderItem.getWarnings().size());
        Assert.assertEquals("Term deposit", orderItem.getAssetType());

        Assert.assertEquals("$10,000.00", orderGroupData.getTotalBuys());
        Assert.assertEquals("$0.00", orderGroupData.getTotalSells());
        Assert.assertEquals("-$10,000.00", orderGroupData.getNetCashMovement());
        Assert.assertEquals(0, orderGroupData.getWarnings().size());
        Assert.assertTrue(orderPreviewReportV2.getDisclaimer(params, dataCollections).contains("contentDisclaimer"));
        Assert.assertEquals(orderPreviewReportV2.getReportSubTitle(),REPORT_SUBTITLE);
        Assert.assertEquals(orderPreviewReportV2.getReportTitle(params, dataCollections),REPORT_TITLE);
    }

    @Test
    public void testOrderPreviewReportForInvalidJSON() throws IOException {
        Mockito.when(assetConverter.toAssetDto(anyMap(),anyList())).thenAnswer(assetAnswer);
        Mockito.when(assetConverter.toAssetDto(any(TermDepositAsset.class),any(TreeSet.class))).thenAnswer(assetAnswerTD);
        when(cmsService.getContent("DS-IP-0194")).thenReturn("contentDisclaimer");
        Map<String, Object> params = new HashMap<>();
        String ordersFile = "/webservices/response/InvalidOrder.json";
        InputStream is = new FileInputStream(new File(OrderReportTest.class.getResource(ordersFile).getFile()));
        String orderjson = IOUtils.toString(is);
        params.put("order-group-details", orderjson);
        params.put("order-summary", "{\"totalBuys\":10000,\"totalSells\":0,\"netCashMovement\":-10000}");
        params.put("account-id", "D673F52FD27F0BC59A589FC2795BA6707EAEE563347509EC");
        Map<String, Object> dataCollections = new HashMap<>();
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("JSON Message illegal");

        orderPreviewReportV2.getData(params,dataCollections);



    }


    @Test
    public void testOrderPreviewReport_Managedfunds() throws IOException {

        when(cmsService.getContent("DS-IP-0168")).thenReturn("orderDisclaimer");
        when(cmsService.getContent("DS-IP-0194")).thenReturn("contentDisclaimer");
        Map<String, Object> params = new HashMap<>();
        String ordersFile = "/webservices/response/orders_mf.json";
        InputStream is = new FileInputStream(new File(OrderReportTest.class.getResource(ordersFile).getFile()));
        String orderjson = IOUtils.toString(is);
        params.put("order-group-details", orderjson);
        params.put("order-summary", "{\"totalBuys\":10000,\"totalSells\":0,\"netCashMovement\":-10000}");
        params.put("account-id", "D673F52FD27F0BC59A589FC2795BA6707EAEE563347509EC");
        Map<String, Object> dataCollections = new HashMap<>();

        ManagedFundAssetImpl asset3 = new ManagedFundAssetImpl();
        asset3.setAssetClass(AssetClass.CASH);
        asset3.setAssetType(AssetType.MANAGED_FUND);
        asset3.setAssetName("Aberdeen Actively Hedged International Equities Fund");
        asset3.setAssetId("28119");
        assets.put("28119", asset3);
        Mockito.when(assetConverter.toAssetDto(assets,null)).thenAnswer(assetAnswer);
        Mockito.when(assetConverter.toAssetDto(any(Asset.class),any(TreeSet.class))).thenAnswer(assetAnswerTD);
        managedFundAssetDto = new ManagedFundAssetDto(asset3);



        Collection<OrderGroupReportData> orderGroups = (Collection<OrderGroupReportData>) orderPreviewReportV2.getData(params,
                dataCollections);
        OrderGroupReportData orderGroupData = orderGroups.iterator().next();

        List<OrderItemData> orderItems = orderGroupData.getChildren();

        OrderItemData orderItem = orderItems.get(0);
        Assert.assertTrue(orderItem.getAssetName().contains("Aberdeen Actively Hedged International Equities Fund"));
        Assert.assertEquals("$0.00", orderItem.getTransactionFee());
        Assert.assertEquals("$1,000.00", orderItem.getAmount());
        Assert.assertEquals("Cash", orderItem.getDistributionMethod());
        Assert.assertNull(orderItem.getIncomePreference());

        String disclaimer = orderPreviewReportV2.getDisclaimer(params, dataCollections);
        Assert.assertTrue(disclaimer.contains("orderDisclaimer"));
        Assert.assertTrue(disclaimer.contains("contentDisclaimer"));

        // order 2 with admin fee
        OrderItemData orderItem2 = orderItems.get(1);
        Assert.assertEquals("$50.00", orderItem2.getTransactionFee());

        // order 3 with null admin fee
        OrderItemData orderItem3 = orderItems.get(2);
        Assert.assertEquals("$0.00", orderItem3.getTransactionFee());


    }

    @Test
    public void testOrderPreviewReport_Share() throws IOException {
        when(cmsService.getContent("DS-IP-0168")).thenReturn("orderDisclaimer");
        when(cmsService.getContent("DS-IP-0085")).thenReturn("feeDisclaimer");
        when(cmsService.getContent("DS-IP-0194")).thenReturn("contentDisclaimer");
        Map<String, Object> params = new HashMap<>();
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

        shareAssetDto = new ShareAssetDto(asset3, Collections.singletonList((DistributionMethod.CASH)));

        Mockito.when(assetConverter.toAssetDto(assets,null)).thenAnswer(assetAnswer);
        Mockito.when(assetConverter.toAssetDto(any(Asset.class),any(TreeSet.class))).thenAnswer(assetAnswerTD);

        Collection<OrderGroupReportData> orderGroups = (Collection<OrderGroupReportData>) orderPreviewReportV2.getData(params,
                dataCollections);
        OrderGroupReportData orderGroupData = orderGroups.iterator().next();

        List<OrderItemData> orderItems = orderGroupData.getChildren();

        OrderItemData orderItem = orderItems.get(0);
        Assert.assertTrue(orderItem.getAssetName().contains("BHP Billiton"));
        Assert.assertEquals("$0.00*", orderItem.getTransactionFee());
        Assert.assertEquals("$1,000.00", orderItem.getAmount());
        Assert.assertEquals("Cash", orderItem.getDistributionMethod());
        Assert.assertEquals("1,000", orderItem.getUnits());
        Assert.assertEquals("GFD", orderItem.getExpiry());
        Assert.assertEquals("Limit", orderItem.getPriceType());
        Assert.assertEquals("Cash", orderItem.getDistributionMethod());
        Assert.assertEquals("$10.000", orderItem.getLimitPrice());
        Assert.assertEquals(true, orderItem.getIsEstimated());
        Assert.assertEquals(true, orderItem.getShowDistributionMethod());
        Assert.assertNull(orderItem.getIncomePreference());

        OrderItemData sellItem = orderItems.get(1);
        Assert.assertEquals("Sell", sellItem.getOrderType());
        Assert.assertEquals(false, sellItem.getShowDistributionMethod());

        String disclaimer = orderPreviewReportV2.getDisclaimer(params, dataCollections);
        Assert.assertTrue(disclaimer.contains("orderDisclaimer"));
        Assert.assertTrue(disclaimer.contains("feeDisclaimer"));
        Assert.assertTrue(disclaimer.contains("contentDisclaimer"));
    }

    @Test
    public void testOrderPreviewReport_Managedportfolio() throws IOException {
        when(cmsService.getContent("DS-IP-0168")).thenReturn("orderDisclaimer");
        when(cmsService.getContent("DS-IP-0194")).thenReturn("contentDisclaimer");
        Map<String, Object> params = new HashMap<>();
        String ordersFile = "/webservices/response/orders_mp.json";
        InputStream is = new FileInputStream(new File(OrderReportTest.class.getResource(ordersFile).getFile()));
        String orderjson = IOUtils.toString(is);
        params.put("order-group-details", orderjson);
        params.put("order-summary", "{\"totalBuys\":10000,\"totalSells\":0,\"netCashMovement\":-10000}");
        params.put("account-id", "D673F52FD27F0BC59A589FC2795BA6707EAEE563347509EC");
        Map<String, Object> dataCollections = new HashMap<>();

        ManagedPortfolioAssetImpl asset3 = new ManagedPortfolioAssetImpl();
        asset3.setAssetClass(AssetClass.AUSTRALIAN_SHARES);
        asset3.setAssetType(AssetType.MANAGED_PORTFOLIO);
        asset3.setAssetName("BHP Billiton");
        asset3.setAssetCode("BHP");
        asset3.setAssetId("28118");
        assets.put("28118", asset3);

        mpAssetDto = new ManagedPortfolioAssetDto(asset3);
        Mockito.when(assetConverter.toAssetDto(assets,null)).thenAnswer(assetAnswer);
        Mockito.when(assetConverter.toAssetDto(any(Asset.class),any(TreeSet.class))).thenAnswer(assetAnswerTD);

        Collection<OrderGroupReportData> orderGroups = (Collection<OrderGroupReportData>) orderPreviewReportV2.getData(params,
                dataCollections);
        OrderGroupReportData orderGroupData = orderGroups.iterator().next();

        List<OrderItemData> orderItems = orderGroupData.getChildren();

        OrderItemData orderItem = orderItems.get(0);
        Assert.assertTrue(orderItem.getAssetName().contains("BHP Billiton"));
        Assert.assertEquals("$1,000.00", orderItem.getAmount());
        Assert.assertEquals("$1,000.00", orderItem.getEstimated());
        Assert.assertEquals("12121", orderItem.getOrderId());
        Assert.assertEquals("Reinvest", orderItem.getIncomePreference());

        String disclaimer = orderPreviewReportV2.getDisclaimer(params, dataCollections);
        Assert.assertTrue(disclaimer.contains("orderDisclaimer"));
        Assert.assertTrue(disclaimer.contains("contentDisclaimer"));

        String reportTitle = orderPreviewReportV2.getReportTitle(params, dataCollections);
        Assert.assertEquals(reportTitle,REPORT_TITLE);

        String reportFileName = orderPreviewReportV2.getReportFileName(params,dataCollections);
        Assert.assertNotNull(reportFileName);
        Assert.assertEquals(reportFileName,"123456789 - Order Authorisation");

        when(cmsService.getContent(anyString())).thenReturn("cms/");
        when(configuration.getString(Mockito.anyString())).thenReturn("classpath:cms/");

        Renderable renderable = orderPreviewReportV2.getWarningIconImageV2();
        Assert.assertNotNull(renderable);

    }

    @Test
    public void testOrderPreviewReport_Tailoredportfolio() throws IOException {
        Map<String, Object> params = new HashMap<>();
        String ordersFile = "/webservices/response/orders_tmp.json";
        InputStream is = new FileInputStream(new File(OrderReportTest.class.getResource(ordersFile).getFile()));
        String orderjson = IOUtils.toString(is);
        params.put("order-group-details", orderjson);
        params.put("order-summary", "{\"totalBuys\":10000,\"totalSells\":0,\"netCashMovement\":-10000}");
        params.put("account-id", "D673F52FD27F0BC59A589FC2795BA6707EAEE563347509EC");
        Map<String, Object> dataCollections = new HashMap<>();

        ManagedPortfolioAssetImpl asset3 = new ManagedPortfolioAssetImpl();
        asset3.setAssetClass(AssetClass.AUSTRALIAN_SHARES);
        asset3.setAssetType(AssetType.MANAGED_PORTFOLIO);
        asset3.setAssetName("BHP Billiton");
        asset3.setAssetCode("BHP");
        asset3.setAssetId("28112");
        assets.put("28112", asset3);

        mpAssetDto = new ManagedPortfolioAssetDto(asset3);

        Mockito.when(assetConverter.toAssetDto(assets,null)).thenAnswer(assetAnswer);
        Mockito.when(assetConverter.toAssetDto(any(Asset.class),any(TreeSet.class))).thenAnswer(assetAnswerTD);

        Collection<OrderGroupReportData> orderGroups = (Collection<OrderGroupReportData>) orderPreviewReportV2.getData(params,
                dataCollections);
        OrderGroupReportData orderGroupData = orderGroups.iterator().next();

        List<OrderItemData> orderItems = orderGroupData.getChildren();

        OrderItemData orderItem = orderItems.get(0);
        Assert.assertTrue(orderItem.getAssetName().contains("BHP Billiton"));
        Assert.assertEquals("$1,000.00", orderItem.getAmount());
        Assert.assertEquals("$1,000.00", orderItem.getEstimated());
        Assert.assertEquals(2, orderItem.getSlidingScaleTierData().size());
        Assert.assertEquals("1.00%", orderItem.getSlidingScaleTierData().get(0).getRate());
        Assert.assertEquals(false, orderItem.getIsEstimated());
        Assert.assertEquals("Transfer to BT CMA", orderItem.getIncomePreference());
        Assert.assertNotNull(orderPreviewReportV2.getReportTitle(params,dataCollections));
        Assert.assertEquals(orderPreviewReportV2.getReportTitle(params,dataCollections),REPORT_TITLE.concat(REPORT_PORTFOLIO_FEE));
        Assert.assertEquals(orderPreviewReportV2.getReportType(params,dataCollections),REPORT_TITLE);
    }

    @Test
    public void test_getPortfolioFeeDisclaimer() throws IOException {

        when(cmsService.getContent("DS-IP-0191")).thenReturn("superDisclaimer");
        when(cmsService.getContent("DS-IP-0170")).thenReturn("investmentDisclaimer");

        Map<String, Object> params = new HashMap<>();
        params.put("order-summary", "{\"totalBuys\":10000,\"totalSells\":0,\"netCashMovement\":-10000}");
        params.put("account-id", "D673F52FD27F0BC59A589FC2795BA6707EAEE563347509EC");

        OrderGroupReportData groupReportData = Mockito.mock(OrderGroupReportData.class);
        when(groupReportData.isPortfolioFeePresent()).thenReturn(Boolean.TRUE);
        Map<String, Object> dataCollections = new HashMap<>();
        dataCollections.put("AbstractOrderReport.orderGroupData", groupReportData);

        String disclaimer = orderPreviewReportV2.getDisclaimer(params, dataCollections);
        Assert.assertTrue(disclaimer.contains("investmentDisclaimer"));

        wrapAccountDetail.setAccountStructureType(AccountStructureType.SUPER);
        disclaimer = orderPreviewReportV2.getDisclaimer(params, dataCollections);
        Assert.assertTrue(disclaimer.contains("superDisclaimer"));
    }

    @Test
    public void test_getPortfolioFeeDeclaration() throws IOException {

        when(cmsService.getContent("DS-IP-0192")).thenReturn("superDeclaration");
        when(cmsService.getContent("DS-IP-0171")).thenReturn("investmentDeclaration");

        Map<String, Object> params = new HashMap<>();
        params.put("account-id", "D673F52FD27F0BC59A589FC2795BA6707EAEE563347509EC");
        OrderGroupReportData groupReportData = Mockito.mock(OrderGroupReportData.class);
        when(groupReportData.isPortfolioFeePresent()).thenReturn(Boolean.TRUE);
        Map<String, Object> dataCollections = new HashMap<>();
        dataCollections.put("AbstractOrderReport.orderGroupData", groupReportData);

        String declaration = orderPreviewReportV2.getDeclaration(params, dataCollections);
        Assert.assertTrue(declaration.contains("investmentDeclaration"));

        wrapAccountDetail.setAccountStructureType(AccountStructureType.SUPER);
        declaration = orderPreviewReportV2.getDeclaration(params, dataCollections);
        Assert.assertTrue(declaration.contains("superDeclaration"));
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
