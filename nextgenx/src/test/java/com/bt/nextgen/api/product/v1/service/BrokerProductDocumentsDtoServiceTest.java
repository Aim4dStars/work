package com.bt.nextgen.api.product.v1.service;

import com.bt.nextgen.api.product.v1.model.BrokerProductDocumentDto;
import com.bt.nextgen.api.product.v1.model.ProductDto;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.bt.nextgen.service.avaloq.product.ProductLevel;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userprofile.JobProfileImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BrokerProductDocumentsDtoServiceTest {

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private AssetIntegrationService assetIntegrationService;

    @InjectMocks
    private BrokerProductDocumentDtoServiceImpl brokerProductDocumentDtoServiceImpl;

    @Mock
    private ProductDtoConverter productDtoConverter;

    private ServiceErrors serviceErrors;
    private List<Asset> assetList;
    private List<Product> productList;
    private List<Product> productDetailList;
    private ProductDto productDto;

    @Before
    public void setup() throws Exception {
        serviceErrors = new ServiceErrorsImpl();
        final Broker dealerGroup = getDealerGroup();
        assetList = getAssetList();
        productList = getProductList();
        productDetailList = getProductDetailList();

        final JobProfileImpl jobProfile = new JobProfileImpl();
        jobProfile.setJobRole(JobRole.ADVISER);
        final UserProfile userProfile = new UserProfileAdapterImpl(null, jobProfile);
        Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(dealerGroup);
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(userProfile);

        productDto = mock(ProductDto.class);
        when(productDto.getKey()).thenReturn(new com.bt.nextgen.api.product.v1.model.ProductKey(EncodedString.fromPlainText("product1").toString()));
        when(productDto.getProductName()).thenReturn("White Label 060f52dc6d17421eaf1632ac9efae210");
        when(productDtoConverter.convert(Mockito.any(Product.class))).thenReturn(productDto);
    }

    @Test
    public void testProductDocumentsNoDealerKey() {
        Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(null);
        final List<BrokerProductDocumentDto> productDocumentDtoList = brokerProductDocumentDtoServiceImpl.findAll(serviceErrors);
        assertEquals(productDocumentDtoList.size(), 0);
    }

    @Test
    public void testProductDocumentsSuccess() {
        Mockito.when(assetIntegrationService.loadAvailableAssets(Mockito.any(BrokerKey.class), Mockito.any(ProductKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(assetList);
        Mockito.when(productIntegrationService.getDealerGroupProductList(Mockito.any(BrokerKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(productList);
        Mockito.when(productIntegrationService.loadProducts(Mockito.any(ServiceErrors.class))).thenReturn(productDetailList);
        final List<BrokerProductDocumentDto> productDocumentDtoList = brokerProductDocumentDtoServiceImpl.findAll(serviceErrors);
        final BrokerProductDocumentDto productDocumentDto = productDocumentDtoList.get(1);
        assertNotNull(productDocumentDto.getProductList());
        assertNotNull(productDocumentDto.getBrandList());
        assertEquals(productDocumentDto.getProductList().size(), 2);
        assertEquals(productDocumentDto.getProductList().get(0).getKey(), productDto.getKey());
        assertEquals(productDocumentDto.getProductList().get(0).getProductName(), productDto.getProductName());
        assertEquals(productDocumentDto.getBrandList().size(), 2);
    }

    @Test
    public void testProductDocumentsNoAssets() {
        Mockito.when(assetIntegrationService.loadAvailableAssets(Mockito.any(BrokerKey.class), Mockito.any(ProductKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(null);
        Mockito.when(productIntegrationService.getDealerGroupProductList(Mockito.any(BrokerKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(productList);
        Mockito.when(productIntegrationService.loadProducts(Mockito.any(ServiceErrors.class))).thenReturn(productDetailList);
        final List<BrokerProductDocumentDto> productDocumentDtoList = brokerProductDocumentDtoServiceImpl.findAll(serviceErrors);
        final BrokerProductDocumentDto productDocumentDto = productDocumentDtoList.get(1);
        assertNotNull(productDocumentDto.getProductList());
        assertNotNull(productDocumentDto.getBrandList());
        assertEquals(productDocumentDto.getProductList().size(), 2);
        assertEquals(productDocumentDto.getProductList().get(0).getKey(), productDto.getKey());
        assertEquals(productDocumentDto.getProductList().get(0).getProductName(), productDto.getProductName());
        assertEquals(productDocumentDto.getBrandList().size(), 0);
    }

    @Test
    public void testProductDocumentsNoProducts() {
        Mockito.when(assetIntegrationService.loadAvailableAssets(Mockito.any(BrokerKey.class), Mockito.any(ProductKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(assetList);
        Mockito.when(productIntegrationService.getDealerGroupProductList(Mockito.any(BrokerKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(null);
        Mockito.when(productIntegrationService.loadProducts(Mockito.any(ServiceErrors.class))).thenReturn(productDetailList);
        final List<BrokerProductDocumentDto> productDocumentDtoList = brokerProductDocumentDtoServiceImpl.findAll(serviceErrors);
        assertNotNull(productDocumentDtoList);
        assertEquals(productDocumentDtoList.size(), 0);
    }

    @Test
    public void testProductDocumentsNoProductDetails() {
        Mockito.when(productIntegrationService.getDealerGroupProductList(Mockito.any(BrokerKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(productList);
        Mockito.when(productIntegrationService.loadProducts(Mockito.any(ServiceErrors.class))).thenReturn(null);
        final List<BrokerProductDocumentDto> productDocumentDtoList = brokerProductDocumentDtoServiceImpl.findAll(serviceErrors);
        final BrokerProductDocumentDto productDocumentDto = productDocumentDtoList.get(1);
        assertNotNull(productDocumentDtoList);
        assertEquals(productDocumentDtoList.size(), 2);
        assertEquals(productDocumentDto.getProductList().size(), 2);
        assertEquals(productDocumentDto.getProductList().get(0).getKey(), productDto.getKey());
        assertEquals(productDocumentDto.getProductList().get(0).getProductName(), productDto.getProductName());
        assertEquals(productDocumentDto.getBrandList().size(), 0);
    }

    @Test
    public void testIsManagedFundAvailable() {
        Mockito.when(assetIntegrationService.loadAvailableAssets(Mockito.any(BrokerKey.class), Mockito.any(ProductKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(assetList);
        Mockito.when(productIntegrationService.getDealerGroupProductList(Mockito.any(BrokerKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(productList);
        Mockito.when(productIntegrationService.loadProducts(Mockito.any(ServiceErrors.class))).thenReturn(productDetailList);
        final List<BrokerProductDocumentDto> productDocumentDtoList = brokerProductDocumentDtoServiceImpl.findAll(serviceErrors);
        final BrokerProductDocumentDto productDocumentDto = productDocumentDtoList.get(1);
        assertNotNull(productDocumentDto.getProductList());
        assertNotNull(productDocumentDto.getBrandList());
        assertEquals(productDocumentDto.getProductList().size(), 2);
        assertEquals(productDocumentDto.getProductList().get(0).getKey(), productDto.getKey());
        assertEquals(productDocumentDto.getProductList().get(0).getProductName(), productDto.getProductName());
        assertEquals(productDocumentDto.getBrandList().size(), 2);
        assertTrue(productDocumentDto.isManagedFundAvailable());
    }

    @Test
    public void testProductDocumentsWithOneWhiteLabelOnly() {
        Mockito.when(assetIntegrationService.loadAvailableAssets(Mockito.any(BrokerKey.class), Mockito.any(ProductKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(assetList);
        Mockito.when(productIntegrationService.getDealerGroupProductList(Mockito.any(BrokerKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(Collections.singletonList(productList.get(1)));
        Mockito.when(productIntegrationService.loadProducts(Mockito.any(ServiceErrors.class))).thenReturn(productDetailList);
        final List<BrokerProductDocumentDto> productDocumentDtoList = brokerProductDocumentDtoServiceImpl.findAll(serviceErrors);
        final BrokerProductDocumentDto productDocumentDto = productDocumentDtoList.get(0);
        assertNotNull(productDocumentDto.getProductList());
        assertNotNull(productDocumentDto.getBrandList());
        assertEquals(productDocumentDto.getProductList().size(), 1);
        assertEquals(productDocumentDto.getProductList().get(0).getKey(), productDto.getKey());
        assertEquals(productDocumentDto.getProductList().get(0).getProductName(), productDto.getProductName());
        assertEquals(productDocumentDto.getBrandList().size(), 0);
    }


    private List<Asset> getAssetList() {
        final List<Asset> assets = new ArrayList<>();
        final AssetImpl asset1 = new AssetImpl();
        asset1.setBrand("80000052");
        asset1.setAssetType(AssetType.TERM_DEPOSIT);

        final AssetImpl asset2 = new AssetImpl();
        asset2.setBrand("80000053");
        asset2.setAssetType(AssetType.TERM_DEPOSIT);

        final AssetImpl asset3 = new AssetImpl();
        asset3.setBrand("80000059");
        asset3.setAssetType(AssetType.MANAGED_PORTFOLIO);

        final AssetImpl asset4 = new AssetImpl();
        asset3.setBrand("80000060");
        asset3.setAssetType(AssetType.MANAGED_FUND);

        assets.add(asset1);
        assets.add(asset2);
        assets.add(asset3);
        assets.add(asset4);

        return assets;
    }

    private List<Product> getProductList() {
        final List<Product> products = new ArrayList<>();

        final ProductImpl product1 = new ProductImpl();
        final ProductImpl product2 = new ProductImpl();
        final ProductImpl product3 = new ProductImpl();

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
        product3.setParentProductId("1234");

        products.add(product1);
        products.add(product2);
        products.add(product3);

        return products;

    }

    private List<Product> getProductDetailList() {
        final List<Product> products = new ArrayList<>();

        final ProductImpl product1 = new ProductImpl();
        final ProductImpl product2 = new ProductImpl();
        final ProductImpl product3 = new ProductImpl();
        final ProductImpl product4 = new ProductImpl();
        final ProductImpl product5 = new ProductImpl();

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
        final Broker broker = Mockito.mock(Broker.class);
        when(broker.getDealerKey()).thenReturn(BrokerKey.valueOf("1234"));
        return broker;
    }
}
