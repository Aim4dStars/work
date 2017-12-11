package com.bt.nextgen.reports.account.investmentorders.ordercapture;

import com.bt.nextgen.api.asset.model.InterestRateDto;
import com.bt.nextgen.api.asset.model.ManagedFundAssetDto;
import com.bt.nextgen.api.asset.model.ManagedPortfolioAssetDto;
import com.bt.nextgen.api.asset.model.ShareAssetDto;
import com.bt.nextgen.api.asset.model.TermDepositAssetDto;
import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
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
import com.bt.nextgen.service.integration.asset.PaymentFrequency;
import com.bt.nextgen.service.integration.asset.Term;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.options.model.OptionKey;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.service.integration.broker.BrokerType;
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
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderReportTest {
    @InjectMocks
    private OrderPreviewReport orderPreviewReport;

    @Mock
    private BrokerIntegrationService brokerService;

    @Mock
    private AssetDtoConverter assetConverter;

    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private AssetIntegrationService assetService;

    @Mock
    private OptionsService optionsService;

    @Mock
    private CmsService cmsService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private AccountIntegrationServiceFactory accountIntegrationServiceFactory;

    private final Map<String, Asset> assets = new HashMap<>();
    private Map<String, TermDepositAssetDetail> termDepositDetailMap;
    private TermDepositAssetDto termDepositAssetDto;
    private ManagedFundAssetDto managedFundAssetDto;
    private ShareAssetDto shareAssetDto;
    private ManagedPortfolioAssetDto mpAssetDto;
    private WrapAccountDetailImpl wrapAccountDetail;

    @Before
    public void setup() {
        mockAccountService();
        mockBrokerService();
        mockAssetService();

        when(optionsService.hasFeature(any(OptionKey.class), any(AccountKey.class), any(ServiceErrorsImpl.class)))
                .thenReturn(Boolean.TRUE);

        when(accountIntegrationService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(wrapAccountDetail);
        when(accountIntegrationServiceFactory.getInstance(anyString())).thenReturn(accountIntegrationService);
    }

    private void mockBrokerService() {
        BrokerImpl broker = new BrokerImpl(BrokerKey.valueOf("23333"), BrokerType.ADVISER);
        when(brokerService.getBroker((Matchers.any(BrokerKey.class)), any(ServiceErrorsImpl.class))).thenReturn(broker);
    }

    private void mockAccountService() {
        wrapAccountDetail = new WrapAccountDetailImpl();
        wrapAccountDetail.setAccountName("test1");
        wrapAccountDetail.setAccountNumber("0");
        when(accountService.loadWrapAccountDetail(Mockito.any(AccountKey.class), any(ServiceErrorsImpl.class)))
                .thenReturn(wrapAccountDetail);
    }

    private void mockAssetService() {
        AssetImpl asset1 = new TermDepositAssetImpl();
        asset1.setAssetClass(AssetClass.CASH);
        asset1.setAssetType(AssetType.TERM_DEPOSIT);
        asset1.setAssetName("BT Term deposit 3 months");
        asset1.setAssetId("40764");
        asset1.setBrand("BT");
        assets.put("40764", asset1);
        AssetImpl asset2 = new TermDepositAssetImpl();
        asset2.setAssetClass(AssetClass.CASH);
        asset2.setAssetType(AssetType.TERM_DEPOSIT);
        asset2.setAssetName("BT Term deposit 4 months");
        asset2.setAssetId("28117");
        asset2.setBrand("BT");
        assets.put("28117", asset2);

        when(assetService.loadAssets(Mockito.anyCollection(), any(ServiceErrorsImpl.class))).thenReturn(assets);
        termDepositDetailMap = new HashMap<>();
        termDepositDetailMap.put("28117", new TermDepositAssetDetail() {

            @Override
            public Term getTerm() {
                return new Term("4 months");
            }

            @Override
            public PaymentFrequency getPaymentFrequency() {
                return PaymentFrequency.AT_MATURITY;
            }

            @Override
            public String getIssuer() {
                return "BT";
            }

            @Override
            public TreeSet<InterestRate> getInterestRates() {
                return null;
            }

            @Override
            public String getAssetId() {
                return "28117";
            }
        });

        when(assetService.loadTermDepositRates(Mockito.any(BrokerKey.class), Mockito.any(DateTime.class), Mockito.anyList(),
                any(ServiceErrorsImpl.class))).thenReturn(termDepositDetailMap);
        InterestRateDto intrRateDto = new InterestRateDto(BigDecimal.valueOf(1.50), BigDecimal.ZERO, BigDecimal.valueOf(100000));
        termDepositAssetDto = new TermDepositAssetDto(asset2, "BT", "BT", new Integer(3), new DateTime(), "At maturity",
                BigDecimal.ZERO, BigDecimal.valueOf(100000), Collections.singletonList(intrRateDto), BigDecimal.valueOf(1.25));
        when(assetConverter.toAssetDto(Mockito.any(Asset.class), Mockito.any(TermDepositAssetDetail.class)))
                .thenReturn(termDepositAssetDto);
        when(cmsService.getContent(Mockito.anyString())).thenReturn("disclaimer");
    }

    @Test
    public void testOrderPreviewReport() throws IOException {
        when(cmsService.getContent("DS-IP-0194")).thenReturn("contentDisclaimer");
        Map<String, Object> params = new HashMap<>();
        String ordersFile = "/webservices/response/orders.json";
        InputStream is = new FileInputStream(new File(OrderReportTest.class.getResource(ordersFile).getFile()));
        String orderjson = IOUtils.toString(is);
        params.put("order-group-details", orderjson);
        params.put("order-summary", "{\"totalBuys\":10000,\"totalSells\":0,\"netCashMovement\":-10000}");
        params.put("account-id", "D673F52FD27F0BC59A589FC2795BA6707EAEE563347509EC");
        Map<String, Object> dataCollections = new HashMap<>();

        Collection<OrderGroupReportData> orderGroups = (Collection<OrderGroupReportData>) orderPreviewReport.getData(params,
                dataCollections);
        OrderGroupReportData orderGroupData = orderGroups.iterator().next();

        List<OrderItemData> orderItems = orderGroupData.getChildren();
        Assert.assertEquals(1, orderItems.size());

        OrderItemData orderItem = orderItems.get(0);
        Assert.assertEquals("BT 3 months term deposit", orderItem.getAssetName());
        Assert.assertEquals("$5,000.00", orderItem.getAmount());
        Assert.assertEquals("Buy", orderItem.getOrderType());
        Assert.assertEquals("At maturity", orderItem.getInterestPaymentFrequency());
        Assert.assertEquals("0.00%", orderItem.getRate());
        Assert.assertEquals(1, orderItem.getWarnings().size());
        Assert.assertEquals("Term deposit", orderItem.getAssetType());

        Assert.assertEquals("$10,000.00", orderGroupData.getTotalBuys());
        Assert.assertEquals("$0.00", orderGroupData.getTotalSells());
        Assert.assertEquals("-$10,000.00", orderGroupData.getNetCashMovement());
        Assert.assertEquals(0, orderGroupData.getWarnings().size());
        Assert.assertTrue(orderPreviewReport.getDisclaimer(params, dataCollections).contains("contentDisclaimer"));
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



        managedFundAssetDto = new ManagedFundAssetDto(asset3);

        when(assetConverter.toAssetDto(Mockito.any(Asset.class), Mockito.any(TermDepositAssetDetail.class)))
                .thenReturn(managedFundAssetDto);

        Collection<OrderGroupReportData> orderGroups = (Collection<OrderGroupReportData>) orderPreviewReport.getData(params,
                dataCollections);
        OrderGroupReportData orderGroupData = orderGroups.iterator().next();

        List<OrderItemData> orderItems = orderGroupData.getChildren();

        OrderItemData orderItem = orderItems.get(0);
        Assert.assertTrue(orderItem.getAssetName().contains("Aberdeen Actively Hedged International Equities Fund"));
        Assert.assertEquals("$0.00", orderItem.getTransactionFee());
        Assert.assertEquals("$1,000.00", orderItem.getAmount());
        Assert.assertEquals("Cash", orderItem.getDistributionMethod());
        Assert.assertNull(orderItem.getIncomePreference());

        String disclaimer = orderPreviewReport.getDisclaimer(params, dataCollections);
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

        shareAssetDto = new ShareAssetDto(asset3, Collections.singletonList((DistributionMethod.CASH)));

        when(assetConverter.toAssetDto(Mockito.any(Asset.class), Mockito.any(TermDepositAssetDetail.class)))
                .thenReturn(shareAssetDto);

        Collection<OrderGroupReportData> orderGroups = (Collection<OrderGroupReportData>) orderPreviewReport.getData(params,
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
        Assert.assertEquals("-", sellItem.getLimitPrice());
        Assert.assertEquals(false, sellItem.getShowDistributionMethod());

        OrderItemData buyItem = orderItems.get(2);
        Assert.assertEquals(false, buyItem.getShowDistributionMethod());

        OrderItemData sellAllItem = orderItems.get(3);
        Assert.assertEquals("Sell all", sellAllItem.getAmount());
        Assert.assertEquals("$20.00", sellAllItem.getRawAmount());
        Assert.assertEquals("Sell all (20)", sellAllItem.getUnits());
        Assert.assertEquals(true, sellAllItem.getIsEstimated());

        String disclaimer = orderPreviewReport.getDisclaimer(params, dataCollections);
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
        asset3.setAssetType(AssetType.SHARE);
        asset3.setAssetName("BHP Billiton");
        asset3.setAssetCode("BHP");
        asset3.setAssetId("28118");
        assets.put("28118", asset3);

        mpAssetDto = new ManagedPortfolioAssetDto(asset3);

        when(assetConverter.toAssetDto(Mockito.any(Asset.class), Mockito.any(TermDepositAssetDetail.class)))
                .thenReturn(mpAssetDto);

        Collection<OrderGroupReportData> orderGroups = (Collection<OrderGroupReportData>) orderPreviewReport.getData(params,
                dataCollections);
        OrderGroupReportData orderGroupData = orderGroups.iterator().next();

        List<OrderItemData> orderItems = orderGroupData.getChildren();

        OrderItemData orderItem = orderItems.get(0);
        Assert.assertTrue(orderItem.getAssetName().contains("BHP Billiton"));
        Assert.assertEquals("$1,000.00", orderItem.getAmount());
        Assert.assertEquals("$1,000.00", orderItem.getEstimated());
        Assert.assertEquals("12121", orderItem.getOrderId());
        Assert.assertEquals("Reinvest", orderItem.getIncomePreference());

        String disclaimer = orderPreviewReport.getDisclaimer(params, dataCollections);
        Assert.assertTrue(disclaimer.contains("orderDisclaimer"));
        Assert.assertTrue(disclaimer.contains("contentDisclaimer"));
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
        asset3.setAssetType(AssetType.SHARE);
        asset3.setAssetName("BHP Billiton");
        asset3.setAssetCode("BHP");
        asset3.setAssetId("28112");
        assets.put("28112", asset3);

        mpAssetDto = new ManagedPortfolioAssetDto(asset3);

        when(assetConverter.toAssetDto(Mockito.any(Asset.class), Mockito.any(TermDepositAssetDetail.class)))
                .thenReturn(mpAssetDto);

        Collection<OrderGroupReportData> orderGroups = (Collection<OrderGroupReportData>) orderPreviewReport.getData(params,
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

        String disclaimer = orderPreviewReport.getDisclaimer(params, dataCollections);
        Assert.assertTrue(disclaimer.contains("investmentDisclaimer"));

        wrapAccountDetail.setAccountStructureType(AccountStructureType.SUPER);
        disclaimer = orderPreviewReport.getDisclaimer(params, dataCollections);
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

        String declaration = orderPreviewReport.getDeclaration(params, dataCollections);
        Assert.assertTrue(declaration.contains("investmentDeclaration"));

        wrapAccountDetail.setAccountStructureType(AccountStructureType.SUPER);
        declaration = orderPreviewReport.getDeclaration(params, dataCollections);
        Assert.assertTrue(declaration.contains("superDeclaration"));
    }

    @Test
    public void testGetDisclaimer_whenStandaloneCma_thenCmaDisclaimerReturned() throws IOException {
        when(optionsService.hasFeature(any(OptionKey.class), any(AccountKey.class), any(ServiceErrorsImpl.class)))
                .thenReturn(Boolean.FALSE);
        when(cmsService.getContent("DS-IP-0198")).thenReturn("cma disclaimer");

        Map<String, Object> params = new HashMap<>();
        params.put("account-id", "D673F52FD27F0BC59A589FC2795BA6707EAEE563347509EC");
        OrderGroupReportData groupReportData = Mockito.mock(OrderGroupReportData.class);
        Map<String, Object> dataCollections = new HashMap<>();
        dataCollections.put("AbstractOrderReport.orderGroupData", groupReportData);

        String disclaimer = orderPreviewReport.getDisclaimer(params, dataCollections);
        Assert.assertTrue(disclaimer.contains("cma disclaimer"));
    }

    @Test
    public void testGetReportFilename() {
        Map<String, Object> params = new HashMap<>();
        params.put("account-id", "D673F52FD27F0BC59A589FC2795BA6707EAEE563347509EC");
        OrderGroupReportData groupReportData = Mockito.mock(OrderGroupReportData.class);
        Map<String, Object> dataCollections = new HashMap<>();
        dataCollections.put("AbstractOrderReport.orderGroupData", groupReportData);

        String reportFilename = orderPreviewReport.getReportFileName(params, dataCollections);
        Assert.assertEquals("0 - Order Authorisation", reportFilename);
    }
}
