package com.bt.nextgen.api.modelportfolio.v2.service.orderstatus;

import com.bt.nextgen.api.modelportfolio.v2.model.orderstatus.ModelOrderDetailsDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.modelportfolio.orderstatus.OrderSummaryResponseImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioDetail;
import com.bt.nextgen.service.integration.modelportfolio.orderstatus.ModelOrderDetails;
import com.bt.nextgen.service.integration.modelportfolio.orderstatus.OrderSummaryIntegrationService;
import com.bt.nextgen.service.integration.order.ExpiryMethod;
import com.bt.nextgen.service.integration.order.OrderStatus;
import com.bt.nextgen.service.integration.transactionfee.ExecutionType;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.order.OrderType;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.junit.Assert;
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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ModelOrderSummaryDtoServiceTest {

    @InjectMocks
    private ModelOrderSummaryDtoServiceImpl mpDetailDtoService;

    @Mock
    private OrderSummaryIntegrationService orderSummaryService;

    @Mock
    private AssetIntegrationService assetService;
    
    @Mock
    private InvestmentPolicyStatementIntegrationService invPolicyService;

    @Mock
    private UserProfileService userProfileService;

    private ModelOrderDetails orderDetails;
    private DateTime openDate;
    private ModelPortfolioDetail model;
    private List<ApiSearchCriteria> criteria;
    private Broker dealer;

    @Before
    public void setUp() throws Exception {

        openDate = DateTime.now();
        // Mock Dto with empty TAA and ModelOffers
        orderDetails = mock(ModelOrderDetails.class);
        when(orderDetails.getAccountName()).thenReturn("accountName");
        when(orderDetails.getAccountNumber()).thenReturn("accountNumber");
        when(orderDetails.getAdviserName()).thenReturn("adviserName");
        when(orderDetails.getAssetCode()).thenReturn("assetCode");
        when(orderDetails.getAssetId()).thenReturn("assetId");
        when(orderDetails.getAssetName()).thenReturn("assetName");
        when(orderDetails.getBrokerage()).thenReturn(BigDecimal.ZERO);
        when(orderDetails.getDealerName()).thenReturn("dealerName");
        when(orderDetails.getDocId()).thenReturn("docId");
        when(orderDetails.getEstimatedPrice()).thenReturn(BigDecimal.ONE);
        when(orderDetails.getExecType()).thenReturn(ExecutionType.DIRECT_MARKET_ACCESS);
        when(orderDetails.getExpiryDate()).thenReturn(openDate);
        when(orderDetails.getExpiryType()).thenReturn(ExpiryMethod.GFD);
        when(orderDetails.getFillQuantity()).thenReturn(BigDecimal.ONE);
        when(orderDetails.getIpsId()).thenReturn("ipsId");
        when(orderDetails.getIpsKey()).thenReturn("ipsKey");
        when(orderDetails.getIpsName()).thenReturn("modelName");
        when(orderDetails.getNetAmount()).thenReturn(BigDecimal.ONE);
        when(orderDetails.getOrderDate()).thenReturn(openDate);
        when(orderDetails.getOrderType()).thenReturn(OrderType.APPLICATION);
        when(orderDetails.getOriginalQuantity()).thenReturn(BigDecimal.ONE);
        when(orderDetails.getRemainingQuantity()).thenReturn(BigDecimal.ZERO);
        when(orderDetails.getStatus()).thenReturn(OrderStatus.MP_STEX_COMPLETE);
        when(orderDetails.getTransactionDate()).thenReturn(openDate);
        
        OrderSummaryResponseImpl response = new OrderSummaryResponseImpl();
        response.setRequestId("requestId");
        response.setOrderDetails(Collections.singletonList(orderDetails));

        when(orderSummaryService.loadOrderStatusSummary(any(BrokerKey.class), any(DateTime.class), any(ServiceErrors.class)))
                .thenReturn(response);

        // Mock Asset service
        Map<String, Asset> assetMap = new HashMap<>();
        Asset asset = mock(Asset.class);
        when(asset.getAssetId()).thenReturn("assetId");
        when(asset.getAssetCode()).thenReturn("assetCode");
        when(asset.getAssetName()).thenReturn("assetName");
        assetMap.put("assetId", asset);
        when(assetService.loadAssets(Mockito.anyList(), any(ServiceErrors.class))).thenReturn(assetMap);

        // Mock investmentPolicyService
        Map<IpsKey, ModelPortfolioDetail> result = new HashMap <> ();
        model = mock(ModelPortfolioDetail.class);
        when(model.getId()).thenReturn("id");
        when(model.getName()).thenReturn("modelName");
        result.put(IpsKey.valueOf("ipsKey"), model);
        when(invPolicyService.getModelDetails(Mockito.anyList(), any(ServiceErrors.class))).thenReturn(result);
        
        // Setup dealer access
        dealer = mock(Broker.class);
        when(dealer.getKey()).thenReturn(BrokerKey.valueOf("brokerId"));
        when(userProfileService.getInvestmentManager(any(ServiceErrors.class))).thenReturn(dealer);
        when(userProfileService.isDealerGroup()).thenReturn(Boolean.TRUE);

        criteria = new ArrayList<ApiSearchCriteria>();
        criteria.add(new ApiSearchCriteria("effective-date", SearchOperation.EQUALS, "2017-08-01", OperationType.STRING));
    }

    @Test
    public void testSearch_validResultsReturn() {
        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        List<ModelOrderDetailsDto> results = mpDetailDtoService.search(criteria, errors);

        Assert.assertTrue(results.size() == 1);
        ModelOrderDetailsDto dto = results.get(0);
        Assert.assertEquals(orderDetails.getAccountName(), dto.getAccountName());
        Assert.assertEquals(orderDetails.getAccountNumber(), dto.getAccountNumber());
        Assert.assertEquals(orderDetails.getAdviserName(), dto.getAdviserName());
        Assert.assertEquals(orderDetails.getAssetCode(), dto.getAssetCode());
        Assert.assertEquals(orderDetails.getAssetName(), dto.getAssetName());
        Assert.assertEquals(orderDetails.getBrokerage(), dto.getBrokerage());
        Assert.assertEquals(orderDetails.getDealerName(), dto.getDealerName());
        Assert.assertEquals(orderDetails.getDocId(), dto.getDocId());
        Assert.assertEquals(orderDetails.getEstimatedPrice(), dto.getEstimatedPrice());
        Assert.assertEquals(orderDetails.getExecType().getIntlId().toUpperCase(), dto.getExecType());
        Assert.assertEquals(orderDetails.getExpiryDate(), dto.getExpiryDate());
        Assert.assertEquals(orderDetails.getExpiryType().name(), dto.getExpiryType());
        Assert.assertEquals(orderDetails.getFillQuantity(), dto.getFillQuantity());
        Assert.assertEquals(orderDetails.getIpsName(), dto.getModelName());
        Assert.assertEquals(orderDetails.getIpsId(), dto.getModelId());
        Assert.assertEquals(orderDetails.getIpsKey(), dto.getIpsKey());
        Assert.assertEquals(orderDetails.getNetAmount(), dto.getNetAmount());
        Assert.assertEquals(orderDetails.getOrderDate(), dto.getOrderDate());
        Assert.assertEquals(orderDetails.getOrderType().getDisplayName(), dto.getOrderType());
        Assert.assertEquals(orderDetails.getOriginalQuantity(), dto.getOriginalQuantity());
        Assert.assertEquals(orderDetails.getRemainingQuantity(), dto.getRemainingQuantity());
        Assert.assertEquals(orderDetails.getStatus().getDisplayName(), dto.getStatus());
        Assert.assertEquals(orderDetails.getTransactionDate(), dto.getTransactionDate());
        Assert.assertEquals(orderDetails.getIpsName(), dto.getModelName());
        Assert.assertEquals(orderDetails.getIpsId(), dto.getModelId());
    }

    @Test
    public void testSearch_NoModelOrderFound_EmptyListReturn() {
        when(orderSummaryService.loadOrderStatusSummary(any(BrokerKey.class), any(DateTime.class), any(ServiceErrors.class)))
                .thenReturn(null);

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        List<ModelOrderDetailsDto> results = mpDetailDtoService.search(criteria, errors);
        Assert.assertNotNull(results);
        Assert.assertTrue(results.size() == 0);
    }

    @Test
    public void testSearchWithNullCriteria_EmptyListReturn() {
        when(orderSummaryService.loadOrderStatusSummary(any(BrokerKey.class), any(DateTime.class), any(ServiceErrors.class)))
                .thenReturn(null);

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        List<ModelOrderDetailsDto> results = mpDetailDtoService.search(null, errors);
        Assert.assertNotNull(results);
        Assert.assertTrue(results.size() == 0);
    }

    @Test
    public void testSearch_nonDealerAccess_EmptyListReturn() {
        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        when(userProfileService.getInvestmentManager(errors)).thenReturn(null);
        List<ModelOrderDetailsDto> results = mpDetailDtoService.search(criteria, errors);
        Assert.assertNotNull(results);
        Assert.assertTrue(results.size() == 0);
    }

    @Test
    public void testSearch_futureDate_EmptyListReturn() {
        criteria.clear();
        DateTime futureDate = DateTime.now().plusDays(2);
        criteria.add(new ApiSearchCriteria("effective-date", SearchOperation.EQUALS, futureDate.toString("yyyy-MM-dd"),
                OperationType.STRING));

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        List<ModelOrderDetailsDto> results = mpDetailDtoService.search(criteria, errors);


        Assert.assertNotNull(results);
        Assert.assertTrue(results.size() == 0);
    }

    @Test
    public void testSearch_noDateCriteria_defaultDateUsed() {
        when(orderSummaryService.loadOrderStatusSummary(any(BrokerKey.class), any(DateTime.class), any(ServiceErrors.class)))
                .thenReturn(null);

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        criteria.clear();
        List<ModelOrderDetailsDto> results = mpDetailDtoService.search(criteria, errors);
        Assert.assertNotNull(results);
        Assert.assertTrue(results.size() == 0);
    }

    @Test
    public void test_orderDetailsWithNullAttributes_thenEmptyStringSet() {
        Mockito.when(orderDetails.getStatus()).thenReturn(null);
        Mockito.when(orderDetails.getExpiryType()).thenReturn(null);
        Mockito.when(orderDetails.getExecType()).thenReturn(null);

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        List<ModelOrderDetailsDto> results = mpDetailDtoService.search(criteria, errors);

        ModelOrderDetailsDto dto = results.get(0);
        Assert.assertTrue(StringUtils.isEmpty(dto.getStatus()));
        Assert.assertTrue(StringUtils.isEmpty(dto.getExpiryType()));
        Assert.assertTrue(StringUtils.isEmpty(dto.getExecType()));

    }

    @Test
    public void test_nullResponseFromSearch_EmptyListreturn() {
        when(orderSummaryService.loadOrderStatusSummary(any(BrokerKey.class), any(DateTime.class), any(ServiceErrors.class)))
                .thenReturn(null);

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        List<ModelOrderDetailsDto> results = mpDetailDtoService.search(criteria, errors);
        Assert.assertNotNull(results);
        Assert.assertTrue(results.size() == 0);

        OrderSummaryResponseImpl response = new OrderSummaryResponseImpl();
        response.setRequestId("requestId");
        response.setOrderDetails(null);
        when(orderSummaryService.loadOrderStatusSummary(any(BrokerKey.class), any(DateTime.class), any(ServiceErrors.class)))
                .thenReturn(response);
        results = mpDetailDtoService.search(criteria, errors);
        Assert.assertNotNull(results);
        Assert.assertTrue(results.size() == 0);
    }

    @Test
    public void testSearch_emptyAssetMapReturn_nullAssetNameInDto() {
        Map<String, Asset> assetMap = new HashMap<>();
        when(assetService.loadAssets(Mockito.anyList(), any(ServiceErrors.class))).thenReturn(assetMap);

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        List<ModelOrderDetailsDto> results = mpDetailDtoService.search(criteria, errors);

        Assert.assertTrue(results.size() == 1);
        ModelOrderDetailsDto dto = results.get(0);

        // Asset-code is based on the modelDetails.
        Assert.assertNotNull(dto.getAssetCode());

        // Asset name is null because no corresponding asset can be retrieved.
        Assert.assertNull(dto.getAssetName());
    }
}
