package com.bt.nextgen.service.avaloq.modelportfolio.detail;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationConverter;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioDetail;
import com.btfin.abs.trxservice.ips.v1_0.Data;
import com.btfin.abs.trxservice.ips.v1_0.IpsReq;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ModelPortfolioDetailIntegrationServiceErrorTest {

    @InjectMocks
    private ModelPortfolioDetailIntegrationServiceImpl integrationServiceImpl;

    @Mock
    private AvaloqGatewayHelperService webserviceClient;

    @Mock
    private ModelPortfolioDetailConverter modelPortfolioConverter;

    @Mock
    private TransactionValidationConverter validationConverter;

    @Mock
    private InvestmentPolicyStatementIntegrationService ipsService;

    private ServiceErrors serviceErrors;

    AvaloqBaseResponseImpl errRsp;

    @Before
    public void setup() {

        IpsReq req = new IpsReq();
        Data data = new Data();
        data.setIpsId(AvaloqGatewayUtil.createIdVal("12345"));
        data.setIpsSym(AvaloqGatewayUtil.createTextVal("TEST0001TMP"));
        req.setData(data);

        Mockito.when(modelPortfolioConverter.toValidateRequest(Mockito.any(ModelPortfolioDetail.class))).thenReturn(req);
        Mockito.when(modelPortfolioConverter.toSubmitRequest(Mockito.any(ModelPortfolioDetail.class))).thenReturn(req);
        Mockito.when(modelPortfolioConverter.toLoadRequest(Mockito.any(ModelPortfolioKey.class))).thenReturn(req);

        serviceErrors = new ServiceErrorsImpl();
        AvaloqBaseResponseImpl errRsp = Mockito.mock(AvaloqBaseResponseImpl.class);
        Mockito.when(
                webserviceClient.sendToWebService(Mockito.any(IpsReq.class), Mockito.any(AvaloqOperation.class),
                        Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenReturn(errRsp);
    }

    @Test
    public void whenValidateModelRequested_avaloqErrorResponseReturn_thenErrorsAreCaptured() {
        Assert.assertTrue(serviceErrors.isEmpty());
        ModelPortfolioDetail response = integrationServiceImpl.validateModelPortfolio(new ModelPortfolioDetailImpl(),
                serviceErrors);
        Assert.assertTrue(!serviceErrors.isEmpty());
        Assert.assertNull(response);
    }

    @Test
    public void whenSubmitModelRequested_avaloqErrorResponseReturn_thenErrorsAreCaptured() {
        ModelPortfolioDetail response = integrationServiceImpl
                .submitModelPortfolio(new ModelPortfolioDetailImpl(), serviceErrors);
        Assert.assertNull(response);
    }
}
