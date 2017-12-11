package com.bt.nextgen.api.termdeposit.service;

import com.bt.nextgen.api.termdeposit.model.Badge;
import com.bt.nextgen.api.termdeposit.model.TermDepositAssetRate;
import com.bt.nextgen.api.termdeposit.model.TermDepositBankRates;
import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorAccountKey;
import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorDealerKey;
import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorDto;
import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorKey;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.domain.key.StringIdKey;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.SubAccountImpl;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetDetailImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetDetailImpl.InterestRateImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.avaloq.product.CacheAvaloqProductIntegrationServiceImpl;
import com.bt.nextgen.service.avaloq.product.ProductIdentifierImpl;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.bt.nextgen.service.avaloq.product.ProductLevel;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.btfin.panorama.service.integration.account.SubAccount;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.PaymentFrequency;
import com.bt.nextgen.service.integration.asset.Term;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail.InterestRate;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.termdeposit.web.model.TermDepositRateModel;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerType;
import org.hamcrest.core.Is;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TermDepositCalculatorDtoServiceTest {
    @InjectMocks
    private TermDepositCalculatorDtoServiceImpl termDepositService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private AssetIntegrationService assetService;

    @Mock
    private BankDateIntegrationService bankDateIntegrationService;

    @Mock
    private TermDepositCalculatorUtils termDepositCalculatorUtils;

    @Mock
    private CmsService cmsService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private AccountIntegrationService avaloqAccountIntegrationService;

    @Mock
    private CacheAvaloqProductIntegrationServiceImpl cachedProducts;

    @Mock
    UserProfileService userProfileService;

    @Mock
    public AccountIntegrationService accountService;

    @Mock
    private BrokerHelperService brokerHelperService;

    private WrapAccountImpl account;
    private SubAccountImpl directSubAccount;
    private ProductImpl product1;
    private ProductImpl investmentProduct;
    List<Product> dgProductList;
    List<Asset> assetList;
    private Map<String, TermDepositAssetDetail> assetRateMap;
    private InterestRateImpl interestRateTier2;
    private TermDepositAssetDetailImpl termDepositAssetDetail1;

    @Before
    public void setup() {
        dgProductList = new ArrayList<>();
        product1 = new ProductImpl();
        product1.setProductKey(ProductKey.valueOf("1"));
        product1.setProductName("BT Panorama Super");
        product1.setParentProductId("11");
        product1.setProductTypeId("btfg$model");
        product1.setProductLevel(ProductLevel.MODEL);
        investmentProduct = new ProductImpl();
        investmentProduct.setProductKey(ProductKey.valueOf("2"));
        investmentProduct.setProductName("BT Panorama Investments");
        investmentProduct.setParentProductId("1");
        investmentProduct.setProductTypeId("btfg$model");
        investmentProduct.setProductLevel(ProductLevel.MODEL);
        investmentProduct.setDirect(true);
        final ProductImpl product3 = new ProductImpl();
        product3.setProductKey(ProductKey.valueOf("3"));
        product3.setProductName("BT Panorama Pension");
        product3.setParentProductId("2");
        product3.setDirect(true);
        product3.setProductTypeId("btfg$model");
        product3.setProductLevel(ProductLevel.MODEL);
        dgProductList.add(product1);
        dgProductList.add(investmentProduct);
        dgProductList.add(product3);
        final Map<StringIdKey, List<Product>> productMap = new HashMap<>();
        productMap.put(BrokerKey.valueOf("71259"), dgProductList);
        // when(cachedProducts.loadAdvisorProducts(any(ServiceErrors.class))).thenReturn(productMap);
        when(productIntegrationService.getDealerGroupProductList(any(BrokerKey.class), any(ServiceErrors.class)))
                .thenReturn(dgProductList);
        when(productIntegrationService.loadProducts(any(ServiceErrors.class))).thenReturn(dgProductList);
        assetList = new ArrayList<>();
        final AssetImpl asset1 = new AssetImpl();
        asset1.setAssetId("9");
        asset1.setAssetName("9");
        final AssetImpl asset2 = new AssetImpl();
        asset2.setAssetId("99");
        asset2.setAssetName("99");
        final AssetImpl asset3 = new AssetImpl();
        asset3.setAssetId("999");
        asset3.setAssetName("999");
        assetList.add(asset1);
        assetList.add(asset2);
        assetList.add(asset3);
        when(assetService.loadAvailableAssets(any(BrokerKey.class), any(ProductKey.class), any(ServiceErrors.class)))
                .thenReturn(assetList);
        assetRateMap = new HashMap<>();
        termDepositAssetDetail1 = new TermDepositAssetDetailImpl();
        termDepositAssetDetail1.setAssetId("9");
        termDepositAssetDetail1.setIssuer("80000055");
        termDepositAssetDetail1.setPaymentFrequency(PaymentFrequency.AT_MATURITY);
        final Term term = new Term("3M");
        termDepositAssetDetail1.setTerm(term);
        final TreeSet<InterestRate> interestRates = new TreeSet<>();
        final InterestRateImpl interestRate = new TermDepositAssetDetailImpl().new InterestRateImpl();
        interestRate.setRate(new BigDecimal("0.035"));
        interestRate.setIrcId("121");
        interestRate.setPriority(4000);
        interestRate.setLowerLimit(new BigDecimal("5000.00"));
        interestRate.setUpperLimit(new BigDecimal("2000000.00"));
        interestRates.add(interestRate);
        final InterestRateImpl interestRate1 = new TermDepositAssetDetailImpl().new InterestRateImpl();
        interestRate1.setRate(new BigDecimal("0.045"));
        interestRate1.setIrcId("122");
        interestRate1.setPriority(4020);
        interestRate1.setLowerLimit(new BigDecimal("5000.00"));
        interestRate1.setUpperLimit(new BigDecimal("2000000.00"));
        interestRates.add(interestRate1);
        interestRateTier2 = new TermDepositAssetDetailImpl().new InterestRateImpl();
        interestRateTier2.setRate(new BigDecimal("0.055"));
        interestRateTier2.setIrcId("123");
        interestRateTier2.setPriority(3950);
        interestRateTier2.setLowerLimit(new BigDecimal("2000000.00"));
        interestRateTier2.setUpperLimit(new BigDecimal("200000000.00"));
        interestRates.add(interestRateTier2);
        termDepositAssetDetail1.setInterestRates(interestRates);
        final TermDepositAssetDetailImpl termDepositAssetDetail2 = new TermDepositAssetDetailImpl();
        termDepositAssetDetail2.setAssetId("99");
        termDepositAssetDetail2.setIssuer("80000055");
        termDepositAssetDetail2.setPaymentFrequency(PaymentFrequency.AT_MATURITY);
        final Term term1 = new Term("3M");
        termDepositAssetDetail2.setTerm(term1);
        final TreeSet<InterestRate> interestRates1 = new TreeSet<>();
        final InterestRateImpl interestRate2 = new TermDepositAssetDetailImpl().new InterestRateImpl();
        interestRate2.setRate(new BigDecimal("0.035"));
        interestRate2.setIrcId("121");
        interestRate2.setPriority(4000);
        interestRate2.setLowerLimit(new BigDecimal("5000.00"));
        interestRate2.setUpperLimit(new BigDecimal("2000000.00"));
        interestRates1.add(interestRate2);
        final InterestRateImpl interestRate3 = new TermDepositAssetDetailImpl().new InterestRateImpl();
        interestRate3.setRate(new BigDecimal("0.045"));
        interestRate3.setIrcId("122");
        interestRate3.setPriority(4020);
        interestRate3.setLowerLimit(new BigDecimal("5000.00"));
        interestRate3.setUpperLimit(new BigDecimal("2000000.00"));
        interestRates1.add(interestRate3);
        termDepositAssetDetail2.setInterestRates(interestRates1);
        assetRateMap.put("9", termDepositAssetDetail1);
        assetRateMap.put("99", termDepositAssetDetail2);
        account = new WrapAccountImpl();
        account.setAccountName("Wrap Account Name 1");
        account.setAccountNumber("account number");
        account.setAccountKey(AccountKey.valueOf("accountId"));
        directSubAccount = new SubAccountImpl();
        directSubAccount.setSubAccountType(ContainerType.DIRECT);
        final ProductIdentifierImpl productIdentifier = new ProductIdentifierImpl();
        productIdentifier.setProductKey(ProductKey.valueOf("directProductIdentifier"));
        directSubAccount.setProductIdentifier(productIdentifier);
        final List<SubAccount> subAccountlist = new ArrayList<>();
        subAccountlist.add(directSubAccount);
        account.setSubAccounts(subAccountlist);
        when(accountService.loadWrapAccountWithoutContainers(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(account);
        when(assetService.loadTermDepositRates(any(BrokerKey.class), any(DateTime.class), any(ArrayList.class),
                any(ServiceErrors.class))).thenReturn(assetRateMap);
        when(productIntegrationService.getProductDetail(any(ProductKey.class), any(ServiceErrors.class)))
                .thenReturn(investmentProduct);
        when(bankDateIntegrationService.getBankDate(any(ServiceErrors.class))).thenReturn(DateTime.now());
        when(termDepositCalculatorUtils.getBankDate()).thenReturn(DateTime.now());
        when(termDepositCalculatorUtils.getAccount(any(TermDepositCalculatorKey.class), any(ServiceErrors.class)))
                .thenReturn(account);
        when(termDepositCalculatorUtils
                .getBrokerKey(any(TermDepositCalculatorKey.class), any(WrapAccount.class), any(ServiceErrors.class)))
                .thenReturn(BrokerKey.valueOf("71259"));
        when(termDepositCalculatorUtils
                .getProducts(any(TermDepositCalculatorKey.class), any(BrokerKey.class), any(WrapAccount.class),
                        any(ServiceErrors.class))).thenReturn(dgProductList);
    }

    @Test
    public void testCompareRate_whenNoCurrentRate_thenBestRateReturned() {
        final BigDecimal bestRate = new BigDecimal("100");
        final BigDecimal newRate = TermDepositCalculatorDtoServiceImpl.compareRate(bestRate, null);
        assertEquals(bestRate, newRate);
    }

    @Test
    public void testCompareRate_whenCurrentRateBigger_thenCurrentRateReturned() {
        final BigDecimal bestRate = new BigDecimal("100");
        final BigDecimal currentRate = new BigDecimal("200");
        final BigDecimal newRate = TermDepositCalculatorDtoServiceImpl.compareRate(bestRate, currentRate);
        assertEquals(currentRate, newRate);
    }

    @Test
    public void testCompareRate_whenCurrentRateSmaller_thenBestRateReturned() {
        final BigDecimal bestRate = new BigDecimal("100");
        final BigDecimal currentRate = new BigDecimal("50");
        final BigDecimal newRate = TermDepositCalculatorDtoServiceImpl.compareRate(bestRate, currentRate);
        assertEquals(bestRate, newRate);
    }

    @Test
    public void testPopulateProductsAndAssets_whenNoAvailableAssets_thenProductNotAdded() {
        final List<Asset> allProductAssets = new ArrayList<>();
        final TermDepositCalculatorKey tdCalcAccountKey = new TermDepositCalculatorAccountKey(null, "10000",
                new com.bt.nextgen.api.account.v3.model.AccountKey(EncodedString.fromPlainText("12345").toString()));
        final BrokerKey brokerKey = BrokerKey.valueOf("12345");
        final Map<ProductKey, List<Asset>> productAssets = new HashMap<>();
        final Set<Badge> badges = new TreeSet<>();
        final List<Asset> emptyAssetList = new ArrayList<>();
        when(assetService.loadAvailableAssets(any(BrokerKey.class), any(ProductKey.class), any(ServiceErrors.class)))
                .thenReturn(emptyAssetList);
        termDepositService
                .populateProductsAndAssets(dgProductList, productAssets, allProductAssets, badges, tdCalcAccountKey,
                        brokerKey, new ServiceErrorsImpl());
        assertEquals(0, productAssets.size());
        assertEquals(0, badges.size());
    }

    @Test
    public void testPopulateProductsAndAssets_whenAvailableAssets_thenProductAdded() {
        final List<Asset> allProductAssets = new ArrayList<>();
        final TermDepositCalculatorKey tdCalcAccountKey = new TermDepositCalculatorAccountKey(null, "10000",
                new com.bt.nextgen.api.account.v3.model.AccountKey(EncodedString.fromPlainText("12345").toString()));
        final BrokerKey brokerKey = BrokerKey.valueOf("12345");
        final Map<ProductKey, List<Asset>> productAssets = new HashMap<>();
        final Set<Badge> badges = new TreeSet<>();
        termDepositService
                .populateProductsAndAssets(dgProductList, productAssets, allProductAssets, badges, tdCalcAccountKey,
                        brokerKey, new ServiceErrorsImpl());
        assertEquals(assetList.size(), productAssets.size());
        assertEquals(dgProductList.size(), badges.size());
    }

    @Test
    public void testPopulateProductsAndAssets_whenBadgeInKey_thenKeyBadgeReturned() {
        final List<Asset> allProductAssets = new ArrayList<>();
        final TermDepositCalculatorKey tdCalcAccountKey = new TermDepositCalculatorAccountKey(product1.getProductKey(),
                "10000",
                new com.bt.nextgen.api.account.v3.model.AccountKey(EncodedString.fromPlainText("12345").toString()));
        final BrokerKey brokerKey = BrokerKey.valueOf("12345");
        final Map<ProductKey, List<Asset>> productAssets = new HashMap<>();
        final Set<Badge> badges = new TreeSet<>();
        final Badge selectedBadge = termDepositService
                .populateProductsAndAssets(dgProductList, productAssets, allProductAssets, badges, tdCalcAccountKey,
                        brokerKey, new ServiceErrorsImpl());
        assertEquals(product1.getProductKey().getId(),
                ConsistentEncodedString.toPlainText(selectedBadge.getProductId()));
    }

    @Test
    public void testPopulateProductsAndAssets_whenBadgeNotInKey_thenInvestmentBadgeReturned() {
        final List<Asset> allProductAssets = new ArrayList<>();
        final TermDepositCalculatorKey tdCalcAccountKey = new TermDepositCalculatorAccountKey(null, "10000",
                new com.bt.nextgen.api.account.v3.model.AccountKey(EncodedString.fromPlainText("12345").toString()));
        final BrokerKey brokerKey = BrokerKey.valueOf("12345");
        final Map<ProductKey, List<Asset>> productAssets = new HashMap<>();
        final Set<Badge> badges = new TreeSet<>();
        final Badge selectedBadge = termDepositService
                .populateProductsAndAssets(dgProductList, productAssets, allProductAssets, badges, tdCalcAccountKey,
                        brokerKey, new ServiceErrorsImpl());
        assertEquals(investmentProduct.getProductKey().getId(),
                ConsistentEncodedString.toPlainText(selectedBadge.getProductId()));
    }

    @Test
    public void testFind_Td_calculator() {
        final TermDepositCalculatorKey key = new TermDepositCalculatorDealerKey(ProductKey.valueOf("1"), "20000",
                BrokerKey.valueOf("71259"),null);
        final TermDepositCalculatorDto termDepositCalculatorDto = termDepositService.find(key, new ServiceErrorsImpl());
        Assert.assertFalse(termDepositCalculatorDto.getTermDepositBankRates().isEmpty());
        assertThat(termDepositCalculatorDto.getTermDepositBankRates().size(), Is.is(1));
        final List<TermDepositBankRates> rates = termDepositCalculatorDto.getTermDepositBankRates();
        for (final TermDepositBankRates rate : rates) {
            assertThat(rate.getBrandId(), Is.is("80000055"));
            Assert.assertFalse(rate.getTermMap().isEmpty());
            final Map<Term, TermDepositRateModel> rateMap = rate.getTermMap();
            for (final TermDepositRateModel termDepositRateModel : rateMap.values()) {
                assertThat(termDepositRateModel.getInterestPerTerm(), Is.is("0.03%"));
            }
        }
    }

    @Test
    public void testFind_whenConsructed_BadgeListIsSorted() {
        final TermDepositCalculatorKey key = new TermDepositCalculatorDealerKey(ProductKey.valueOf("1"), "20000",
                BrokerKey.valueOf("71259"),null);
        final TermDepositCalculatorDto termDepositCalculatorDto = termDepositService.find(key, new ServiceErrorsImpl());
        List<Badge> badges = termDepositCalculatorDto.getBadges();
        Assert.assertEquals(3, badges.size());
        Assert.assertEquals("BT Panorama Investments", badges.get(0).getName());
        Assert.assertEquals("BT Panorama Pension", badges.get(1).getName());
        Assert.assertEquals("BT Panorama Super", badges.get(2).getName());
    }

    @Test
    public void testTermDepositRatesAsCsvforDirect() {
        BrokerImpl broker = new BrokerImpl(BrokerKey.valueOf("66773"), BrokerType.ADVISER);
        broker.setDealerKey(BrokerKey.valueOf("45677"));
        broker.setIsDirectInvestment(false);
        final BrokerImpl brokertwo = new BrokerImpl(BrokerKey.valueOf("66773"), BrokerType.ADVISER);
        brokertwo.setDealerKey(BrokerKey.valueOf("456787"));
        brokertwo.setIsDirectInvestment(true);
        final Set<Broker> brokerList = new HashSet<>();
        brokerList.add(broker);
        brokerList.add(brokertwo);
        when(brokerHelperService.getDealerGroupsforInvestor(any(ServiceErrors.class))).thenReturn(brokerList);
        broker = new BrokerImpl(BrokerKey.valueOf("56789"), BrokerType.ADVISER);
        when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
        // when(termDepositService.).thenReturn()
        final String csv = termDepositService.getTermDepositRatesAsCsv("80000055", "direct", null);
        verify(brokerHelperService, times(1)).getDealerGroupsforInvestor(any(ServiceErrors.class));
        Assert.assertNotNull("csv", csv);
    }

    @Test
    public void testGetTermDepositRatesAsCsv_whenConsistentlyEncodedProductIdIsProvided_thenItIsDecodedCorrectly() {
        final String csv = termDepositService
                .getTermDepositRatesAsCsv("80000055", "direct", ConsistentEncodedString.fromPlainText("2").toString());
        verify(assetService, times(1))
                .loadAvailableAssets(Mockito.any(BrokerKey.class), Mockito.eq(ProductKey.valueOf("2")),
                        Mockito.any(ServiceErrors.class));
        Assert.assertNotNull("csv", csv);
    }

    @Test
    public void testGetTermDepositRatesAsCsv_whenInconsistentlyEncodedProductIdIsProvided_thenItIsDecodedCorrectly() {
        final String csv = termDepositService
                .getTermDepositRatesAsCsv("80000055", "direct", EncodedString.fromPlainText("2").toString());
        verify(assetService, times(1))
                .loadAvailableAssets(Mockito.any(BrokerKey.class), Mockito.eq(ProductKey.valueOf("2")),
                        Mockito.any(ServiceErrors.class));
        Assert.assertNotNull("csv", csv);
    }

    @Test
    public void testGetTermDepositRatesAsCsv_whenPlainTextProductIdIsProvided_thenItIsDecodedCorrectly() {
        final String csv = termDepositService.getTermDepositRatesAsCsv("80000055", "direct", "2");
        verify(assetService, times(1))
                .loadAvailableAssets(Mockito.any(BrokerKey.class), Mockito.eq(ProductKey.valueOf("2")),
                        Mockito.any(ServiceErrors.class));
        Assert.assertNotNull("csv", csv);
    }

    @Test
    public void testFilterTermDepositRates_whenAmountIsAtCuspOfTiers_thenTopTierReturned() {
        final Map<String, TermDepositAssetRate> filteredRates = TermDepositCalculatorDtoServiceImpl
                .filterTermDepositRates(assetRateMap, new BigDecimal("2000000.00"));
        assertEquals(1, filteredRates.get(termDepositAssetDetail1.getAssetId()).getRatePool().size());
        assertEquals(interestRateTier2.getIrcId(),
                filteredRates.get(termDepositAssetDetail1.getAssetId()).getRatePool().get(0).getAssetIrcId());
    }
}
