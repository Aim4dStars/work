package com.bt.nextgen.api.product.service;

import com.bt.nextgen.api.product.model.ProductDocumentDto;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.bt.nextgen.service.avaloq.product.ProductLevel;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductDocumentsDtoServiceTest {

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private AssetIntegrationService assetIntegrationService;

    @InjectMocks
    private ProductDocumentsDtoServiceImpl productDocumentsDtoService;

    private ServiceErrors serviceErrors;
    private List<Asset> assetList;
    List<Product> productList;
    List<Product> productDetailList;

    @Before
    public void setup() throws Exception {
        serviceErrors = new ServiceErrorsImpl();
        Broker dealerGroup = getDealerGroup();
        assetList = getAssetList();
        productList = getProductList();
        productDetailList = getProductDetailList();

        Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(dealerGroup);
    }

    @Test
    public void testProductDocumentsNoDealerKey() {
        Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(null);
        ProductDocumentDto productDocumentDto = productDocumentsDtoService.findOne(serviceErrors);
        assertNull(productDocumentDto);
    }
    @Test
    public void testProductDocumentsSuccess() {
        Mockito.when(assetIntegrationService.loadAvailableAssets(Mockito.any(BrokerKey.class),
                Mockito.any(ProductKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(assetList);
        Mockito.when(productIntegrationService.getDealerGroupProductList(Mockito.any(BrokerKey.class),
                Mockito.any(ServiceErrors.class)))
                .thenReturn(productList);
        Mockito.when(productIntegrationService.loadProducts(Mockito.any(ServiceErrors.class)))
                .thenReturn(productDetailList);
        ProductDocumentDto productDocumentDto = productDocumentsDtoService.findOne(serviceErrors);
        assertNotNull(productDocumentDto.getProductList());
        assertNotNull(productDocumentDto.getBrandList());
        assertEquals(productDocumentDto.getProductList().size(), 3);
        assertEquals(productDocumentDto.getBrandList().size(), 2);
    }

    @Test
    public void testProductDocumentsNoAssets() {
        Mockito.when(assetIntegrationService.loadAvailableAssets(Mockito.any(BrokerKey.class),
                Mockito.any(ProductKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(null);
        Mockito.when(productIntegrationService.getDealerGroupProductList(Mockito.any(BrokerKey.class),
                Mockito.any(ServiceErrors.class)))
                .thenReturn(productList);
        Mockito.when(productIntegrationService.loadProducts(Mockito.any(ServiceErrors.class)))
                .thenReturn(productDetailList);
        ProductDocumentDto productDocumentDto = productDocumentsDtoService.findOne(serviceErrors);
        assertNotNull(productDocumentDto.getProductList());
        assertNotNull(productDocumentDto.getBrandList());
        assertEquals(productDocumentDto.getProductList().size(), 3);
        assertEquals(productDocumentDto.getBrandList().size(), 0);
    }

    @Test
    public void testProductDocumentsNoProducts() {
        Mockito.when(assetIntegrationService.loadAvailableAssets(Mockito.any(BrokerKey.class),
                Mockito.any(ProductKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(assetList);
        Mockito.when(productIntegrationService.getDealerGroupProductList(Mockito.any(BrokerKey.class),
                Mockito.any(ServiceErrors.class)))
                .thenReturn(null);
        Mockito.when(productIntegrationService.loadProducts(Mockito.any(ServiceErrors.class)))
                .thenReturn(productDetailList);
        ProductDocumentDto productDocumentDto = productDocumentsDtoService.findOne(serviceErrors);
        assertNotNull(productDocumentDto.getProductList());
        assertNotNull(productDocumentDto.getBrandList());
        assertEquals(productDocumentDto.getProductList().size(), 0);
        assertEquals(productDocumentDto.getBrandList().size(), 0);
    }

    @Test
    public void testProductDocumentsNoProductDetails() {
        Mockito.when(productIntegrationService.getDealerGroupProductList(Mockito.any(BrokerKey.class),
                Mockito.any(ServiceErrors.class)))
                .thenReturn(productList);
        Mockito.when(productIntegrationService.loadProducts(Mockito.any(ServiceErrors.class)))
                .thenReturn(null);
        ProductDocumentDto productDocumentDto = productDocumentsDtoService.findOne(serviceErrors);
        assertNotNull(productDocumentDto.getProductList());
        assertNotNull(productDocumentDto.getBrandList());
        assertEquals(productDocumentDto.getProductList().size(), 3);
        assertEquals(productDocumentDto.getBrandList().size(), 0);
    }

    @Test
    public void testIsManagedFundAvailable() {
        Mockito.when(assetIntegrationService.loadAvailableAssets(Mockito.any(BrokerKey.class),
                Mockito.any(ProductKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(assetList);
        Mockito.when(productIntegrationService.getDealerGroupProductList(Mockito.any(BrokerKey.class),
                Mockito.any(ServiceErrors.class)))
                .thenReturn(productList);
        Mockito.when(productIntegrationService.loadProducts(Mockito.any(ServiceErrors.class)))
                .thenReturn(productDetailList);
        ProductDocumentDto productDocumentDto = productDocumentsDtoService.findOne(serviceErrors);
        assertNotNull(productDocumentDto.getProductList());
        assertNotNull(productDocumentDto.getBrandList());
        assertEquals(productDocumentDto.getProductList().size(), 3);
        assertEquals(productDocumentDto.getBrandList().size(), 2);
        assertTrue(productDocumentDto.isManagedFundAvailable());
    }

    private List<Asset> getAssetList() {
        List<Asset> assets = new ArrayList<>();
        AssetImpl asset1 = new AssetImpl();
        asset1.setBrand("80000052");
        asset1.setAssetType(AssetType.TERM_DEPOSIT);

        AssetImpl asset2 = new AssetImpl();
        asset2.setBrand("80000053");
        asset2.setAssetType(AssetType.TERM_DEPOSIT);

        AssetImpl asset3 = new AssetImpl();
        asset3.setBrand("80000059");
        asset3.setAssetType(AssetType.MANAGED_PORTFOLIO);

        AssetImpl asset4 = new AssetImpl();
        asset3.setBrand("80000060");
        asset3.setAssetType(AssetType.MANAGED_FUND);

        assets.add(asset1);
        assets.add(asset2);
        assets.add(asset3);
        assets.add(asset4);

        return assets;
    }

    private List<Product> getProductList() {
        List<Product> products = new ArrayList<>();

        ProductImpl product1 = new ProductImpl();
        ProductImpl product2 = new ProductImpl();
        ProductImpl product3 = new ProductImpl();

        product1.setProductKey(ProductKey.valueOf("1234"));
        product1.setShortName("PROD.WL.060F52DC6D17421EAF1632AC9EFAE210");
        product1.setProductName("Asset Administrator");
        product1.setProductLevel(ProductLevel.WHITE_LABEL);
        product1.setParentProductId("1");

        product2.setProductKey(ProductKey.valueOf("5678"));
        product2.setShortName("PROD.WL.35D1B6570418");
        product2.setProductName("BT Panorama");
        product2.setProductLevel(ProductLevel.WHITE_LABEL);
        product2.setParentProductId("2");

        product3.setProductKey(ProductKey.valueOf("Offer1234"));
        product3.setShortName("PROD.OFFER.IFAO");
        product3.setProductName("IFA Open");
        product3.setProductLevel(ProductLevel.OFFER);
        product3.setDirect(true);
        product3.setParentProductId("2");

        products.add(product1);
        products.add(product2);
        products.add(product3);

        return products;

    }

    private List<Product> getProductDetailList() {
        List<Product> products = new ArrayList<>();

        ProductImpl product1 = new ProductImpl();
        ProductImpl product2 = new ProductImpl();
        ProductImpl product3 = new ProductImpl();
        ProductImpl product4 = new ProductImpl();
        ProductImpl product5 = new ProductImpl();

        product1.setProductKey(ProductKey.valueOf("12345"));
        product1.setProductLevel(ProductLevel.OFFER);
        product1.setParentProductId("1234");
        product1.setShortName("Offer.12345");
        product1.setDirect(false);

        product3.setProductKey(ProductKey.valueOf("123"));
        product3.setProductLevel(ProductLevel.MODEL);
        product3.setDirect(true);
        product3.setParentProductId("12345");

        product2.setProductKey(ProductKey.valueOf("56789"));
        product2.setProductLevel(ProductLevel.OFFER);
        product2.setShortName("Offer.56789");
        product2.setParentProductId("5678");
        product2.setDirect(false);

        product4.setProductKey(ProductKey.valueOf("567"));
        product4.setProductLevel(ProductLevel.OFFER);
        product4.setShortName("Offer.567");
        product4.setDirect(true);
        product4.setParentProductId("56789");

        product5.setProductKey(ProductKey.valueOf("model123"));
        product5.setProductLevel(ProductLevel.MODEL);
        product5.setDirect(true);
        product5.setParentProductId("Offer1234");

        products.add(product1);
        products.add(product2);
        products.add(product3);
        products.add(product4);
        products.add(product5);
        products.addAll(getProductList());

        return products;

    }

    private Broker getDealerGroup() {
        Broker broker = Mockito.mock(Broker.class);
        when(broker.getDealerKey()).thenReturn(BrokerKey.valueOf("1234"));
        return broker;
    }
}
