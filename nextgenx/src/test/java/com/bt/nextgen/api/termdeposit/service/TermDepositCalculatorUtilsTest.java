package com.bt.nextgen.api.termdeposit.service;

import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorAccountKey;
import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorDealerKey;
import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorKey;
import com.bt.nextgen.core.domain.key.StringIdKey;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.SubAccountImpl;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetDetailImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.avaloq.product.ProductIdentifierImpl;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.bt.nextgen.service.avaloq.product.ProductLevel;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.btfin.panorama.service.integration.account.SubAccount;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.PaymentFrequency;
import com.bt.nextgen.service.integration.asset.Term;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.broker.BrokerType;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by M044020 on 31/07/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class TermDepositCalculatorUtilsTest {

    @InjectMocks
    private TermDepositCalculatorUtils utils;

    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private BrokerHelperService brokerHelperService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private BankDateIntegrationService bankDateIntegrationService;

    private WrapAccountImpl account;
    private SubAccountImpl directSubAccount;
    private ProductImpl product1;
    private ProductImpl investmentProduct;
    List<Product> dgProductList;
    List<Asset> assetList;
    private Map<String, TermDepositAssetDetail> assetRateMap;
    private TermDepositAssetDetailImpl.InterestRateImpl interestRateTier2;
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
        assetRateMap = new HashMap<>();
        termDepositAssetDetail1 = new TermDepositAssetDetailImpl();
        termDepositAssetDetail1.setAssetId("9");
        termDepositAssetDetail1.setIssuer("80000055");
        termDepositAssetDetail1.setPaymentFrequency(PaymentFrequency.AT_MATURITY);
        final Term term = new Term("3M");
        termDepositAssetDetail1.setTerm(term);
        final TreeSet<TermDepositAssetDetail.InterestRate> interestRates = new TreeSet<>();
        final TermDepositAssetDetailImpl.InterestRateImpl interestRate = new TermDepositAssetDetailImpl().new
                InterestRateImpl();
        interestRate.setRate(new BigDecimal("0.035"));
        interestRate.setIrcId("121");
        interestRate.setPriority(4000);
        interestRate.setLowerLimit(new BigDecimal("5000.00"));
        interestRate.setUpperLimit(new BigDecimal("2000000.00"));
        interestRates.add(interestRate);
        final TermDepositAssetDetailImpl.InterestRateImpl interestRate1 = new TermDepositAssetDetailImpl().new
                InterestRateImpl();
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
        final TreeSet<TermDepositAssetDetail.InterestRate> interestRates1 = new TreeSet<>();
        final TermDepositAssetDetailImpl.InterestRateImpl interestRate2 = new TermDepositAssetDetailImpl().new
                InterestRateImpl();
        interestRate2.setRate(new BigDecimal("0.035"));
        interestRate2.setIrcId("121");
        interestRate2.setPriority(4000);
        interestRate2.setLowerLimit(new BigDecimal("5000.00"));
        interestRate2.setUpperLimit(new BigDecimal("2000000.00"));
        interestRates1.add(interestRate2);
        final TermDepositAssetDetailImpl.InterestRateImpl interestRate3 = new TermDepositAssetDetailImpl().new
                InterestRateImpl();
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
        when(productIntegrationService.getProductDetail(any(ProductKey.class), any(ServiceErrors.class)))
                .thenReturn(investmentProduct);
        when(bankDateIntegrationService.getBankDate(any(ServiceErrors.class))).thenReturn(DateTime.now());
    }

    @Test
    public void testGetAccount_whenAccountKey_thenAccountReturned() throws Exception {
        final TermDepositCalculatorKey tdCalcAccountKey = new TermDepositCalculatorAccountKey(null, "10000",
                new com.bt.nextgen.api.account.v3.model.AccountKey(EncodedString.fromPlainText("12345").toString()));
        final WrapAccount account = utils.getAccount(tdCalcAccountKey, new ServiceErrorsImpl());
        assertNotNull(account);
    }

    @Test
    public void testGetAccount_whenNoAccountKey_thenNullReturned() {
        final TermDepositCalculatorKey tdCalcDealerKey = new TermDepositCalculatorDealerKey(
                ProductKey.valueOf("product"), "10000", BrokerKey.valueOf("12345"),"Individual");
        final WrapAccount account = utils.getAccount(tdCalcDealerKey, new ServiceErrorsImpl());
        assertNull(account);
    }

    @Test
    public void testGetBrokerKey_whenAccountKey_thenAccountBrokerReturned() {
        final BrokerImpl broker = new BrokerImpl(BrokerKey.valueOf("66773"), BrokerType.ADVISER);
        broker.setDealerKey(BrokerKey.valueOf("45677"));
        broker.setIsDirectInvestment(false);
        when(brokerHelperService.getDealerGroupForInvestor(any(WrapAccount.class), any(ServiceErrors.class)))
                .thenReturn(broker);
        final TermDepositCalculatorKey tdCalcAccountKey = new TermDepositCalculatorAccountKey(null, "10000",
                new com.bt.nextgen.api.account.v3.model.AccountKey(EncodedString.fromPlainText("12345").toString()));
        final BrokerKey brokerKey = utils.getBrokerKey(tdCalcAccountKey, account, new ServiceErrorsImpl());
        assertEquals(broker.getDealerKey(), brokerKey);
    }

    @Test
    public void testGetBrokerKey_whenNoAccountKey_thenBrokerKeyReturned() {
        final TermDepositCalculatorDealerKey tdCalcDealerKey = new TermDepositCalculatorDealerKey(
                ProductKey.valueOf("product"), "10000", BrokerKey.valueOf("12345"),"Individual");
        final BrokerKey brokerKey = utils.getBrokerKey(tdCalcDealerKey, account, new ServiceErrorsImpl());
        assertEquals(tdCalcDealerKey.getBrokerKey(), brokerKey);
    }

    @Test
    public void testGetProducts_whenAccountKey_thenAccountProductReturned() {
        final TermDepositCalculatorKey tdCalcAccountKey = new TermDepositCalculatorAccountKey(null, "10000",
                new com.bt.nextgen.api.account.v3.model.AccountKey(EncodedString.fromPlainText("12345").toString()));
        final List<Product> products = utils.getProducts(tdCalcAccountKey, null, account, new ServiceErrorsImpl());
        assertEquals(1, products.size());
    }

    @Test
    public void testGetProducts_whenNoAccountKey_thenBrokerProductsReturned() {
        final TermDepositCalculatorDealerKey tdCalcDealerKey = new TermDepositCalculatorDealerKey(
                ProductKey.valueOf("product"), "10000", BrokerKey.valueOf("12345"),"Individual");
        final List<Product> products = utils
                .getProducts(tdCalcDealerKey, tdCalcDealerKey.getBrokerKey(), null, new ServiceErrorsImpl());
        assertEquals(dgProductList.size(), products.size());
    }

    @Test
    public void getBankDate() throws Exception {
        DateTime result = utils.getBankDate();
        assertNotNull(result);
    }

    @Test
    public void getBankDateException() throws Exception {
        when(bankDateIntegrationService.getBankDate(any(ServiceErrors.class))).thenThrow(new RuntimeException("Test"));
        DateTime result = utils.getBankDate();
        assertNotNull(result);
    }
}