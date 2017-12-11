package com.bt.nextgen.api.asset.service;

import com.bt.nextgen.api.account.v2.model.AccountSubscription;
import com.bt.nextgen.api.asset.model.ManagedPortfolioAssetDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.ManagedPortfolioAssetImpl;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.bt.nextgen.service.avaloq.product.ProductLevel;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SimplePortfolioAssetDtoServiceImplTest {

    @InjectMocks
    private SimplePortfolioAssetDtoServiceImpl simplePortfolioAssetDtoService;

    @Mock
    private AssetIntegrationService assetIntegrationService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    private Map<String, Asset> assetMap = new HashMap<>();
    private ServiceErrors serviceErrors = new ServiceErrorsImpl();

    @Before
    public void setup() throws Exception {
        assetMap.put("1", getManagedPortfolio("WFS0583AU", "Conservative"));
        assetMap.put("2", getManagedPortfolio("WFS0584AU", "Growth"));
        assetMap.put("3", getManagedPortfolio("WFS0585AU", "High Growth"));
        assetMap.put("4", getManagedPortfolio("WFS0586AU", "Moderate"));
        assetMap.put("5", getManagedPortfolio("WFS0587AU", "Balanced"));
    }

    @Test
    public void testFindAll() {
        when(productIntegrationService.loadProducts(Mockito.any(ServiceErrors.class))).thenReturn(getProductList());
        when(assetIntegrationService.loadAssets(anyList(), Mockito.any(ServiceErrors.class))).thenReturn(assetMap);
        List<ManagedPortfolioAssetDto> availableAssets = simplePortfolioAssetDtoService.findAll(serviceErrors);
        assertNotNull(availableAssets);
        Assert.assertEquals(availableAssets.size(), 5);
        Assert.assertEquals((select(availableAssets, having(on(ManagedPortfolioAssetDto.class).getInvestmentStyle(),
                anyOf(is("Conservative"), is("Growth"), is("High Growth"), is("Moderate"), is("Balanced"))))).size(), 5);
        Assert.assertEquals((select(availableAssets, having(on(ManagedPortfolioAssetDto.class).getInvestmentStyle(),
                is("Conservative Growth"))).size()), 0);
    }

    private ManagedPortfolioAssetImpl getManagedPortfolio(String assetCode, String investmentStyle) {
        ManagedPortfolioAssetImpl managedPortfolioAsset = new ManagedPortfolioAssetImpl();
        managedPortfolioAsset.setAssetCode(assetCode);
        managedPortfolioAsset.setIpsInvestmentStyle(investmentStyle);
        return managedPortfolioAsset;
    }

    private List<Product> getProductList() {
        List<Product> products = new ArrayList<>();

        ProductImpl product1 = new ProductImpl();
        ProductImpl product2 = new ProductImpl();
        ProductImpl product3 = new ProductImpl();

        product1.setProductKey(ProductKey.valueOf("1234"));
        product1.setShortName("PROD.WL.060F52DC6D17421EAF1632AC9EFAE210");
        product1.setProductName("BT Direct");
        product1.setProductLevel(ProductLevel.WHITE_LABEL);
        product1.setParentProductId("1");

        product2.setProductKey(ProductKey.valueOf("5678"));
        product2.setShortName(AccountSubscription.SIMPLE.getSubscriptionProduct());
        product2.setProductName("BT Direct simple");
        product2.setProductLevel(ProductLevel.OFFER);
        product2.setParentProductId("2");

        product3.setProductKey(ProductKey.valueOf("Offer1234"));
        product3.setShortName(AccountSubscription.ACTIVE.getSubscriptionProduct());
        product3.setProductName("BT Direct active");
        product3.setProductLevel(ProductLevel.OFFER);
        product3.setParentProductId("2");

        products.add(product1);
        products.add(product2);
        products.add(product3);

        return products;
    }
}
