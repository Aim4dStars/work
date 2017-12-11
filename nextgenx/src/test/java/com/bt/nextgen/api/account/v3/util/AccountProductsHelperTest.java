package com.bt.nextgen.api.account.v3.util;

import com.bt.nextgen.api.account.v3.model.CashSweepInvestmentDto;
import com.bt.nextgen.api.account.v3.model.DirectOffer;
import com.bt.nextgen.api.account.v3.model.InitialInvestmentDto;
import com.bt.nextgen.api.product.v1.model.ProductDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountSubType;
import com.bt.nextgen.service.avaloq.account.PensionAccountDetailImpl;
import com.bt.nextgen.service.avaloq.account.PensionType;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.asset.ManagedFundAssetImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.product.ProductLevel;
import com.bt.nextgen.service.avaloq.product.ProductRelation;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.account.PensionAccountDetail;
import com.bt.nextgen.service.integration.account.direct.CashSweepInvestmentAssetImpl;
import com.bt.nextgen.service.integration.account.direct.InitialInvestmentAssetImpl;
import com.bt.nextgen.service.integration.account.direct.ProductSubscriptionImpl;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.ManagedFundAsset;
import com.bt.nextgen.service.integration.options.model.OptionKey;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedPortfolioAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIdentifier;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.account.CashSweepInvestmentAsset;
import com.btfin.panorama.service.integration.account.InitialInvestmentAsset;
import com.btfin.panorama.service.integration.account.ProductSubscription;
import com.btfin.panorama.service.integration.account.SubAccount;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bt.nextgen.api.account.v3.model.DirectOffer.ACTIVE;
import static com.bt.nextgen.api.account.v3.model.DirectOffer.SIMPLE;
import static com.bt.nextgen.service.avaloq.product.ProductLevel.MODEL;
import static com.bt.nextgen.service.avaloq.product.ProductLevel.OFFER;
import static com.bt.nextgen.service.avaloq.product.ProductLevel.PRIVATE_LABEL;
import static com.bt.nextgen.service.avaloq.product.ProductLevel.WHITE_LABEL;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountProductsHelperTest {

    @InjectMocks
    private AccountProductsHelper accountProductsHelper;

    @Mock
    private BrokerHelperService brokerHelperService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private AssetIntegrationService assetIntegrationService;

    @Mock
    private PortfolioIntegrationService portfolioIntegrationService;

    @Mock
    private OptionsService optionsService;

    private List<ProductSubscription> subscriptions;
    private List<InitialInvestmentAsset> initialInvestmentAssets;
    private List<CashSweepInvestmentAsset> cashSweepInvestmentAssets;
    private ServiceErrors serviceErrors;
    private final Map<ProductKey, Product> productMap = new HashMap<>();
    private ManagedFundAssetImpl asset;
    private WrapAccountValuation valuation;

    @Before
    public void setUp() throws Exception {
        serviceErrors = new ServiceErrorsImpl();
        subscriptions = new ArrayList<>();
        initialInvestmentAssets = new ArrayList<>();

        productMap.put(ProductKey.valueOf("prod1"), getProduct("prod1", ACTIVE.getSubscriptionProduct(), "prod4", OFFER));
        productMap.put(ProductKey.valueOf("prod2"), getProduct("prod2", SIMPLE.getSubscriptionProduct(), "prod4", OFFER));
        productMap.put(ProductKey.valueOf("prod3"), getProduct("prod3", "DIRE.BTPI", "prod6", WHITE_LABEL));
        productMap.put(ProductKey.valueOf("prod4"), getProduct("prod4", "PROD.WL.PANORAMA", "prod5", WHITE_LABEL));
        productMap.put(ProductKey.valueOf("prod5"), getProduct("prod5", "PROD.PL.PANORAMA", "prod2", PRIVATE_LABEL));
        productMap.put(ProductKey.valueOf("prod7"), getProduct("prod7", "BTCASH", null, MODEL));

        InitialInvestmentAssetImpl initialInvestmentAsset = new InitialInvestmentAssetImpl();
        initialInvestmentAsset.setInitialInvestmentAmount(BigDecimal.valueOf(2000));
        initialInvestmentAsset.setInitialInvestmentAssetId("asset1");
        initialInvestmentAssets.add(initialInvestmentAsset);

        CashSweepInvestmentAsset cashSweepInvestmentAsset = mock(CashSweepInvestmentAsset.class);
        when(cashSweepInvestmentAsset.getInvestmentAssetId()).thenReturn("asset1");
        when(cashSweepInvestmentAsset.getSweepPercent()).thenReturn(BigDecimal.valueOf(20));
        cashSweepInvestmentAssets = Collections.singletonList(cashSweepInvestmentAsset);

        asset = new ManagedFundAssetImpl();
        asset.setAssetId("asset1");
        asset.setAssetCode("code1");
        asset.setAssetName("asset1");
        valuation = setupMockAccountValuation();

        when(brokerHelperService.getUserExperience(any(WrapAccount.class), Mockito.any(ServiceErrors.class))).thenReturn(UserExperience.DIRECT);
        when(assetIntegrationService.loadAsset(anyString(), any(ServiceErrors.class))).thenReturn(asset);
        when(productIntegrationService.getProductDetail(ProductKey.valueOf("prod1"), serviceErrors)).thenReturn(productMap.get(ProductKey.valueOf("prod1")));
        when(productIntegrationService.getProductDetail(ProductKey.valueOf("prod2"), serviceErrors)).thenReturn(productMap.get(ProductKey.valueOf("prod2")));
        when(productIntegrationService.getProductDetail(ProductKey.valueOf("prod3"), serviceErrors)).thenReturn(productMap.get(ProductKey.valueOf("prod3")));
        when(productIntegrationService.getProductDetail(ProductKey.valueOf("prod4"), serviceErrors)).thenReturn(productMap.get(ProductKey.valueOf("prod4")));
        when(productIntegrationService.getProductDetail(ProductKey.valueOf("prod5"), serviceErrors)).thenReturn(productMap.get(ProductKey.valueOf("prod5")));
        when(productIntegrationService.getProductDetail(ProductKey.valueOf("prod7"), serviceErrors)).thenReturn(productMap.get(ProductKey.valueOf("prod7")));
        when(portfolioIntegrationService.loadWrapAccountValuation(any(com.bt.nextgen.service.integration.account.AccountKey.class),
                any(DateTime.class), any(ServiceErrors.class))).thenReturn(valuation);
    }

    @Test
    public void testGetSubscriptionTypeForDirectUser() {
        String subscriptionType = accountProductsHelper.getSubscriptionType(getAccount(), serviceErrors);
        assertThat(subscriptionType, is(SIMPLE.getSubscriptionType()));
    }

    @Test
    public void testGetSubscriptionTypeForNonDirectUser() {
        subscriptions.add(getProductSubscription("prod1"));
        subscriptions.add(getProductSubscription("prod2"));
        Mockito.when(brokerHelperService.getUserExperience(Mockito.any(WrapAccount.class), Mockito.any(ServiceErrors.class))).thenReturn(null);
        String subscriptionType = accountProductsHelper.getSubscriptionType(getAccount(), serviceErrors);
        assertNull(subscriptionType);
    }

    @Test
    public void testGetSubscriptionTypeActive() {
        subscriptions.add(getProductSubscription("prod1"));
        subscriptions.add(getProductSubscription("prod2"));
        String subscriptionType = accountProductsHelper.getSubscriptionType(subscriptions, false, serviceErrors);
        assertThat(subscriptionType, is(ACTIVE.getSubscriptionType()));
    }

    @Test
    public void testGetSubscriptionTypeSimple() {
        subscriptions.add(getProductSubscription("prod2"));
        subscriptions.add(getProductSubscription("prod3"));
        String subscriptionType = accountProductsHelper.getSubscriptionType(subscriptions, false, serviceErrors);
        assertThat(subscriptionType, is(SIMPLE.getSubscriptionType()));
    }

    @Test
    public void testGetSubscriptionTypeUndecided() {
        subscriptions.add(getProductSubscription("prod3"));
        subscriptions.add(getProductSubscription("prod4"));
        String subscriptionType = accountProductsHelper.getSubscriptionType(subscriptions, false, serviceErrors);
        assertThat(subscriptionType, is(DirectOffer.UNDECIDED.getSubscriptionType()));
    }

    @Test
    public void testGetSubscriptionTypeNoSubscription() {
        subscriptions = new ArrayList<>();
        String subscriptionType = accountProductsHelper.getSubscriptionType(subscriptions, false, serviceErrors);
        assertThat(subscriptionType, is(DirectOffer.UNDECIDED.getSubscriptionType()));
    }

    @Test
    public void testGetSubscriptionTypeDirectSuper() {
        subscriptions = new ArrayList<>();
        String subscriptionType = accountProductsHelper.getSubscriptionType(subscriptions, true, serviceErrors);
        assertThat(subscriptionType, is(DirectOffer.ACTIVE.getSubscriptionType()));
    }

    @Test
    public void tesGetInitialInvestmentsForAccount() {
        List<InitialInvestmentDto> initialInvestments = accountProductsHelper.getInitialInvestments(getAccount(), serviceErrors);
        assertEquals(initialInvestments.size(), 1);
        assertEquals(initialInvestments.get(0).getAsset().getAssetId(), "asset1");
        assertEquals(initialInvestments.get(0).getAmount(), BigDecimal.valueOf(2000));
    }

    @Test
    public void testGetProductDtoForNonSuper() {
        final WrapAccountDetail account = getAccount();
        final List<SubAccount> subAccounts = new ArrayList<>();
        subAccounts.add(getSubAccount("prod7", ContainerType.DIRECT));

        when(account.getProductKey()).thenReturn(ProductKey.valueOf("prod4"));
        when(account.getSubAccounts()).thenReturn(subAccounts);

        final ProductDto productDto = accountProductsHelper.getProductDto(account, serviceErrors);
        assertNotNull(productDto);
        assertEquals(EncodedString.toPlainText(productDto.getKey().getProductId()), "prod4");
        assertEquals(productDto.getProductName(), "prod4");
        assertEquals(productDto.getInvestmentBuffer(), BigDecimal.valueOf(100));
    }

    @Test
    public void testGetProductDtoForSuper() {
        final Product superProduct = getProduct("prodSup", "PROD.SUP.1234", "prod5", WHITE_LABEL);
        ProductRelation relation = mock(ProductRelation.class);
        when(relation.getProductRelToABN()).thenReturn("12345678");
        when(superProduct.isSuper()).thenReturn(true);
        when(superProduct.getProductRelation()).thenReturn(asList(relation));
        when(superProduct.getParentProductKey()).thenReturn(ProductKey.valueOf("prod5"));
        productMap.put(ProductKey.valueOf("prodSup"), superProduct);
        when(productMap.get(ProductKey.valueOf("prod5")).getProductUsi()).thenReturn("12345");
        when(productIntegrationService.getProductDetail(ProductKey.valueOf("prodSup"), serviceErrors)).thenReturn(productMap.get(ProductKey.valueOf("prodSup")));

        final WrapAccountDetail account = getAccount();
        when(account.getProductKey()).thenReturn(ProductKey.valueOf("prodSup"));

        final ProductDto productDto = accountProductsHelper.getProductDto(account, serviceErrors);
        assertNotNull(productDto);
        assertEquals(EncodedString.toPlainText(productDto.getKey().getProductId()), "prodSup");
        assertEquals(productDto.getProductName(), "prodSup");
        assertEquals(productDto.getProductUsi(), "12345");
        assertEquals(productDto.getProductABN(), "12345678");
    }

    @Test
    public void testConvertToInitialInvestmentDto() {
        List<InitialInvestmentDto> initialInvestments = accountProductsHelper.
                convertToInitialInvestmentDto(getAccount().getInitialInvestmentAsset(), serviceErrors);
        assertEquals(initialInvestments.size(), 1);
        assertEquals(initialInvestments.get(0).getAsset().getAssetId(), "asset1");
        assertEquals(initialInvestments.get(0).getAmount(), BigDecimal.valueOf(2000));
    }

    @Test
    public void testConvertToInitialInvestmentDto_NoInitialInvestments() {
        List<InitialInvestmentDto> initialInvestments = accountProductsHelper.
                convertToInitialInvestmentDto(new ArrayList<InitialInvestmentAsset>(), serviceErrors);
        assertEquals(initialInvestments.size(), 0);
    }

    @Test
    public void testConvertToInitialInvestmentDto_emptyValuesInInitialInvestments() {
        ArrayList<InitialInvestmentAsset> initialInvestmentAssets = new ArrayList<>();

        InitialInvestmentAssetImpl initialInvestmentAsset = new InitialInvestmentAssetImpl();
        initialInvestmentAssets.add(initialInvestmentAsset);

        List<InitialInvestmentDto> initialInvestments = accountProductsHelper.
                convertToInitialInvestmentDto(initialInvestmentAssets, serviceErrors);
        assertEquals(initialInvestments.size(), 0);
    }

    @Test
    public void tesGetCashSweepInvestmentsForSubAccount() {
        List<CashSweepInvestmentDto> cashSweepInvestmentDtos = accountProductsHelper.getCashSweepAssets(com.bt.nextgen.service.integration.account.AccountKey.valueOf("12345"), getSubAccount("prod1", ContainerType.DIRECT), serviceErrors);
        assertEquals(cashSweepInvestmentDtos.size(), 2);
        assertEquals(cashSweepInvestmentDtos.get(0).getAsset().getAssetId(), "asset1");
        assertEquals(cashSweepInvestmentDtos.get(0).getAllocationPercent(), BigDecimal.valueOf(20));
        assertEquals(cashSweepInvestmentDtos.get(1).getAsset().getAssetId(), "asset2");
        assertEquals(cashSweepInvestmentDtos.get(1).getAllocationPercent(), BigDecimal.ZERO);
    }

    @Test
    public void tesGetCashSweepInvestmentsForSubAccount_NoValuations() {
        Mockito.when(portfolioIntegrationService.loadWrapAccountValuation(any(com.bt.nextgen.service.integration.account.AccountKey.class),
                any(DateTime.class), any(ServiceErrors.class))).thenReturn(null);
        List<CashSweepInvestmentDto> cashSweepInvestmentDtos = accountProductsHelper.getCashSweepAssets(com.bt.nextgen.service.integration.account.AccountKey.valueOf("12345"), getSubAccount("prod1", ContainerType.DIRECT), serviceErrors);
        assertEquals(cashSweepInvestmentDtos.size(), 0);
    }

    @Test
    public void testConvertToCashSweepInvestmentDto() {
        List<CashSweepInvestmentDto> cashSweepInvestmentDtos = accountProductsHelper.
                convertToCashSweepInvestmentDto(getSubAccount("prod1", ContainerType.DIRECT).getCashSweepInvestmentAssetList(), serviceErrors);
        assertEquals(cashSweepInvestmentDtos.size(), 1);
        assertEquals(cashSweepInvestmentDtos.get(0).getAsset().getAssetId(), "asset1");
        assertEquals(cashSweepInvestmentDtos.get(0).getAllocationPercent(), BigDecimal.valueOf(20));
    }

    @Test
    public void testConvertToCashSweepInvestmentDto_NoInvestments() {
        List<CashSweepInvestmentDto> cashSweepInvestmentDtos = accountProductsHelper.convertToCashSweepInvestmentDto(
                new ArrayList<CashSweepInvestmentAsset>(), serviceErrors);
        assertEquals(cashSweepInvestmentDtos.size(), 0);
    }

    @Test
    public void testConvertToCashSweepInvestmentDto_emptyValuesInInvestments() {
        ArrayList<CashSweepInvestmentAsset> cashSweepInvestmentAssets = new ArrayList<>();

        CashSweepInvestmentAssetImpl cashSweepInvestmentAsset = new CashSweepInvestmentAssetImpl();
        cashSweepInvestmentAssets.add(cashSweepInvestmentAsset);

        List<CashSweepInvestmentDto> cashSweepInvestmentDtos = accountProductsHelper.convertToCashSweepInvestmentDto(cashSweepInvestmentAssets, serviceErrors);
        assertEquals(cashSweepInvestmentDtos.size(), 0);
    }

    @Test
    public void getFeatureKeyDirectAccount() {
        when(optionsService.getOption(any(OptionKey.class), any(AccountKey.class), any(ServiceErrors.class))).thenReturn("false");
        String featureKey = accountProductsHelper.getAccountFeatureKey(getAccount(), serviceErrors);
        assertEquals(featureKey, "direct.simple.individual");
    }

    @Test
    public void getFeatureKeyAdvisedAccount() {
        WrapAccountDetail account = mock(WrapAccountDetail.class);
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        when(brokerHelperService.getUserExperience(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn(UserExperience.ADVISED);
        when(optionsService.getOption(any(OptionKey.class), any(AccountKey.class), any(ServiceErrors.class))).thenReturn("");
        String featureKey = accountProductsHelper.getAccountFeatureKey(account, serviceErrors);
        assertEquals(featureKey, "advised.individual");
    }

    @Test
    public void getFeatureKeyAdvisedSuperAccount() {
        WrapAccountDetail account = mock(WrapAccountDetail.class);
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.SUPER);
        when(account.getSuperAccountSubType()).thenReturn(AccountSubType.ACCUMULATION);
        when(brokerHelperService.getUserExperience(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn(UserExperience.ADVISED);
        when(optionsService.getOption(any(OptionKey.class), any(AccountKey.class), any(ServiceErrors.class))).thenReturn("");
        String featureKey = accountProductsHelper.getAccountFeatureKey(account, serviceErrors);
        assertEquals(featureKey, "advised.super");
    }

    @Test
    public void getFeatureKeyAdvisedPensionAccount() {
        WrapAccountDetail account = mock(PensionAccountDetailImpl.class);
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.SUPER);
        when(account.getSuperAccountSubType()).thenReturn(AccountSubType.PENSION);
        when(brokerHelperService.getUserExperience(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn(UserExperience.ADVISED);
        when(optionsService.getOption(any(OptionKey.class), any(AccountKey.class), any(ServiceErrors.class))).thenReturn("");

        when(((PensionAccountDetail) account).isCommencementPending()).thenReturn(true);
        when(((PensionAccountDetail) account).getCommenceDate()).thenReturn(new DateTime("2016-01-01"));
        String featureKey = accountProductsHelper.getAccountFeatureKey(account, serviceErrors);
        assertEquals(featureKey, "advised.pension");

        when(((PensionAccountDetail) account).isCommencementPending()).thenReturn(true);
        when(((PensionAccountDetail) account).getCommenceDate()).thenReturn(null);
        featureKey = accountProductsHelper.getAccountFeatureKey(account, serviceErrors);
        assertEquals(featureKey, "advised.pension.noncommenced");

        when(((PensionAccountDetail) account).isCommencementPending()).thenReturn(false);
        when(((PensionAccountDetail) account).getCommenceDate()).thenReturn(new DateTime("2016-01-01"));
        featureKey = accountProductsHelper.getAccountFeatureKey(account, serviceErrors);
        assertEquals(featureKey, "advised.pension");

        when(((PensionAccountDetail) account).isCommencementPending()).thenReturn(false);
        when(((PensionAccountDetail) account).getCommenceDate()).thenReturn(new DateTime().plusDays(5 ));
        featureKey = accountProductsHelper.getAccountFeatureKey(account, serviceErrors);
        assertEquals(featureKey, "advised.pension.noncommenced");
    }

    @Test
    public void getFeatureKeyAdvisedPensionTtrAccount() {
        WrapAccountDetail account = mock(PensionAccountDetailImpl.class);
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.SUPER);
        when(account.getSuperAccountSubType()).thenReturn(AccountSubType.PENSION);
        when(((PensionAccountDetail) account).getPensionType()).thenReturn(PensionType.TTR);
        when(((PensionAccountDetail) account).getCommenceDate()).thenReturn(new DateTime("2016-01-01"));
        when(brokerHelperService.getUserExperience(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn(UserExperience.ADVISED);
        when(optionsService.getOption(any(OptionKey.class), any(AccountKey.class), any(ServiceErrors.class))).thenReturn("");
        String featureKey = accountProductsHelper.getAccountFeatureKey(account, serviceErrors);
        assertEquals(featureKey, "advised.pension.ttr");

        when(((PensionAccountDetail) account).getCommenceDate()).thenReturn(null);
        featureKey = accountProductsHelper.getAccountFeatureKey(account, serviceErrors);
        assertEquals(featureKey, "advised.pension.noncommenced");
    }

    @Test
    public void getFeatureKeyMigratedSuperAccount() {
        WrapAccountDetail account = mock(WrapAccountDetail.class);

        when(account.getMigrationKey()).thenReturn("testMigrationKey");
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.SUPER);
        when(account.getSuperAccountSubType()).thenReturn(AccountSubType.ACCUMULATION);
        when(brokerHelperService.getUserExperience(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn(UserExperience.ADVISED);
        when(optionsService.getOption(any(OptionKey.class), any(AccountKey.class), any(ServiceErrors.class))).thenReturn("");
        String featureKey = accountProductsHelper.getAccountFeatureKey(account, serviceErrors);
        assertEquals("migrated.super", featureKey);
    }

    @Test
    public void getFeatureKeyMigratedPensionAccount() {
        WrapAccountDetail account = mock(WrapAccountDetail.class);

        when(account.getMigrationKey()).thenReturn("testMigrationKey");
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.SUPER);
        when(account.getSuperAccountSubType()).thenReturn(AccountSubType.PENSION);
        when(brokerHelperService.getUserExperience(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn(UserExperience.ADVISED);
        when(optionsService.getOption(any(OptionKey.class), any(AccountKey.class), any(ServiceErrors.class))).thenReturn("");
        String featureKey = accountProductsHelper.getAccountFeatureKey(account, serviceErrors);
        assertEquals("migrated.pension", featureKey);
    }

    @Test
    public void getFeatureKeyMigratedAccounts() {
        WrapAccountDetail account = mock(WrapAccountDetail.class);

        when(account.getMigrationKey()).thenReturn("testMigrationKey");
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        when(brokerHelperService.getUserExperience(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn(UserExperience.ADVISED);
        when(optionsService.getOption(any(OptionKey.class), any(AccountKey.class), any(ServiceErrors.class))).thenReturn("");
        String featureKey = accountProductsHelper.getAccountFeatureKey(account, serviceErrors);
        assertEquals("migrated.individual", featureKey);

        when(account.getAccountStructureType()).thenReturn(AccountStructureType.Joint);
        featureKey = accountProductsHelper.getAccountFeatureKey(account, serviceErrors);
        assertEquals("migrated.joint", featureKey);

        when(account.getAccountStructureType()).thenReturn(AccountStructureType.Trust);
        featureKey = accountProductsHelper.getAccountFeatureKey(account, serviceErrors);
        assertEquals("migrated.trust", featureKey);

        when(account.getAccountStructureType()).thenReturn(AccountStructureType.Company);
        featureKey = accountProductsHelper.getAccountFeatureKey(account, serviceErrors);
        assertEquals("migrated.company", featureKey);

    }

    @Test
    public void getFeatureKeyDirectSuperAccount() {
        WrapAccountDetail account = mock(WrapAccountDetail.class);
        subscriptions.add(getProductSubscription("prod1"));

        when(account.getAccountStructureType()).thenReturn(AccountStructureType.SUPER);
        when(account.getSuperAccountSubType()).thenReturn(AccountSubType.ACCUMULATION);
        when(account.getProductKey()).thenReturn(ProductKey.valueOf("prod1"));
        when(account.getProductSubscription()).thenReturn(subscriptions);
        when(brokerHelperService.getUserExperience(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn(UserExperience.DIRECT);
        when(optionsService.getOption(any(OptionKey.class), any(AccountKey.class), any(ServiceErrors.class))).thenReturn("");
        String featureKey = accountProductsHelper.getAccountFeatureKey(account, serviceErrors);
        assertEquals(featureKey, "direct.active.super");
    }

    @Test
    public void getFeatureKeyCmaAccount() {
        WrapAccountDetail account = mock(WrapAccountDetail.class);
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        when(account.getProductKey()).thenReturn(ProductKey.valueOf("prod1"));
        when(brokerHelperService.getUserExperience(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn(UserExperience.ADVISED);
        when(optionsService.getOption(any(OptionKey.class), any(AccountKey.class), any(ServiceErrors.class))).thenReturn("cma");
        String featureKey = accountProductsHelper.getAccountFeatureKey(account, serviceErrors);
        assertEquals(featureKey, "cma.individual");
    }


    private ProductSubscription getProductSubscription(String prodId) {
        ProductSubscriptionImpl productSubscription = new ProductSubscriptionImpl();
        productSubscription.setSubscribedProductId(prodId);
        return productSubscription;
    }

    private WrapAccountDetail getAccount() {
        List<ProductSubscription> productSubscriptions = new ArrayList<>();
        productSubscriptions.add(getProductSubscription("prod2"));

        WrapAccountDetailImpl accountDetail = mock(WrapAccountDetailImpl.class);
        when(accountDetail.getModificationSeq()).thenReturn("2");
        when(accountDetail.getProductKey()).thenReturn(ProductKey.valueOf("prod2"));
        when(accountDetail.isOpen()).thenReturn(false);
        when(accountDetail.isHasMinCash()).thenReturn(false);
        when(accountDetail.getProductSubscription()).thenReturn(productSubscriptions);
        when(accountDetail.getInitialInvestmentAsset()).thenReturn(initialInvestmentAssets);
        when(accountDetail.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        return accountDetail;
    }

    private SubAccount getSubAccount(String productId, ContainerType containerType) {
        SubAccount subAccount = mock(SubAccount.class);
        ProductIdentifier pid = mock(ProductIdentifier.class);
        when(pid.getProductKey()).thenReturn(ProductKey.valueOf(productId));
        when(subAccount.getProductIdentifier()).thenReturn(pid);
        when(subAccount.getSubAccountType()).thenReturn(containerType);
        PowerMockito.when(subAccount.getCashSweepInvestmentAssetList()).thenReturn(cashSweepInvestmentAssets);
        return subAccount;
    }

    private Product getProduct(String productId, String productShortName, String parentProductId, ProductLevel level) {
        Product product = mock(Product.class);
        when(product.getProductKey()).thenReturn(ProductKey.valueOf(productId));
        when(product.isActive()).thenReturn(false);
        when(product.isLicenseeFeeActive()).thenReturn(false);
        when(product.getProductName()).thenReturn(productId);
        when(product.getShortName()).thenReturn(productShortName);
        when(product.isDirect()).thenReturn(false);
        when(product.isSuper()).thenReturn(false);
        when(product.getParentProductKey()).thenReturn(ProductKey.valueOf(parentProductId));
        when(product.getProductLevel()).thenReturn(level);
        when(product.getInvestmentBuffer()).thenReturn(BigDecimal.valueOf(100));
        return product;
    }

    private WrapAccountValuation setupMockAccountValuation() {
        WrapAccountValuation wrapAccountValuation = mock(WrapAccountValuation.class);
        SubAccountValuation mpValuation = mock(ManagedPortfolioAccountValuation.class);
        when(mpValuation.getAssetType()).thenReturn(AssetType.MANAGED_PORTFOLIO);
        when(((ManagedPortfolioAccountValuation) mpValuation).getAsset()).thenReturn(asset);

        SubAccountValuation mfValuation = mock(SubAccountValuation.class);
        AccountHolding holding = mock(AccountHolding.class);
        ManagedFundAsset mfAsset = mock(ManagedFundAsset.class);
        when(mfAsset.getAssetId()).thenReturn("asset2");
        when(mfAsset.getAssetCode()).thenReturn("code2");
        when(mfAsset.getAssetName()).thenReturn("asset2");
        when(mfAsset.getAssetType()).thenReturn(AssetType.MANAGED_FUND);
        when(holding.getAsset()).thenReturn(mfAsset);

        AccountHolding holdingWithPrepaymentAsset = mock(AccountHolding.class);
        ManagedFundAsset mfAsset2 = mock(ManagedFundAsset.class);
        when(mfAsset2.getAssetId()).thenReturn("asset3");
        when(mfAsset2.getAssetCode()).thenReturn("code3");
        when(mfAsset2.getAssetName()).thenReturn("asset3");
        when(mfAsset2.getAssetType()).thenReturn(AssetType.MANAGED_FUND);
        when(mfAsset2.isPrepayment()).thenReturn(true);
        when(holdingWithPrepaymentAsset.getReferenceAsset()).thenReturn(mfAsset2);

        when(mfValuation.getAssetType()).thenReturn(AssetType.MANAGED_FUND);
        when(mfValuation.getHoldings()).thenReturn(Arrays.asList(holding, holdingWithPrepaymentAsset));

        List<SubAccountValuation> subAccountValuations = new ArrayList<>();
        subAccountValuations.add(mpValuation);
        subAccountValuations.add(mfValuation);

        when(wrapAccountValuation.getSubAccountValuations()).thenReturn(subAccountValuations);
        return wrapAccountValuation;
    }
}
