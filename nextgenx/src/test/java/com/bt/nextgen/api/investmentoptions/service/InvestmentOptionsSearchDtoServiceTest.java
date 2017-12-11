package com.bt.nextgen.api.investmentoptions.service;

import com.bt.nextgen.api.investmentoptions.model.InvestmentOptionsDto;
import com.bt.nextgen.api.investmentoptions.util.InvestmentOptionsDtoServiceHelper;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.avaloq.product.ProductLevel;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.integration.broker.BrokerType;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementInterface;
import com.bt.nextgen.service.integration.ips.IpsIdentifier;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.ips.IpsProductAssociationInterface;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductKey;
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

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InvestmentOptionsSearchDtoServiceTest {
    @InjectMocks
    InvestmentOptionsSearchDtoServiceImpl dtoService;

    @Mock
    private InvestmentPolicyStatementIntegrationService ipsService;

    @Mock
    private InvestmentOptionsDtoServiceHelper helper;

    private ServiceErrors serviceErrors;
    private List<ApiSearchCriteria> criteriaList;
    private Map<IpsKey, IpsProductAssociationInterface> ipsListResponse;
    private InvestmentPolicyStatementInterface ipsDetailResponse;

    @Before
    public void setup() {
        criteriaList = new ArrayList<>();
        ipsListResponse = getIpsList();
        serviceErrors = new ServiceErrorsImpl();

        Map<ProductKey, Product> modelProductsMap = new HashMap<>();
        modelProductsMap.put(ProductKey.valueOf("111"), getProduct("model 1", "111", "123", ProductLevel.MODEL));
        modelProductsMap.put(ProductKey.valueOf("333"), getProduct("model 3", "333", "123", ProductLevel.MODEL));
        modelProductsMap.put(ProductKey.valueOf("555"), getProduct("model 5", "555", "123", ProductLevel.MODEL));

        when(helper.getModelProducts(any(ApiSearchCriteria.class), any(ServiceErrors.class))).thenReturn(modelProductsMap);
        when(helper.getStaticValue(any(CodeCategory.class), anyString(), any(ServiceErrors.class))).thenReturn("code1");

    }

    @Test
    public void testFindAllSuccess() {
        ipsDetailResponse = getIpsDetail("ipsid1", "apir1", "class1", "style1", BigDecimal.ONE, "1234", "code1");
        when(ipsService.getAsscociatedProductAndIpsIds(any(ServiceErrors.class))).thenReturn(ipsListResponse);
        when(ipsService.getIPSDetail(any(IpsIdentifier.class), any(ServiceErrors.class))).thenReturn(ipsDetailResponse);
        List<InvestmentOptionsDto> result = dtoService.search(criteriaList, serviceErrors);
        assertEquals(result.size(), 3);
        assertEquals(result.get(0).getCode(), "apir1");
        assertEquals(result.get(0).getAssetClass(), "code1");
        assertEquals(result.get(0).getStyle(), "code1");
        assertEquals(result.get(0).getMinAmount(), BigDecimal.ONE);
        assertEquals(result.get(0).getName(), "model 5");
        assertEquals(result.get(0).getAssetCode(), "code1");
    }

    private Broker getBroker(String brokerId) {
        return new BrokerImpl(BrokerKey.valueOf(brokerId), BrokerType.DEALER);
    }

    @Test
    public void testFindAllSuccessNoIMDetails() {
        ipsDetailResponse = getIpsDetail("ips1", "apir1", "class1", "style1", BigDecimal.ONE, "1234", "code1");
        when(ipsService.getAsscociatedProductAndIpsIds(any(ServiceErrors.class))).thenReturn(ipsListResponse);
        when(ipsService.getIPSDetail(any(IpsIdentifier.class), any(ServiceErrors.class))).thenReturn(ipsDetailResponse);

        List<InvestmentOptionsDto> result = dtoService.search(criteriaList, serviceErrors);
        assertEquals(result.size(), 3);
        assertEquals(result.get(0).getCode(), "apir1");
        assertEquals(result.get(0).getAssetClass(), "code1");
        assertEquals(result.get(0).getStyle(), "code1");
        assertEquals(result.get(0).getMinAmount(), BigDecimal.ONE);
        assertEquals(result.get(0).getName(), "model 5");
        assertNull(result.get(0).getInvestmentManagerId());
        assertEquals(result.get(0).getAssetCode(), "code1");
    }

    @Test
    public void testFindNoProduct() {
        ipsDetailResponse = getIpsDetail("ips1", "apir1", "class1", "style1", BigDecimal.ONE, "1234", "code1");
        when(ipsService.getAsscociatedProductAndIpsIds(any(ServiceErrors.class))).thenReturn(ipsListResponse);
        when(helper.getModelProducts(any(ApiSearchCriteria.class), any(ServiceErrors.class))).thenReturn(null);
        List<InvestmentOptionsDto> result = dtoService.search(criteriaList, serviceErrors);
        assertEquals(result.size(), 0);
    }

    @Test
    public void testFindNoIpsDetailAndNoStaticCodes() {
        when(ipsService.getAsscociatedProductAndIpsIds(any(ServiceErrors.class))).thenReturn(ipsListResponse);
        when(ipsService.getIPSDetail(any(IpsIdentifier.class), any(ServiceErrors.class))).thenReturn(null);
        when(helper.getStaticValue(any(CodeCategory.class), any(String.class), any(ServiceErrors.class))).thenReturn(null);
        List<InvestmentOptionsDto> result = dtoService.search(criteriaList, serviceErrors);
        assertEquals(result.size(), 0);
    }

    @Test
    public void testFilterNameResultsSuccess() {
        ipsDetailResponse = getIpsDetail("ipsid1", "apir1", "class1", "style1", BigDecimal.ONE, "1234", "code1");
        criteriaList.add(new ApiSearchCriteria("name", SearchOperation.STARTS_WITH, "mod", OperationType.STRING));
        when(ipsService.getAsscociatedProductAndIpsIds(any(ServiceErrors.class))).thenReturn(ipsListResponse);
        when(ipsService.getIPSDetail(any(IpsIdentifier.class), any(ServiceErrors.class))).thenReturn(ipsDetailResponse);
        List<InvestmentOptionsDto> result = dtoService.search(criteriaList, serviceErrors);
        assertEquals(result.size(), 3);
        assertEquals(result.get(1).getCode(), "apir1");
        assertEquals(result.get(1).getAssetClass(), "code1");
        assertEquals(result.get(1).getStyle(), "code1");
        assertEquals(result.get(1).getMinAmount(), BigDecimal.ONE);
        assertEquals(result.get(1).getName(), "model 1");
        assertEquals(result.get(1).getAssetCode(), "code1");
    }

    @Test
    public void testFilterNameResultsFindNoneSuccess() {
        ipsDetailResponse = getIpsDetail("ips1", "apir1", "class1", "style1", BigDecimal.ONE, "1234", "code1");
        criteriaList.add(new ApiSearchCriteria("name", SearchOperation.STARTS_WITH, "other", OperationType.STRING));
        when(ipsService.getAsscociatedProductAndIpsIds(any(ServiceErrors.class))).thenReturn(ipsListResponse);
        when(ipsService.getIPSDetail(any(IpsIdentifier.class), any(ServiceErrors.class))).thenReturn(
                ipsDetailResponse);
        List<InvestmentOptionsDto> result = dtoService.search(criteriaList, serviceErrors);
        assertEquals(result.size(), 0);
    }

    @Test
    public void testFilterCodeResultsSuccess() {
        ipsDetailResponse = getIpsDetail("ips1", "apir3", "class1", "style1", BigDecimal.TEN, "1234", "code1");
        criteriaList.add(new ApiSearchCriteria("code", SearchOperation.EQUALS, "apir3", OperationType.STRING));
        when(ipsService.getIPSDetail(any(IpsIdentifier.class), any(ServiceErrors.class))).thenReturn(ipsDetailResponse);
        when(ipsService.getAsscociatedProductAndIpsIds(any(ServiceErrors.class))).thenReturn(ipsListResponse);
        when(helper.getStaticValue(any(CodeCategory.class), anyString(), any(ServiceErrors.class))).thenReturn("code3");

        List<InvestmentOptionsDto> result = dtoService.search(criteriaList, serviceErrors);
        assertEquals(result.size(), 3);
        assertEquals(result.get(0).getCode(), "apir3");
        assertEquals(result.get(0).getAssetClass(), "code3");
        assertEquals(result.get(0).getStyle(), "code3");
        assertEquals(result.get(0).getMinAmount(), BigDecimal.TEN);
        assertEquals(result.get(0).getName(), "model 5");
        assertEquals(result.get(0).getAssetCode(), "code1");
    }

    @Test
    public void testFilterCodeResultsFindNoneSuccess() {
        ipsDetailResponse = getIpsDetail("ips1", "apir1", "class1", "style1", BigDecimal.ONE, "1234", "code1");
        criteriaList.add(new ApiSearchCriteria("code", SearchOperation.STARTS_WITH, "other", OperationType.STRING));
        when(ipsService.getAsscociatedProductAndIpsIds(any(ServiceErrors.class))).thenReturn(ipsListResponse);
        when(ipsService.getIPSDetail(any(IpsIdentifier.class), any(ServiceErrors.class))).thenReturn(
                ipsDetailResponse);
        List<InvestmentOptionsDto> result = dtoService.search(criteriaList, serviceErrors);
        assertEquals(result.size(), 0);
    }

    private Map<IpsKey, IpsProductAssociationInterface> getIpsList() {
        Map<IpsKey, IpsProductAssociationInterface> list = new HashMap<>();
        list.put(IpsKey.valueOf("ipsid1"), getIpsAssociation("ipsid1", "111", "222", "333"));
        list.put(IpsKey.valueOf("ipsid2"), getIpsAssociation("ipsid2", "444"));
        list.put(IpsKey.valueOf("ipsid3"), getIpsAssociation("ipsid3", "555"));
        return list;
    }

    private Product getProduct(final String prodName, final String prodId, final String parentProdId, final ProductLevel level) {
        final Product product = mock(Product.class);
        when(product.getProductName()).thenReturn(prodName);
        when(product.getProductKey()).thenReturn(ProductKey.valueOf(prodId));
        when(product.getParentProductKey()).thenReturn(ProductKey.valueOf(parentProdId));
        when(product.getProductLevel()).thenReturn(level);
        return product;
    }

    public InvestmentPolicyStatementInterface getIpsDetail(final String ipsId, final String apirCode, final String classId,
                                                           final String styleId, final BigDecimal minAmount,
                                                           final String investmentManagerId, final String symbolCode) {
        InvestmentPolicyStatementInterface detail = mock(InvestmentPolicyStatementInterface.class);
        when(detail.getCode()).thenReturn(symbolCode);
        when(detail.getApirCode()).thenReturn(apirCode);
        when(detail.getAssetClassId()).thenReturn(classId);
        when(detail.getInvestmentStyleId()).thenReturn(styleId);
        when(detail.getMinInitInvstAmt()).thenReturn(minAmount);
        when(detail.getInvestmentManagerPersonId()).thenReturn(investmentManagerId);
        when(detail.getIpsKey()).thenReturn(IpsKey.valueOf(ipsId));
        return detail;
    }

    public IpsProductAssociationInterface getIpsAssociation(final String ipsId, final String... productIdList) {
        IpsProductAssociationInterface ipsAssoc = new IpsProductAssociationInterface() {
            @Override
            public List<ProductKey> getAsscociatedProductList() {
                List<ProductKey> keyList = new ArrayList<>();
                for (String productId : asList(productIdList)) {
                    keyList.add(ProductKey.valueOf(productId));
                }
                return keyList;
            }

            @Override
            public IpsKey getIpsKey() {
                return IpsKey.valueOf(ipsId);
            }

            @Override
            public void setIpsKey(IpsKey ipsId) {

            }
        };
        return ipsAssoc;
    }
}
