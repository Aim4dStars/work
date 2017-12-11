package com.bt.nextgen.api.modelportfolio.v2.service.defaultparams;

import com.bt.nextgen.api.modelportfolio.v2.model.detail.ModelOfferDto;
import com.bt.nextgen.api.product.v1.service.ProductDtoService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.product.ProductDetailImpl;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TbrTailoredPortfolioOfferDtoServiceTest {

    @InjectMocks
    private TailoredPortfolioOfferDtoServiceImpl offerService;

    @Mock
    private ProductDtoService pdtDtoService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Before
    public void setup() {
        Broker dealerBroker = mock(Broker.class);
        when(dealerBroker.getDealerKey()).thenReturn(BrokerKey.valueOf("dealerId"));
        when(dealerBroker.getKey()).thenReturn(BrokerKey.valueOf("dealerId"));
        when(userProfileService.getInvestmentManager(any(ServiceErrors.class))).thenReturn(dealerBroker);
    }

    @Test
    public void test_searchOfferList_forInvestment() {
        Broker dealerBroker = mock(Broker.class);
        when(dealerBroker.getDealerKey()).thenReturn(BrokerKey.valueOf("brokerId"));
        when(brokerIntegrationService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(dealerBroker);

        ProductDetailImpl pdt = mock(ProductDetailImpl.class);
        when(pdt.isActive()).thenReturn(Boolean.TRUE);
        when(pdt.isTailorMadeProduct()).thenReturn(Boolean.TRUE);
        when(pdt.isSuper()).thenReturn(Boolean.FALSE);
        when(pdt.getProductKey()).thenReturn(ProductKey.valueOf("productId"));
        when(pdt.getProductName()).thenReturn("productName");
        when(productIntegrationService.getDealerGroupProductList(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(
                Collections.singletonList((Product) pdt));

        List<ApiSearchCriteria> critiera = new ArrayList<>();
        ApiSearchCriteria modelCriteria = new ApiSearchCriteria("modelType", ApiSearchCriteria.SearchOperation.EQUALS,
                ModelType.INVESTMENT.getCode(), ApiSearchCriteria.OperationType.STRING);
        critiera.add(modelCriteria);

        ApiSearchCriteria brokerCriteria = new ApiSearchCriteria("dealerId", ApiSearchCriteria.SearchOperation.EQUALS,
                "brokerId", ApiSearchCriteria.OperationType.STRING);
        critiera.add(brokerCriteria);

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        List<ModelOfferDto> dtoList = offerService.search(critiera, errors);
        Assert.assertTrue(!dtoList.isEmpty());
        Assert.assertEquals(pdt.getProductKey().getId(), dtoList.get(0).getOfferId());
        Assert.assertEquals(pdt.getProductName(), dtoList.get(0).getOfferName());
    }

    @Test
    public void test_searchOfferList_forInvestmentWithEmptyProduct() {
        Broker dealerBroker = mock(Broker.class);
        when(dealerBroker.getDealerKey()).thenReturn(BrokerKey.valueOf("brokerId"));
        when(brokerIntegrationService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(dealerBroker);

        Product pdt = mock(Product.class);
        when(pdt.isActive()).thenReturn(Boolean.TRUE);
        when(pdt.isSuper()).thenReturn(Boolean.FALSE);
        when(pdt.isTailorMadeProduct()).thenReturn(Boolean.FALSE);
        when(pdt.getProductKey()).thenReturn(ProductKey.valueOf("productId"));
        when(pdt.getProductName()).thenReturn("productName");
        when(productIntegrationService.getDealerGroupProductList(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(
                Collections.singletonList((Product) pdt));

        List<ApiSearchCriteria> critiera = new ArrayList<>();
        ApiSearchCriteria modelCriteria = new ApiSearchCriteria("modelType", ApiSearchCriteria.SearchOperation.EQUALS,
                ModelType.INVESTMENT.getCode(), ApiSearchCriteria.OperationType.STRING);
        critiera.add(modelCriteria);

        ApiSearchCriteria brokerCriteria = new ApiSearchCriteria("dealerId", ApiSearchCriteria.SearchOperation.EQUALS,
                "brokerId", ApiSearchCriteria.OperationType.STRING);
        critiera.add(brokerCriteria);

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        List<ModelOfferDto> dtoList = offerService.search(critiera, errors);
        Assert.assertTrue(dtoList.isEmpty());
    }

    @Test
    public void test_searchOfferList_forSuper() {
        Broker dealerBroker = mock(Broker.class);
        when(dealerBroker.getDealerKey()).thenReturn(BrokerKey.valueOf("brokerId"));
        when(brokerIntegrationService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(dealerBroker);

        ProductDetailImpl pdt = mock(ProductDetailImpl.class);
        when(pdt.isActive()).thenReturn(Boolean.TRUE);
        when(pdt.isSuper()).thenReturn(Boolean.FALSE);
        when(pdt.getProductKey()).thenReturn(ProductKey.valueOf("productId"));
        when(pdt.getProductName()).thenReturn("productName");
        when(productIntegrationService.getDealerGroupProductList(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(
                Collections.singletonList((Product) pdt));

        List<ApiSearchCriteria> critiera = new ArrayList<>();
        ApiSearchCriteria modelCriteria = new ApiSearchCriteria("modelType", ApiSearchCriteria.SearchOperation.EQUALS,
                ModelType.SUPERANNUATION.getCode(), ApiSearchCriteria.OperationType.STRING);
        critiera.add(modelCriteria);

        ApiSearchCriteria brokerCriteria = new ApiSearchCriteria("dealerId", ApiSearchCriteria.SearchOperation.EQUALS,
                "brokerId", ApiSearchCriteria.OperationType.STRING);
        critiera.add(brokerCriteria);

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        List<ModelOfferDto> dtoList = offerService.search(critiera, errors);
        Assert.assertTrue(dtoList.isEmpty());

        when(pdt.isActive()).thenReturn(Boolean.FALSE);
        when(pdt.isSuper()).thenReturn(Boolean.TRUE);
        when(productIntegrationService.getDealerGroupProductList(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(
                Collections.singletonList((Product) pdt));
        dtoList = offerService.search(critiera, errors);
        Assert.assertTrue(dtoList.isEmpty());

        when(pdt.isTailorMadeProduct()).thenReturn(Boolean.TRUE);
        when(productIntegrationService.getDealerGroupProductList(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(
                Collections.singletonList((Product) pdt));
        dtoList = offerService.search(critiera, errors);
        Assert.assertTrue(dtoList.isEmpty());

        when(pdt.isActive()).thenReturn(Boolean.TRUE);
        when(pdt.isTailorMadeProduct()).thenReturn(Boolean.TRUE);
        when(productIntegrationService.getDealerGroupProductList(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(
                Collections.singletonList((Product) pdt));
        dtoList = offerService.search(critiera, errors);
        Assert.assertEquals(pdt.getProductKey().getId(), dtoList.get(0).getOfferId());
        Assert.assertEquals(pdt.getProductName(), dtoList.get(0).getOfferName());
    }

    @Test
    public void test_getModelOffers() {
        Broker dealerBroker = mock(Broker.class);
        when(dealerBroker.getDealerKey()).thenReturn(BrokerKey.valueOf("brokerId"));
        when(brokerIntegrationService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(dealerBroker);

        ProductDetailImpl pdt = mock(ProductDetailImpl.class);
        when(pdt.isActive()).thenReturn(Boolean.TRUE);
        when(pdt.isSuper()).thenReturn(Boolean.TRUE);
        when(pdt.getProductKey()).thenReturn(ProductKey.valueOf("productId"));
        when(pdt.getProductName()).thenReturn("productName");
        when(pdt.isTailorMadeProduct()).thenReturn(Boolean.TRUE);
        when(productIntegrationService.getDealerGroupProductList(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(
                Collections.singletonList((Product) pdt));

        List<ModelOfferDto> dtoList = offerService.getModelOffers(dealerBroker.getDealerKey(), ModelType.INVESTMENT,
                new ServiceErrorsImpl());
        Assert.assertTrue(dtoList.isEmpty());
        dtoList = offerService.getModelOffers(dealerBroker.getDealerKey(), ModelType.SUPERANNUATION, new ServiceErrorsImpl());
        Assert.assertTrue(dtoList.size() == 1);
        dtoList = offerService.getModelOffers(dealerBroker.getDealerKey(), null, new ServiceErrorsImpl());
        Assert.assertTrue(dtoList.size() == 0);

        when(pdt.isSuper()).thenReturn(Boolean.FALSE);
        dtoList = offerService.getModelOffers(dealerBroker.getDealerKey(), ModelType.INVESTMENT, new ServiceErrorsImpl());
        Assert.assertTrue(dtoList.size() == 1);
        dtoList = offerService.getModelOffers(dealerBroker.getDealerKey(), ModelType.SUPERANNUATION, new ServiceErrorsImpl());
        Assert.assertTrue(dtoList.size() == 0);
        dtoList = offerService.getModelOffers(dealerBroker.getDealerKey(), null, new ServiceErrorsImpl());
        Assert.assertTrue(dtoList.size() == 0);
    }
}
