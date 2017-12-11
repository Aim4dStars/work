package com.bt.nextgen.api.investmentoptions.util;

import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.avaloq.product.ProductLevel;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.integration.broker.BrokerType;
import com.btfin.panorama.service.integration.broker.ExternalBrokerKey;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InvestmentOptionsDtoServiceHelperTest {

    @InjectMocks
    private InvestmentOptionsDtoServiceHelper investmentOptionsDtoServiceHelper;

    @Mock
    private ProductIntegrationService productService;

    @Mock
    private StaticIntegrationService staticService;

    @Mock
    AssetIntegrationService assetService;

    @Mock
    BrokerIntegrationService brokerService;

    @Mock
    UserProfileService profileService;

    private List<Product> productList;
    private Broker investmentManager;
    private Broker dealerGroup;
    private Code staticResponse;
    private List<Product> allProductsList;

    @Before
    public void setup() {
        dealerGroup = new BrokerImpl(BrokerKey.valueOf("broker1"), BrokerType.DEALER);
        staticResponse = getStatic("code1", "code name 1");
        investmentManager = getInvestmentManager("EQ.234");

        productList = Arrays.asList(
                getProduct("prod name 1", "123", "456", ProductLevel.OFFER),
                getProduct("prod name 2", "456", "000", ProductLevel.WHITE_LABEL),
                getProduct("prod name 3", "789", "123", ProductLevel.MODEL));

        allProductsList = Arrays.asList(
                getProduct("white label 2", "222", "456", ProductLevel.WHITE_LABEL),
                getProduct("offer 4", "444", "789", ProductLevel.OFFER),

                getProduct("model 1", "111", "123", ProductLevel.MODEL),
                getProduct("model 3", "333", "123", ProductLevel.MODEL),
                getProduct("model 5", "555", "444", ProductLevel.MODEL),
                getProduct("model 6", "666", "456", ProductLevel.MODEL));

        when(profileService.getDealerGroupBroker()).thenReturn(dealerGroup);
        when(productService.getDealerGroupProductList(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(productList);
        when(productService.loadProducts(any(ServiceErrors.class))).thenReturn(allProductsList);
    }

    @Test
    public void testGetModelProducts() {
        ApiSearchCriteria productCriteria = new ApiSearchCriteria("product-id", ApiSearchCriteria.SearchOperation.EQUALS,
                EncodedString.fromPlainText("456").toString(), ApiSearchCriteria.OperationType.STRING);
        Map<ProductKey, Product> modelProducts = investmentOptionsDtoServiceHelper.getModelProducts(productCriteria, new ServiceErrorsImpl());

        assertEquals(modelProducts.size(), 2);
        assertTrue(modelProducts.containsKey(ProductKey.valueOf("111")));
        assertTrue(modelProducts.containsKey(ProductKey.valueOf("333")));
    }

    @Test
    public void testGetStaticValue() {
        when(staticService.loadCode(any(CodeCategory.class), any(String.class), any(ServiceErrors.class))).thenReturn(staticResponse);
        String staticValue = investmentOptionsDtoServiceHelper.getStaticValue(CodeCategory.IPS_ASSET_CLASS, "code1", new ServiceErrorsImpl());
        assertEquals(staticValue, "code name 1");
    }

    
    private Product getProduct(final String prodName, final String prodId, final String parentProdId, final ProductLevel level) {
        final Product product = mock(Product.class);
        when(product.getProductName()).thenReturn(prodName);
        when(product.getProductKey()).thenReturn(ProductKey.valueOf(prodId));
        when(product.getParentProductKey()).thenReturn(ProductKey.valueOf(parentProdId));
        when(product.getProductLevel()).thenReturn(level);
        return product;
    }

    public Code getStatic(final String id, final String name) {
        final Code code = mock(Code.class);
        when(code.getCodeId()).thenReturn(id);
        when(code.getName()).thenReturn(name);
        return code;
    }

    private Broker getInvestmentManager(final String investmentManagerId) {
        Broker broker = mock(Broker.class);
        when(broker.getExternalBrokerKey()).thenReturn(ExternalBrokerKey.valueOf(investmentManagerId));
        return broker;
    }
}
