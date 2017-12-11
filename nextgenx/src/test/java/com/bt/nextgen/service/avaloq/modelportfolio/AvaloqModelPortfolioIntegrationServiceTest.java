package com.bt.nextgen.service.avaloq.modelportfolio;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationConverter;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioUpload;
import com.bt.nextgen.service.integration.transaction.TransactionResponse;
import com.bt.nextgen.service.integration.transaction.TransactionValidation;
import com.btfin.abs.trxservice.mp_cton.v1_0.MpCtonReq;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class AvaloqModelPortfolioIntegrationServiceTest {

    @InjectMocks
    private AvaloqModelPortfolioIntegrationServiceImpl modelPortfolioService;

    @Mock
    private AvaloqGatewayHelperService webserviceClient;

    @Mock
    private TransactionValidationConverter validationConverter;

    @Mock
    private ModelPortfolioUploadConverter modelUploadConverter;

    @Test
    public void testValidateModel_whenCalled_thenNewModelDataPassedToAvaloq() {
        final ModelPortfolioUploadImpl response = Mockito.mock(ModelPortfolioUploadImpl.class);
        final MpCtonReq request = Mockito.mock(MpCtonReq.class);
        Mockito.when(
                webserviceClient.sendToWebService(Mockito.any(MpCtonReq.class), Mockito.any(AvaloqOperation.class),
                        Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<ModelPortfolioUploadImpl>() {

                    @Override
                    public ModelPortfolioUploadImpl answer(InvocationOnMock invocation) throws Throwable {
                        MpCtonReq req = (MpCtonReq) invocation.getArguments()[0];
                        Assert.assertEquals(request, req);
                        return response;
                    }
                });

        Mockito.when(
                modelUploadConverter.toModelValidateRequest(Mockito.any(ModelPortfolioUpload.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(request);

        Mockito.when(
                validationConverter.toValidationError(Mockito.any(TransactionResponse.class),
                        Mockito.anyListOf(TransactionValidation.class))).thenReturn(null);

        modelPortfolioService.validateModel(Mockito.mock(ModelPortfolioUploadImpl.class), new FailFastErrorsImpl());
    }

    @Test
    public void testSubmitModel_whenCalled_thenNewModelDataPassedToAvaloq() {
        final ModelPortfolioUploadImpl response = Mockito.mock(ModelPortfolioUploadImpl.class);
        final MpCtonReq request = Mockito.mock(MpCtonReq.class);
        Mockito.when(
                webserviceClient.sendToWebService(Mockito.any(MpCtonReq.class), Mockito.any(AvaloqOperation.class),
                        Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<ModelPortfolioUploadImpl>() {

                    @Override
                    public ModelPortfolioUploadImpl answer(InvocationOnMock invocation) throws Throwable {
                        MpCtonReq req = (MpCtonReq) invocation.getArguments()[0];
                        Assert.assertEquals(request, req);
                        return response;
                    }
                });

        Mockito.when(
                modelUploadConverter.toModelUploadRequest(Mockito.any(ModelPortfolioUpload.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(request);

        Mockito.when(
                validationConverter.toValidationError(Mockito.any(TransactionResponse.class),
                        Mockito.anyListOf(TransactionValidation.class))).thenReturn(null);

        modelPortfolioService.submitModel(Mockito.mock(ModelPortfolioUploadImpl.class), new FailFastErrorsImpl());
    }

    @Test
    public void testLoadUploadedModel_whenCalled_thenParametersPassedToAvaloq() {
        final ModelPortfolioUploadImpl response = Mockito.mock(ModelPortfolioUploadImpl.class);
        final MpCtonReq request = Mockito.mock(MpCtonReq.class);
        Mockito.when(
                webserviceClient.sendToWebService(Mockito.any(MpCtonReq.class), Mockito.any(AvaloqOperation.class),
                        Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<ModelPortfolioUploadImpl>() {

                    @Override
                    public ModelPortfolioUploadImpl answer(InvocationOnMock invocation) throws Throwable {
                        MpCtonReq req = (MpCtonReq) invocation.getArguments()[0];
                        Assert.assertEquals(request, req);
                        return response;
                    }
                });

        Mockito.when(modelUploadConverter.toGetModelRequest(Mockito.anyString())).thenReturn(request);

        Mockito.when(
                validationConverter.toValidationError(Mockito.any(TransactionResponse.class),
                        Mockito.anyListOf(TransactionValidation.class))).thenReturn(null);

        modelPortfolioService.loadUploadedModel(IpsKey.valueOf("modelId"), new FailFastErrorsImpl());
    }

    @Test(expected = ServiceException.class)
    public void testValidateModel_whenAvaloqReturnsFatalError_thenExpectServiceError() {
        ModelPortfolioUploadImpl model = new ModelPortfolioUploadImpl();
        model.setErrorMessage("errorMessage");

        Mockito.when(
                webserviceClient.sendToWebService(Mockito.any(MpCtonReq.class), Mockito.any(AvaloqOperation.class),
                        Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenReturn(model);

        modelPortfolioService.validateModel(Mockito.mock(ModelPortfolioUpload.class), new FailFastErrorsImpl());
    }

    @Test(expected = ServiceException.class)
    public void testUploadModel_whenAvaloqReturnsFatalError_thenExpectServiceError() {
        ModelPortfolioUploadImpl model = new ModelPortfolioUploadImpl();
        model.setErrorMessage("errorMessage");

        Mockito.when(
                webserviceClient.sendToWebService(Mockito.any(MpCtonReq.class), Mockito.any(AvaloqOperation.class),
                        Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenReturn(model);

        modelPortfolioService.submitModel(Mockito.mock(ModelPortfolioUpload.class), new FailFastErrorsImpl());
    }
}
