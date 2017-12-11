package com.bt.nextgen.api.account.v2.service;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.account.v2.model.AccountSubscription;
import com.bt.nextgen.api.account.v2.model.AccountSubscriptionDto;
import com.bt.nextgen.api.account.v2.model.InitialInvestmentAssetDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.UpdateSubscriptionResponseImpl;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.asset.ManagedFundAssetImpl;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.btfin.panorama.service.integration.account.InitialInvestmentAsset;
import com.bt.nextgen.service.integration.account.InitialInvestmentRequest;
import com.btfin.panorama.service.integration.account.ProductSubscription;
import com.bt.nextgen.service.integration.account.SubscriptionRequest;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.account.direct.InitialInvestmentAssetImpl;
import com.bt.nextgen.service.integration.account.direct.ProductSubscriptionImpl;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountSubscriptionDtoServiceTest {

    @InjectMocks
    private AccountSubscriptionDtoServiceImpl accountSubscriptionDtoService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private AssetIntegrationService assetIntegrationService;

    private ServiceErrors serviceErrors;
    private List<ProductSubscription> subscriptions;
    private List<InitialInvestmentAsset> initialInvestmentAssets = new ArrayList<>();
    private WrapAccountDetail account;
    private Map<ProductKey, Product> productMap = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        serviceErrors = new ServiceErrorsImpl();
        subscriptions = new ArrayList<>();
        account = getAccount();
        productMap.put(ProductKey.valueOf("prod1"), getProduct("prod1", "DIRE.BTPI.ACTIVE"));
        productMap.put(ProductKey.valueOf("prod2"), getProduct("prod2", "DIRE.BTPI.SIMPLE"));
        productMap.put(ProductKey.valueOf("prod3"), getProduct("prod3", "DIRE.BTPI"));
        productMap.put(ProductKey.valueOf("prod4"), getProduct("prod4", "PROD.WL.PANORAMA"));
        InitialInvestmentAssetImpl initialInvestmentAsset = new InitialInvestmentAssetImpl();
        initialInvestmentAsset.setInitialInvestmentAmount(new BigDecimal(2000));
        initialInvestmentAsset.setInitialInvestmentAssetId("123456");
        initialInvestmentAssets.add(initialInvestmentAsset);
    }

    @Test
    public void TestUpdateSubscriptionActiveSuccess() {
        AccountSubscriptionDto accountSubscriptionDto = new AccountSubscriptionDto(new AccountKey(EncodedString.fromPlainText("account1").toString()), "active");
        subscriptions = new ArrayList<>();
        subscriptions.add(getProductSubscription("prod1"));
        subscriptions.add(getProductSubscription("prod2"));
        UpdateSubscriptionResponseImpl response = new UpdateSubscriptionResponseImpl(com.bt.nextgen.service.integration.account.AccountKey.valueOf("121212"), subscriptions, null);
        when(accountIntegrationService.addSubscription(any(SubscriptionRequest.class), any(ServiceErrors.class))).thenReturn(response);
        when(accountIntegrationService.loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class), any(ServiceErrors.class))).thenReturn(account);
        when(productIntegrationService.loadProductsMap(any(ServiceErrors.class))).thenReturn(productMap);
        AccountSubscriptionDto result = accountSubscriptionDtoService.update(accountSubscriptionDto, serviceErrors);
        assertNotNull(result);
        assertEquals(result.getSubscriptionType(), AccountSubscription.ACTIVE.getSubscriptionType());
        assertNull(result.getInitialInvestments());
    }

    @Test
    public void TestUpdateSubscriptionSimpleWithoutInvestmentSuccess() {
        AccountSubscriptionDto accountSubscriptionDto = new AccountSubscriptionDto(new AccountKey(EncodedString.fromPlainText("account1").toString()), "simple");
        subscriptions = new ArrayList<>();
        subscriptions.add(getProductSubscription("prod2"));
        UpdateSubscriptionResponseImpl response = new UpdateSubscriptionResponseImpl(
                com.bt.nextgen.service.integration.account.AccountKey.valueOf("121212"), subscriptions, null);
        when(accountIntegrationService.addSubscription(any(SubscriptionRequest.class), any(ServiceErrors.class))).thenReturn(response);
        when(accountIntegrationService.loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class), any(ServiceErrors.class))).thenReturn(account);
        when(productIntegrationService.loadProductsMap(any(ServiceErrors.class))).thenReturn(productMap);
        AccountSubscriptionDto result = accountSubscriptionDtoService.update(accountSubscriptionDto, serviceErrors);
        assertNotNull(result);
        assertEquals(result.getSubscriptionType(), AccountSubscription.SIMPLE.getSubscriptionType());
        assertNull(result.getInitialInvestments());
    }

    @Test
    public void TestUpdateSubscriptionSimpleWithInvestmentSuccess() {
        ManagedFundAssetImpl asset = new ManagedFundAssetImpl();
        asset.setAssetId("asset1");
        asset.setAssetCode("code1");
        asset.setAssetName("asset1");

        AccountSubscriptionDto accountSubscriptionDto = new AccountSubscriptionDto(new AccountKey(EncodedString.fromPlainText("account1").toString()), "simple",
                Collections.singletonList(new InitialInvestmentAssetDto(asset, new BigDecimal(1000))));
        subscriptions = new ArrayList<>();
        subscriptions.add(getProductSubscription("prod2"));
        UpdateSubscriptionResponseImpl response = new UpdateSubscriptionResponseImpl(com.bt.nextgen.service.integration.account.AccountKey.valueOf("121212"), subscriptions, initialInvestmentAssets);
        when(accountIntegrationService.addSubscription(any(SubscriptionRequest.class), any(ServiceErrors.class))).thenReturn(response);
        when(assetIntegrationService.loadAsset(anyString(), any(ServiceErrors.class))).thenReturn(asset);

        when(accountIntegrationService.addInitialInvestment(any(InitialInvestmentRequest.class), any(ServiceErrors.class))).thenReturn(response);
        when(accountIntegrationService.loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class), any(ServiceErrors.class))).thenReturn(account);
        when(productIntegrationService.loadProductsMap(any(ServiceErrors.class))).thenReturn(productMap);
        AccountSubscriptionDto result = accountSubscriptionDtoService.update(accountSubscriptionDto, serviceErrors);
        assertNotNull(result);
        assertEquals(result.getSubscriptionType(), AccountSubscription.SIMPLE.getSubscriptionType());
        assertEquals(result.getInitialInvestments().get(0).getAssetId(), "asset1");
        assertEquals(result.getInitialInvestments().get(0).getAssetCode(), "code1");
        assertEquals(result.getInitialInvestments().get(0).getAssetName(), "asset1");
        assertEquals(result.getInitialInvestments().get(0).getAmount(), new BigDecimal(2000));
    }

    @Test
    public void TestUpdateSubscriptionFailure() {
        AccountSubscriptionDto accountSubscriptionDto = new AccountSubscriptionDto(new AccountKey(EncodedString.fromPlainText("account1").toString()), "active");
        when(accountIntegrationService.addSubscription(any(SubscriptionRequest.class), any(ServiceErrors.class))).thenReturn(null);
        when(accountIntegrationService.loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class), any(ServiceErrors.class))).thenReturn(account);
        AccountSubscriptionDto result = accountSubscriptionDtoService.update(accountSubscriptionDto, serviceErrors);
        assertNull(result.getKey());
    }

    @Test
    public void testGetSubscriptionTypeActive() {
        subscriptions.add(getProductSubscription("prod1"));
        subscriptions.add(getProductSubscription("prod2"));
        String subscriptionType = accountSubscriptionDtoService.getSubscriptionType(subscriptions, productMap);
        assertThat(subscriptionType, is("active"));
    }

    @Test
    public void testGetSubscriptionTypeSimple() {
        subscriptions.add(getProductSubscription("prod2"));
        subscriptions.add(getProductSubscription("prod3"));
        String subscriptionType = accountSubscriptionDtoService.getSubscriptionType(subscriptions, productMap);
        assertThat(subscriptionType, is("simple"));
    }

    @Test
    public void testGetSubscriptionTypeUndecided() {
        subscriptions.add(getProductSubscription("prod3"));
        subscriptions.add(getProductSubscription("prod4"));
        String subscriptionType = accountSubscriptionDtoService.getSubscriptionType(subscriptions, productMap);
        assertThat(subscriptionType, is("undecided"));
    }

    @Test
    public void testGetSubscriptionTypeNoSubscription() {
        String subscriptionType = accountSubscriptionDtoService.getSubscriptionType(null, productMap);
        assertThat(subscriptionType, is("undecided"));
    }

    private ProductSubscription getProductSubscription(String prodId) {
        ProductSubscriptionImpl productSubscription = new ProductSubscriptionImpl();
        productSubscription.setSubscribedProductId(prodId);
        return productSubscription;
    }

    private WrapAccountDetail getAccount() {
        List<ProductSubscription> productSubscriptions = new ArrayList<>();
        productSubscriptions.add(getProductSubscription("prod2"));

        WrapAccountDetailImpl wrapAccountDetail = new WrapAccountDetailImpl();
        wrapAccountDetail.setModificationSeq("2");
        wrapAccountDetail.setProductSubscription(productSubscriptions);
        return wrapAccountDetail;
    }

    public Product getProduct(String productId, String productShortName) {
        ProductImpl product = new ProductImpl();
        product.setProductKey(ProductKey.valueOf(productId));
        product.setShortName(productShortName);
        return product;
    }
}
