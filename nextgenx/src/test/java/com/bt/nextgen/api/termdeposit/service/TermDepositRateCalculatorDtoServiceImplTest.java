package com.bt.nextgen.api.termdeposit.service;

import com.bt.nextgen.api.termdeposit.model.Badge;
import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorDealerKey;
import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorDto;
import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorKey;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.SubAccountImpl;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.CacheManagedTermDepositAssetRateIntegrationService;
import com.bt.nextgen.service.avaloq.product.ProductIdentifierImpl;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.bt.nextgen.service.avaloq.product.ProductLevel;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.btfin.panorama.service.integration.account.SubAccount;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.bt.nextgen.service.integration.asset.PaymentFrequency;
import com.bt.nextgen.service.integration.asset.Term;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRate;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRateImpl;
import com.bt.nextgen.termdeposit.service.TermDepositAssetRateSearchKey;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import com.btfin.panorama.service.integration.asset.AssetType;
import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.junit.Assert;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TermDepositRateCalculatorDtoServiceImplTest {
    @InjectMocks
    private TermDepositRateCalculatorDtoServiceImpl termDepositService;

    @Mock
    private TermDepositCalculatorUtils termDepositCalculatorUtils;

    @Mock
    private AssetIntegrationService assetService;

    @Mock
    private TermDepositCalculatorConverter termDepositCalculatorConverter;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Before
    public void setup() {
        WrapAccountImpl account = getAccount();
        List<Asset> assetList = getAssetList();
        Map<ProductKey, Product> productMap =  getProductMap();
        List<Product> productList = getProductList();
        SortedSet accountTypeSet = new TreeSet<>();
        accountTypeSet.add("Individual");
        accountTypeSet.add("Joint");
        accountTypeSet.add("Company");
        accountTypeSet.add("Trust");

        List<TermDepositInterestRate> termDepositInterestRateList = getTermDepositInterestRates();
        when(productIntegrationService.getDealerGroupProductList(any(BrokerKey.class), any(ServiceErrors.class)))
                .thenReturn(productList);
        when(productIntegrationService.loadProductsMap(any(ServiceErrors.class))).thenReturn(productMap);
        when(assetService.loadAvailableAssets(any(BrokerKey.class), any(ProductKey.class), any(ServiceErrors.class)))
                .thenReturn(assetList);
        when(productIntegrationService.getProductDetail(any(ProductKey.class), any(ServiceErrors.class)))
                .thenReturn(productList.get(1));
        when(termDepositCalculatorUtils.getBankDate()).thenReturn(DateTime.now());
        when(termDepositCalculatorUtils.getAccount(any(TermDepositCalculatorKey.class), any(ServiceErrors.class)))
                .thenReturn(account);
        when(termDepositCalculatorUtils
                .getBrokerKey(any(TermDepositCalculatorKey.class), any(WrapAccount.class), any(ServiceErrors.class)))
                .thenReturn(BrokerKey.valueOf("71259"));
        when(termDepositCalculatorUtils
                .getProducts(any(TermDepositCalculatorKey.class), any(BrokerKey.class), any(WrapAccount.class),
                        anyMapOf(ProductKey.class, Product.class), any(ServiceErrors.class))).thenReturn(productList);
        when(assetService
                .loadTermDepositRatesForAdviser(any(TermDepositAssetRateSearchKey.class), any(ServiceErrors.class)))
                .thenReturn(termDepositInterestRateList);
        when(termDepositCalculatorConverter.toTermDepositCalculatorDto(anyList(), anyList(), any(BigDecimal.class)))
                .thenCallRealMethod();
        when(assetService.getAccountTypesByProduct(any(ProductKey.class),any(ServiceErrors.class))).thenReturn(accountTypeSet);
    }

    private Map<ProductKey, Product> getParentKeyProducts(Collection<Product> products) {
        Map<ProductKey, Product> parentKeyProducts = new HashMap<>();
        for (Product product : products) {
            ProductKey parentProductKey = product.getParentProductKey();
            if (parentProductKey != null && product
                    .isDirect()) // ROOT products are not included & only direct product is
                // included.
                parentKeyProducts.put(parentProductKey, product);
        }
        return parentKeyProducts;
    }

    private WrapAccountImpl getAccount() {
        WrapAccountImpl account = new WrapAccountImpl();
        account.setAccountName("Wrap Account Name 1");
        account.setAccountNumber("account number");
        account.setAccountKey(AccountKey.valueOf("accountId"));
        SubAccountImpl directSubAccount = new SubAccountImpl();
        directSubAccount.setSubAccountType(ContainerType.DIRECT);
        final ProductIdentifierImpl productIdentifier = new ProductIdentifierImpl();
        productIdentifier.setProductKey(ProductKey.valueOf("directProductIdentifier"));
        directSubAccount.setProductIdentifier(productIdentifier);
        final List<SubAccount> subAccountlist = new ArrayList<>();
        subAccountlist.add(directSubAccount);
        account.setSubAccounts(subAccountlist);
        return account;
    }

    private List<Asset> getAssetList() {
        List<Asset> assetList = new ArrayList<>();
        AssetImpl asset = new AssetImpl();
        asset.setAssetId("9");
        asset.setAssetName("9");
        assetList.add(asset);
        asset = new AssetImpl();
        asset.setAssetId("10");
        asset.setAssetName("10");
        assetList.add(asset);
        asset = new AssetImpl();
        asset.setAssetId("11");
        asset.setAssetName("11");
        asset.setAssetType(AssetType.TERM_DEPOSIT);
        assetList.add(asset);
        return assetList;
    }

    private List<Product> getProductList() {
        List<Product> productList = new ArrayList<>();
        ProductImpl product1 = new ProductImpl();
        product1.setProductKey(ProductKey.valueOf("1"));
        product1.setProductName("BT Panorama Super");
        product1.setParentProductId("11");
        product1.setProductTypeId("btfg$model");
        product1.setProductLevel(ProductLevel.MODEL);
        ProductImpl investmentProduct = new ProductImpl();
        investmentProduct.setProductKey(ProductKey.valueOf("2"));
        investmentProduct.setProductName("BT Panorama Investments");
        investmentProduct.setParentProductId("1");
        investmentProduct.setProductTypeId("btfg$model");
        investmentProduct.setProductLevel(ProductLevel.MODEL);
        investmentProduct.setDirect(true);
        ProductImpl product3 = new ProductImpl();
        product3.setProductKey(ProductKey.valueOf("3"));
        product3.setProductName("BT Panorama Pension");
        product3.setParentProductId("2");
        product3.setDirect(true);
        product3.setProductTypeId("btfg$model");
        product3.setProductLevel(ProductLevel.MODEL);
        ProductImpl catProduct1 = new ProductImpl();
        catProduct1.setProductKey(ProductKey.valueOf("11"));
        catProduct1.setProductName("IDPS");
        catProduct1.setParentProductId("0");
        catProduct1.setDirect(true);
        catProduct1.setProductTypeId("btfg$cat");
        catProduct1.setProductLevel(ProductLevel.CATEGORY);
        productList.add(product1);
        productList.add(investmentProduct);
        productList.add(product3);
        productList.add(catProduct1);
        return productList;
    }

    private Map<ProductKey, Product> getProductMap() {
        Map<ProductKey, Product> productMap = new HashMap<>();
        ProductImpl product1 = new ProductImpl();
        product1.setProductKey(ProductKey.valueOf("1"));
        product1.setProductName("BT Panorama Super");
        product1.setParentProductId("11");
        product1.setProductTypeId("btfg$model");
        product1.setProductLevel(ProductLevel.MODEL);
        ProductImpl investmentProduct = new ProductImpl();
        investmentProduct.setProductKey(ProductKey.valueOf("2"));
        investmentProduct.setProductName("BT Panorama Investments");
        investmentProduct.setParentProductId("1");
        investmentProduct.setProductTypeId("btfg$model");
        investmentProduct.setProductLevel(ProductLevel.MODEL);
        investmentProduct.setDirect(true);
        ProductImpl product3 = new ProductImpl();
        product3.setProductKey(ProductKey.valueOf("3"));
        product3.setProductName("BT Panorama Pension");
        product3.setParentProductId("2");
        product3.setDirect(true);
        product3.setProductTypeId("btfg$model");
        product3.setProductLevel(ProductLevel.MODEL);
        ProductImpl catProduct1 = new ProductImpl();
        catProduct1.setProductKey(ProductKey.valueOf("11"));
        catProduct1.setProductName("IDPS");
        catProduct1.setParentProductId("0");
        catProduct1.setDirect(true);
        catProduct1.setProductTypeId("btfg$cat");
        catProduct1.setProductLevel(ProductLevel.CATEGORY);
        productMap.put(ProductKey.valueOf("1"), product1);
        productMap.put(ProductKey.valueOf("2"), investmentProduct);
        productMap.put(ProductKey.valueOf("3"), product3);
        productMap.put(ProductKey.valueOf("11"), catProduct1);
        return productMap;
    }

    private Map<ProductKey, Product> getProductMap1() {
        Map<ProductKey, Product> productMap = new HashMap<>();
        ProductImpl investmentProduct = new ProductImpl();
        investmentProduct.setProductKey(ProductKey.valueOf("121"));
        investmentProduct.setProductName("BT Panorama Investments");
        investmentProduct.setParentProductId("1");
        investmentProduct.setProductTypeId("btfg$model");
        investmentProduct.setProductLevel(ProductLevel.MODEL);
        investmentProduct.setDirect(true);
        productMap.put(ProductKey.valueOf("121"), investmentProduct);
        return productMap;
    }

    private List<TermDepositInterestRate> getTermDepositInterestRates() {
        List<TermDepositInterestRate> termDepositInterestRates = new ArrayList<>();
        TermDepositInterestRateImpl.TermDepositInterestRateBuilder rateBuilder = new TermDepositInterestRateImpl
                .TermDepositInterestRateBuilder()
                .make().withAssetKey(AssetKey.valueOf("879485")).withIssuerId("10602").withIssuerName("St. George")
                .withWhiteLabelProductKey(ProductKey.valueOf("209457")).withDealerGroupKey(BrokerKey.valueOf("312314"))
                .withAccountStructureType(AccountStructureType.Individual).withTerm(new Term("3M"))
                .withPaymentFrequency(PaymentFrequency.AT_MATURITY).withRate(new BigDecimal("2.25"))
                .withRateAsPercentage(new BigDecimal("2.25")).withLowerLimit(new BigDecimal("0.00"))
                .withUpperLimit(new BigDecimal("20000")).withStartDate(DateTime.now())
                .withEndDate(DateTime.now().plusYears(2));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("6M"));
        rateBuilder.withRate(new BigDecimal("2.50")).withRateAsPercentage(new BigDecimal("2.50"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("1Y"));
        rateBuilder.withRate(new BigDecimal("2.85")).withRateAsPercentage(new BigDecimal("2.85"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withPaymentFrequency(PaymentFrequency.MONTHLY);
        rateBuilder.withRate(new BigDecimal("2.75")).withRateAsPercentage(new BigDecimal("2.75"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("3Y"));
        rateBuilder.withRate(new BigDecimal("3.25")).withRateAsPercentage(new BigDecimal("3.25"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withPaymentFrequency(PaymentFrequency.ANNUALLY);
        rateBuilder.withRate(new BigDecimal("3.35")).withRateAsPercentage(new BigDecimal("3.35"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("5Y"));
        rateBuilder.withRate(new BigDecimal("4.15")).withRateAsPercentage(new BigDecimal("4.15"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withPaymentFrequency(PaymentFrequency.MONTHLY);
        rateBuilder.withRate(new BigDecimal("4.05")).withRateAsPercentage(new BigDecimal("4.05"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withIssuerId("40179").withIssuerName("BT");
        rateBuilder.withTerm(new Term("3M"));
        rateBuilder.withRate(new BigDecimal("2.28")).withRateAsPercentage(new BigDecimal("2.28"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("6M"));
        rateBuilder.withRate(new BigDecimal("2.55")).withRateAsPercentage(new BigDecimal("2.55"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("1Y"));
        rateBuilder.withRate(new BigDecimal("2.95")).withRateAsPercentage(new BigDecimal("2.95"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withPaymentFrequency(PaymentFrequency.MONTHLY);
        rateBuilder.withRate(new BigDecimal("2.65")).withRateAsPercentage(new BigDecimal("2.65"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("3Y"));
        rateBuilder.withRate(new BigDecimal("3.25")).withRateAsPercentage(new BigDecimal("3.25"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withPaymentFrequency(PaymentFrequency.ANNUALLY);
        rateBuilder.withRate(new BigDecimal("3.35")).withRateAsPercentage(new BigDecimal("3.35"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("5Y"));
        rateBuilder.withRate(new BigDecimal("4.15")).withRateAsPercentage(new BigDecimal("4.15"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withPaymentFrequency(PaymentFrequency.MONTHLY);
        rateBuilder.withRate(new BigDecimal("4.05")).withRateAsPercentage(new BigDecimal("4.05"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withIssuerId("10603").withIssuerName("Bank of Melbourne");
        rateBuilder.withTerm(new Term("3M"));
        rateBuilder.withRate(new BigDecimal("2.28")).withRateAsPercentage(new BigDecimal("2.28"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("6M"));
        rateBuilder.withRate(new BigDecimal("2.25")).withRateAsPercentage(new BigDecimal("2.25"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("1Y"));
        rateBuilder.withRate(new BigDecimal("2.95")).withRateAsPercentage(new BigDecimal("2.95"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withPaymentFrequency(PaymentFrequency.MONTHLY);
        rateBuilder.withRate(new BigDecimal("2.65")).withRateAsPercentage(new BigDecimal("2.65"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("3Y"));
        rateBuilder.withRate(new BigDecimal("3.25")).withRateAsPercentage(new BigDecimal("3.25"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withPaymentFrequency(PaymentFrequency.ANNUALLY);
        rateBuilder.withRate(new BigDecimal("3.35")).withRateAsPercentage(new BigDecimal("3.35"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("5Y"));
        rateBuilder.withRate(new BigDecimal("4.35")).withRateAsPercentage(new BigDecimal("4.35"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withPaymentFrequency(PaymentFrequency.MONTHLY);
        rateBuilder.withRate(new BigDecimal("4.05")).withRateAsPercentage(new BigDecimal("4.05"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withIssuerId("10604").withIssuerName("Westpac");
        rateBuilder.withTerm(new Term("3M"));
        rateBuilder.withRate(new BigDecimal("2.30")).withRateAsPercentage(new BigDecimal("2.30"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("6M"));
        rateBuilder.withRate(new BigDecimal("2.55")).withRateAsPercentage(new BigDecimal("2.55"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("1Y"));
        rateBuilder.withRate(new BigDecimal("2.95")).withRateAsPercentage(new BigDecimal("2.95"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withPaymentFrequency(PaymentFrequency.MONTHLY);
        rateBuilder.withRate(new BigDecimal("2.65")).withRateAsPercentage(new BigDecimal("2.65"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("3Y"));
        rateBuilder.withRate(new BigDecimal("3.25")).withRateAsPercentage(new BigDecimal("3.25"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withPaymentFrequency(PaymentFrequency.ANNUALLY);
        rateBuilder.withRate(new BigDecimal("3.35")).withRateAsPercentage(new BigDecimal("3.35"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withTerm(new Term("5Y"));
        rateBuilder.withRate(new BigDecimal("4.15")).withRateAsPercentage(new BigDecimal("4.15"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        rateBuilder.withPaymentFrequency(PaymentFrequency.MONTHLY);
        rateBuilder.withRate(new BigDecimal("4.05")).withRateAsPercentage(new BigDecimal("4.05"));
        termDepositInterestRates.add(rateBuilder.buildTermDepositRate());
        return termDepositInterestRates;
    }

    @Test
    public void testFind() {
        final TermDepositCalculatorKey key = new TermDepositCalculatorDealerKey(ProductKey.valueOf("1"), "20000",
                BrokerKey.valueOf("71259"),"Individual");
        final TermDepositCalculatorDto termDepositCalculatorDto = termDepositService.find(key, new ServiceErrorsImpl());
        Assert.assertFalse(termDepositCalculatorDto.getTermDepositBankRates().isEmpty());
        assertThat(termDepositCalculatorDto.getTermDepositBankRates().size(), Is.is(4));
        assertNotNull(termDepositCalculatorDto.getAccountTypeList());
        assertEquals(termDepositCalculatorDto.getAccountTypeList().size(), 4);
        assertTrue(termDepositCalculatorDto.getAccountTypeList().contains("Individual"));
        assertThat(termDepositCalculatorDto.getSelectedAccountType(),is("Individual"));
    }

    @Test
    public void testFind_whenConstructed_BadgeListIsSorted() {
        final TermDepositCalculatorKey key = new TermDepositCalculatorDealerKey(ProductKey.valueOf("1"), "20000",
                BrokerKey.valueOf("71259"),"Individual");
        final TermDepositCalculatorDto termDepositCalculatorDto = termDepositService.find(key, new ServiceErrorsImpl());
        List<Badge> badges = termDepositCalculatorDto.getBadges();
        Assert.assertEquals(3, badges.size());
        Assert.assertEquals("BT Panorama Investments", badges.get(0).getName());
        Assert.assertEquals("BT Panorama Pension", badges.get(1).getName());
        Assert.assertEquals("BT Panorama Super", badges.get(2).getName());
    }

    @Test
    public void testGetDirectModelProductForCategoryProduct() {
        ProductImpl baseProduct = new ProductImpl();
        baseProduct.setProductKey(ProductKey.valueOf("11"));
        baseProduct.setProductName("IDPS");
        baseProduct.setParentProductId("0");
        baseProduct.setDirect(true);
        baseProduct.setProductTypeId("btfg$cat");
        baseProduct.setProductLevel(ProductLevel.CATEGORY);
        List<Product> productList = getProductList();
        when(productIntegrationService.getProductDetail(any(ProductKey.class), any(ServiceErrors.class)))
                .thenReturn(productList.get(3));
        Product product = termDepositService
                .getDirectModelProduct(getParentKeyProducts(productList), baseProduct, getProductMap(), new ServiceErrorsImpl());
        assertThat(product, is(nullValue()));
    }

    @Test
    public void testGetDirectModelProductForOfferProduct() {
        ProductImpl offerProduct = new ProductImpl();
        offerProduct.setProductKey(ProductKey.valueOf("121"));
        offerProduct.setProductName("Just offer");
        offerProduct.setParentProductId("3");
        offerProduct.setDirect(true);
        offerProduct.setProductTypeId("btfg$offer");
        offerProduct.setProductLevel(ProductLevel.OFFER);
        List<Product> productList = getProductList();
        Product product = termDepositService
                .getDirectModelProduct(getParentKeyProducts(productList), offerProduct, getProductMap1(), new ServiceErrorsImpl());
        assertThat(product.getProductKey().getId(), is("121"));
        assertThat(product.getParentProductKey().getId(), is("1"));
    }

    @Test
    public void testGetDirectModelProductForOfferWithoutParentProduct() {
        ProductImpl offerProduct = new ProductImpl();
        offerProduct.setProductKey(ProductKey.valueOf("121"));
        offerProduct.setProductName("Just offer");
        offerProduct.setParentProductId("3");
        offerProduct.setDirect(true);
        offerProduct.setProductTypeId("btfg$offer");
        offerProduct.setProductLevel(ProductLevel.OFFER);
        List<Product> productList = getProductList();
        when(productIntegrationService.getProductDetail(any(ProductKey.class), any(ServiceErrors.class)))
                .thenReturn(productList.get(3));
        Product product = termDepositService
                .getDirectModelProduct(getParentKeyProducts(productList), offerProduct, getProductMap(), new ServiceErrorsImpl());
        assertThat(product, is(nullValue()));
    }
}
