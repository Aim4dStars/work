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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class ModelPortfolioDetailIntegrationServiceTest {

    @InjectMocks
    private ModelPortfolioDetailIntegrationServiceImpl integrationServiceImpl;

    @Mock
    private AvaloqGatewayHelperService webserviceClient;

    @Mock
    private AvaloqGatewayHelperService webserviceClient2;

    @Mock
    private ModelPortfolioDetailConverter modelPortfolioConverter;

    @Mock
    private TransactionValidationConverter validationConverter;

    @Mock
    private InvestmentPolicyStatementIntegrationService ipsService;

    private ServiceErrors serviceErrors;

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
        Mockito.when(
                webserviceClient.sendToWebService(Mockito.any(IpsReq.class), Mockito.any(AvaloqOperation.class),
                        Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<ModelPortfolioDetailImpl>() {

                    @Override
                    public ModelPortfolioDetailImpl answer(InvocationOnMock invocation) throws Throwable {
                        IpsReq req = (IpsReq) invocation.getArguments()[0];
                        Assert.assertEquals("12345", req.getData().getIpsId().getVal());
                        Assert.assertEquals("TEST0001TMP", req.getData().getIpsSym().getVal());
                        return new ModelPortfolioDetailImpl();
                    }
                });
    }

    @Test
    public void whenValidateModelRequested_thenCorrectParametersArePassed() {
        ModelPortfolioDetail response = integrationServiceImpl.validateModelPortfolio(new ModelPortfolioDetailImpl(),
                serviceErrors);
        Assert.assertNotNull(response);
    }

    @Test
    public void whenSubmitModelRequested_thenCorrectParametersArePassed() {
        ModelPortfolioDetail response = integrationServiceImpl
                .submitModelPortfolio(new ModelPortfolioDetailImpl(), serviceErrors);
        Assert.assertNotNull(response);
    }

    @Test
    public void whenLoadModelRequested_thenCorrectParametersArePassed() {
        ModelPortfolioDetail response = integrationServiceImpl.loadModelPortfolio(new ModelPortfolioKey("12345"), serviceErrors);
        Assert.assertNotNull(response);
    }

    @Test
    public void whenSubmitModelSubmitted_withErrorRespone() {
        AvaloqBaseResponseImpl errRsp = Mockito.mock(AvaloqBaseResponseImpl.class);
        Mockito.when(
                webserviceClient2.sendToWebService(Mockito.any(IpsReq.class), Mockito.any(AvaloqOperation.class),
                        Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenReturn(errRsp);

        ModelPortfolioDetail response = integrationServiceImpl
                .submitModelPortfolio(new ModelPortfolioDetailImpl(), serviceErrors);
        Assert.assertNotNull(response);
    }
}
