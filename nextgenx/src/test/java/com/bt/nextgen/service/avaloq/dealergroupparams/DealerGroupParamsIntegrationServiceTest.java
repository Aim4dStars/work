package com.bt.nextgen.service.avaloq.dealergroupparams;

import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.DealerParameterKey;
import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.bt.nextgen.service.request.AvaloqRequest;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DealerGroupParamsIntegrationServiceTest {

    @InjectMocks
    private DealerGroupParamsIntegrationServiceImpl paramService;

    @Mock
    private AvaloqReportService avaloqService;

    private DealerGroupParams dgParam;

    @Before
    public void setup() {
        dgParam = mock(DealerGroupParams.class);
        when(dgParam.getMinimumInitialInvestmentAmount()).thenReturn(BigDecimal.ONE);
        when(dgParam.getMinimumCashAllocationPercentage()).thenReturn(BigDecimal.ONE);
        when(dgParam.getMinimumTradePercentageScan()).thenReturn(BigDecimal.ONE);
        when(dgParam.getMinimumTradeAmountScan()).thenReturn(BigDecimal.ONE);
        when(dgParam.getMinimumTradePercentage()).thenReturn(BigDecimal.ONE);
        when(dgParam.getMinimumTradeAmount()).thenReturn(BigDecimal.ONE);
        when(dgParam.getToleranceAbsolutePercentage()).thenReturn(BigDecimal.ONE);
        when(dgParam.getToleranceRelativePercentage()).thenReturn(BigDecimal.ONE);
        when(dgParam.getPPDefaultAssetTolerance()).thenReturn(BigDecimal.ONE);
        when(dgParam.getPPMinimumInvestmentAmount()).thenReturn(BigDecimal.ONE);
        when(dgParam.getPPMinimumTradeAmount()).thenReturn(BigDecimal.ONE);
        when(dgParam.getIsTmpProduct()).thenReturn(Boolean.TRUE);
        when(dgParam.getIsSuperProduct()).thenReturn(Boolean.FALSE);
    }

    @Test
    public void testLoadCustomerAccountObjects_whenNoErrors_thenObjectListReturned() {
        DealerGroupParamsResponseImpl response = new DealerGroupParamsResponseImpl();
        response.setCustomerAccountObjects(Collections.singletonList(dgParam));

        BrokerKey brokerKey = mock(BrokerKey.class);
        when(brokerKey.getId()).thenReturn("dealerId");

        DealerParameterKey paramKey = mock(DealerParameterKey.class);
        when(paramKey.getAccountType()).thenReturn(ModelType.INVESTMENT.getCode());
        when(paramKey.getBrokerKey()).thenReturn(brokerKey);

        when(avaloqService.executeReportRequestToDomain(any(AvaloqRequest.class), any(Class.class), any(ServiceErrors.class)))
                .thenReturn(response);

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        List<DealerGroupParams> resultList = paramService.loadCustomerAccountObjects(paramKey, errors);
        Assert.assertNotNull(resultList);
        Assert.assertEquals(1, resultList.size());
    }

    @Test
    public void testLoadCustomerAccountObjects_whenNullResponse_thenServiceError() {
        BrokerKey brokerKey = mock(BrokerKey.class);
        when(brokerKey.getId()).thenReturn("dealerId");

        DealerParameterKey paramKey = mock(DealerParameterKey.class);
        when(paramKey.getAccountType()).thenReturn(ModelType.INVESTMENT.getCode());
        when(paramKey.getBrokerKey()).thenReturn(brokerKey);

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        List<DealerGroupParams> resultList = paramService.loadCustomerAccountObjects(paramKey, errors);
        Assert.assertEquals(0, resultList.size());
        Assert.assertEquals(1, errors.getErrors().size());
    }

    @Test
    public void testLoadCustomerAccountObjects_whenNoDefaultsReturned_thenServiceError() {
        DealerGroupParamsResponseImpl response = new DealerGroupParamsResponseImpl();

        BrokerKey brokerKey = mock(BrokerKey.class);
        when(brokerKey.getId()).thenReturn("dealerId");

        DealerParameterKey paramKey = mock(DealerParameterKey.class);
        when(paramKey.getAccountType()).thenReturn(ModelType.INVESTMENT.getCode());
        when(paramKey.getBrokerKey()).thenReturn(brokerKey);

        when(avaloqService.executeReportRequestToDomain(any(AvaloqRequest.class), any(Class.class), any(ServiceErrors.class)))
                .thenReturn(response);

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        List<DealerGroupParams> resultList = paramService.loadCustomerAccountObjects(paramKey, errors);
        Assert.assertNotNull(resultList);
        Assert.assertEquals(0, resultList.size());
        Assert.assertEquals(1, errors.getErrors().size());
    }
}