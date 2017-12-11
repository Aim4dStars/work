package com.bt.nextgen.api.product.service;

import com.bt.nextgen.api.adviser.model.AdviserSearchDto;
import com.bt.nextgen.api.adviser.service.AdviserSearchDtoService;
import com.bt.nextgen.api.product.model.ProductDto;
import com.bt.nextgen.core.api.exception.NotAllowedException;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.avaloq.product.ProductDetailImpl;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.bt.nextgen.service.avaloq.product.ProductLevel;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.integration.broker.BrokerType;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductFeeComponent;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductDtoServiceImplTest {

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @InjectMocks
    ProductDtoServiceImpl productDtoService;

    @Mock
    private ProductDtoConverter productDtoConverter;

    @Mock
    private AdviserSearchDtoService adviserSearchDtoService;

    @Test
    public void searchShouldReturnProductsForAValidAdviserPositionId() throws Exception {
        String positionId = "abc";
        String dealerGroupId = "dealer";
        String productId = "68445";

        BrokerKey key = BrokerKey.valueOf(positionId);
        BrokerKey dealerKey = BrokerKey.valueOf(dealerGroupId);
        BrokerImpl broker = new BrokerImpl(key, BrokerType.ADVISER);
        broker.setDealerKey(dealerKey);

        AdviserSearchDto adviserDto = mock(AdviserSearchDto.class);
        when(adviserDto.getAdviserPositionId()).thenReturn(EncodedString.fromPlainText(positionId).toString());
        Mockito.when(adviserSearchDtoService.search(any(List.class), any(ServiceErrors.class))).thenReturn(Arrays.asList(adviserDto));

        Product product = new ProductImpl();
        product.setProductKey(ProductKey.valueOf(productId));
        ((ProductImpl) product).setFeeComponents(new ArrayList<ProductFeeComponent>());
        when(brokerIntegrationService.getBroker(key, null)).thenReturn(broker);
        when(productIntegrationService.getDealerGroupProductList(BrokerKey.valueOf(dealerGroupId), null)).thenReturn(Arrays.asList(product));
        List<ApiSearchCriteria> searchCriteriaList = new ArrayList<>();
        ApiSearchCriteria criteria = new ApiSearchCriteria("positionId", ApiSearchCriteria.SearchOperation.EQUALS, EncodedString.fromPlainText(positionId).toString(), ApiSearchCriteria.OperationType.STRING);
        searchCriteriaList.add(criteria);

        ProductDto dto = new ProductDto();
        dto.setKey(new com.bt.nextgen.api.product.model.ProductKey(EncodedString.fromPlainText(productId).toString()));
        dto.setProductName("White Label 060f52dc6d17421eaf1632ac9efae210");
        Mockito.when(productDtoConverter.convert(product)).thenReturn(dto);

        List<ProductDto> products = productDtoService.search(searchCriteriaList, null);
        assertNotNull(products);
        assertThat(EncodedString.toPlainText(products.get(0).getKey().getProductId()), is(productId));
    }


    @Test(expected= NotAllowedException.class)
    public void testSearchShouldThrowAnException_WhenAdviserIsNotPresentInThePermittedAdviserList(){
        String positionId = "abc";
        AdviserSearchDto adviserDto = mock(AdviserSearchDto.class);
        when(adviserDto.getAdviserPositionId()).thenReturn(EncodedString.fromPlainText("SOME_ADVISER_ID").toString());
        Mockito.when(adviserSearchDtoService.search(any(List.class), any(ServiceErrors.class))).thenReturn(Arrays.asList(adviserDto));
        ApiSearchCriteria criteria = new ApiSearchCriteria("positionId", ApiSearchCriteria.SearchOperation.EQUALS, EncodedString.fromPlainText(positionId).toString(), ApiSearchCriteria.OperationType.STRING);
        productDtoService.search(Arrays.asList(criteria), null);
    }


    //@Test
    public void toProductDtoTest() throws Exception {
        String productId = "65365";
        ProductDetailImpl product = new ProductDetailImpl();
        product.setProductKey(ProductKey.valueOf(productId));
        product.setProductType("White label");
        product.setActive(true);
        product.setProductName("White Label 1 35d1b65704184ae3b87799400f7ab93c");
        product.setParentProduct("BT IDPS");
        product.setShortName("PROD.WL.1");
        product.setMinIntialInvestment(new BigDecimal("50000.0"));
        product.setMinContribution(new BigDecimal("1250.0"));
        product.setMinWithdrwal(new BigDecimal("1250.0"));
        product.setCpcCode("35D1B65704184AE3B87799400F7AB93C");
        product.setProductLevel(ProductLevel.WHITE_LABEL);
    }


}