package com.bt.nextgen.api.account.v3.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.account.v3.model.AccountSubscriptionDto;
import com.bt.nextgen.api.account.v3.model.DirectOffer;
import com.bt.nextgen.api.account.v3.model.InitialInvestmentDto;
import com.bt.nextgen.api.account.v3.util.AccountProductsHelper;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.UpdateSubscriptionResponseImpl;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.asset.ManagedFundAssetImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.btfin.panorama.service.integration.account.InitialInvestmentAsset;
import com.bt.nextgen.service.integration.account.InitialInvestmentRequest;
import com.btfin.panorama.service.integration.account.ProductSubscription;
import com.bt.nextgen.service.integration.account.SubscriptionRequest;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.account.direct.InitialInvestmentAssetImpl;
import com.bt.nextgen.service.integration.account.direct.ProductSubscriptionImpl;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class AccountSubscriptionDtoServiceTest {

    @InjectMocks
    private AccountSubscriptionDtoServiceImpl accountSubscriptionDtoService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private AccountProductsHelper subscriptionHelper;

    private ServiceErrors serviceErrors;
    private List<ProductSubscription> subscriptions;
    private List<InitialInvestmentAsset> initialInvestmentAssets = new ArrayList<>();
    private WrapAccountDetailImpl simpleAccount;
    private WrapAccountDetailImpl activeAccount;
    private ManagedFundAssetImpl asset;
    private final Map<ProductKey, Product> productMap = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        serviceErrors = new ServiceErrorsImpl();
        subscriptions = new ArrayList<>();
        simpleAccount = getAccount("prod2");
        activeAccount = getAccount("prod1");
        productMap.put(ProductKey.valueOf("prod1"), getProduct("prod1", "DIRE.BTPI.ACTIVE"));
        productMap.put(ProductKey.valueOf("prod2"), getProduct("prod2", "DIRE.BTPI.SIMPLE"));
        productMap.put(ProductKey.valueOf("prod3"), getProduct("prod3", "DIRE.BTPI"));
        productMap.put(ProductKey.valueOf("prod4"), getProduct("prod4", "PROD.WL.PANORAMA"));

        asset = new ManagedFundAssetImpl();
        asset.setAssetId("123456");
        asset.setAssetCode("code1");
        asset.setAssetName("asset1");

        InitialInvestmentAssetImpl initialInvestmentAsset = mock(InitialInvestmentAssetImpl.class);
        when(initialInvestmentAsset.getInvestmentAssetId()).thenReturn("123456");
        when(initialInvestmentAsset.getInitialInvestmentAmount()).thenReturn(BigDecimal.valueOf(2000));
        initialInvestmentAssets.add(initialInvestmentAsset);

        simpleAccount.setInitialInvestmentAsset(initialInvestmentAssets);
    }

    @Test
    public void TestFindByKeyActiveSuccess() {
        AccountKey accountKey = new AccountKey(EncodedString.fromPlainText("account1").toString());
        when(accountIntegrationService.loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class), any(ServiceErrors.class))).thenReturn(activeAccount);
        when(subscriptionHelper.getSubscriptionType(any(WrapAccountDetail.class), any(ServiceErrors.class))).thenReturn(DirectOffer.ACTIVE.getSubscriptionType());
        AccountSubscriptionDto result = accountSubscriptionDtoService.find(accountKey, serviceErrors);
        assertNotNull(result);
        assertEquals(result.getSubscriptionType(), DirectOffer.ACTIVE.getSubscriptionType());
        assertEquals(result.getInitialInvestments().size(), 0);
    }

    @Test
    public void TestFindByKeySimpleSuccess() {
        AccountKey accountKey = new AccountKey(EncodedString.fromPlainText("account1").toString());
        List<InitialInvestmentDto> initialInvestmentDtos = Collections.singletonList(new InitialInvestmentDto(asset, new BigDecimal(2000)));

        when(accountIntegrationService.loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class), any(ServiceErrors.class))).thenReturn(simpleAccount);
        when(subscriptionHelper.getSubscriptionType(any(WrapAccountDetail.class), any(ServiceErrors.class))).thenReturn(DirectOffer.SIMPLE.getSubscriptionType());
        when(subscriptionHelper.getInitialInvestments(any(WrapAccountDetail.class), any(ServiceErrors.class))).thenReturn(initialInvestmentDtos);

        AccountSubscriptionDto result = accountSubscriptionDtoService.find(accountKey, serviceErrors);
        assertNotNull(result);
        assertEquals(result.getSubscriptionType(), DirectOffer.SIMPLE.getSubscriptionType());
        assertEquals(result.getInitialInvestments().size(), 1);
        assertEquals(result.getInitialInvestments().get(0).getAsset().getAssetId(), "123456");
        assertEquals(result.getInitialInvestments().get(0).getAmount(), new BigDecimal(2000));
    }

    @Test
    public void TestFindByKeyFail() {
        AccountKey accountKey = new AccountKey(EncodedString.fromPlainText("account1").toString());
        when(accountIntegrationService.loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class), any(ServiceErrors.class))).thenReturn(null);
        AccountSubscriptionDto result = accountSubscriptionDtoService.find(accountKey, serviceErrors);
        assertNull(result);
    }

    @Test
    public void TestUpdateSubscriptionSimpleWithoutInvestmentSuccess() {
        AccountSubscriptionDto accountSubscriptionDto = new AccountSubscriptionDto(new AccountKey(EncodedString.fromPlainText("account1").toString()), "simple");
        subscriptions = new ArrayList<>();
        subscriptions.add(getProductSubscription("prod2"));
        UpdateSubscriptionResponseImpl response = mock(UpdateSubscriptionResponseImpl.class);
        when(response.getAccountKey()).thenReturn(com.bt.nextgen.service.integration.account.AccountKey.valueOf("121212"));
        when(response.getSubscriptions()).thenReturn(subscriptions);

        when(accountIntegrationService.addSubscription(any(SubscriptionRequest.class), any(ServiceErrors.class))).thenReturn(response);
        when(accountIntegrationService.loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class), any(ServiceErrors.class))).thenReturn(activeAccount);
        when(subscriptionHelper.getSubscriptionType(anyList(), anyBoolean(), any(ServiceErrors.class))).thenReturn(DirectOffer.SIMPLE.getSubscriptionType());

        AccountSubscriptionDto result = accountSubscriptionDtoService.update(accountSubscriptionDto, serviceErrors);
        assertNotNull(result);
        assertEquals(result.getSubscriptionType(), DirectOffer.SIMPLE.getSubscriptionType());
        assertEquals(result.getInitialInvestments().size(), 0);
    }

    @Test
    public void TestUpdateSubscriptionSimpleWithInvestmentSuccess() {
        List<InitialInvestmentDto> initialInvestmentDtos = Collections.singletonList(new InitialInvestmentDto(asset, new BigDecimal(2000)));

        AccountSubscriptionDto accountSubscriptionDto = new AccountSubscriptionDto(new AccountKey(EncodedString.fromPlainText("account1").toString()), "simple", initialInvestmentDtos);
        subscriptions = new ArrayList<>();
        subscriptions.add(getProductSubscription("prod2"));
        UpdateSubscriptionResponseImpl response = mock(UpdateSubscriptionResponseImpl.class);
        when(response.getAccountKey()).thenReturn(com.bt.nextgen.service.integration.account.AccountKey.valueOf("121212"));
        when(response.getSubscriptions()).thenReturn(subscriptions);
        when(response.getInitialInvestmentAsset()).thenReturn(initialInvestmentAssets);

        when(accountIntegrationService.addSubscription(any(SubscriptionRequest.class), any(ServiceErrors.class))).thenReturn(response);
        when(accountIntegrationService.addInitialInvestment(any(InitialInvestmentRequest.class), any(ServiceErrors.class))).thenReturn(response);
        when(accountIntegrationService.loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class), any(ServiceErrors.class))).thenReturn(activeAccount);
        when(subscriptionHelper.getSubscriptionType(anyList(), anyBoolean(), any(ServiceErrors.class)))
                .thenReturn(DirectOffer.SIMPLE.getSubscriptionType());
        when(subscriptionHelper.convertToInitialInvestmentDto(anyList(), any(ServiceErrors.class))).thenReturn(initialInvestmentDtos);
        AccountSubscriptionDto result = accountSubscriptionDtoService.update(accountSubscriptionDto, serviceErrors);
        assertNotNull(result);
        assertEquals(result.getSubscriptionType(), DirectOffer.SIMPLE.getSubscriptionType());
        assertEquals(result.getInitialInvestments().get(0).getAsset().getAssetId(), "123456");
        assertEquals(result.getInitialInvestments().get(0).getAsset().getAssetCode(), "code1");
        assertEquals(result.getInitialInvestments().get(0).getAsset().getAssetName(), "asset1");
        assertEquals(result.getInitialInvestments().get(0).getAmount(), new BigDecimal(2000));
    }

    @Test
    public void TestUpdateSubscriptionFailure() {
        AccountSubscriptionDto accountSubscriptionDto = new AccountSubscriptionDto(new AccountKey(EncodedString.fromPlainText("account1").toString()), "active");
        UpdateSubscriptionResponseImpl response = mock(UpdateSubscriptionResponseImpl.class);
        when(response.getAccountKey()).thenReturn(com.bt.nextgen.service.integration.account.AccountKey.valueOf("121212"));
        when(response.getSubscriptions()).thenReturn(subscriptions);

        when(accountIntegrationService.addSubscription(any(SubscriptionRequest.class), any(ServiceErrors.class))).thenReturn(null);
        when(accountIntegrationService.loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class), any(ServiceErrors.class))).thenReturn(activeAccount);
        AccountSubscriptionDto result = accountSubscriptionDtoService.update(accountSubscriptionDto, serviceErrors);
        assertNull(result.getSubscriptionType());
        assertEquals(result.getInitialInvestments().size(), 0);
    }

    private ProductSubscription getProductSubscription(String prodId) {
        ProductSubscriptionImpl productSubscription = new ProductSubscriptionImpl();
        productSubscription.setSubscribedProductId(prodId);
        return productSubscription;
    }

    private WrapAccountDetailImpl getAccount(String product) {
        List<ProductSubscription> productSubscriptions = new ArrayList<>();
        productSubscriptions.add(getProductSubscription(product));

        WrapAccountDetailImpl accountDetail = Mockito.mock(WrapAccountDetailImpl.class);
        Mockito.when(accountDetail.getModificationSeq()).thenReturn("2");
        Mockito.when(accountDetail.isOpen()).thenReturn(false);
        Mockito.when(accountDetail.isHasMinCash()).thenReturn(false);
        Mockito.when(accountDetail.getProductSubscription()).thenReturn(productSubscriptions);
        return accountDetail;
    }

    public Product getProduct(String productId, String productShortName) {
        Product product = Mockito.mock(Product.class);
        Mockito.when(product.getProductKey()).thenReturn(ProductKey.valueOf(productId));
        Mockito.when(product.isActive()).thenReturn(false);
        Mockito.when(product.isLicenseeFeeActive()).thenReturn(false);
        Mockito.when(product.getShortName()).thenReturn(productShortName);
        Mockito.when(product.isDirect()).thenReturn(false);
        return product;
    }
}
