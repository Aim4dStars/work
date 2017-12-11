package com.bt.nextgen.service.avaloq.rollover;

import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationConverter;
import com.bt.nextgen.service.integration.rollover.RolloverDetails;
import com.bt.nextgen.service.integration.rollover.SuperfundDetails;
import com.bt.nextgen.service.integration.transaction.TransactionResponse;
import com.bt.nextgen.service.integration.transaction.TransactionValidation;
import com.bt.nextgen.service.request.AvaloqReportRequestImpl;
import com.bt.nextgen.service.request.AvaloqRequest;
import com.btfin.abs.trxservice.rlovin.v1_0.RlovInReq;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class CashRolloverServiceTest {

    @InjectMocks
    private AvaloqCashRolloverServiceImpl rolloverIntegrationService;

    @Mock
    private AvaloqReportService avaloqService;

    @Mock
    private AvaloqGatewayHelperService webserviceClient;

    @Mock
    private CacheSuperfundIntegrationServiceImpl cachedSuperfundService;

    @Mock
    private RolloverConverter rolloverConverter;

    @Mock
    private TransactionValidationConverter validationConverter;

    @Test
    public void testLoadAvailableSuperfunds() {
        Mockito.when(
                avaloqService.executeReportRequestToDomain(Mockito.any(AvaloqRequest.class), Mockito.any(Class.class),
                        Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<SuperfundResponseImpl>() {
            @Override
            public SuperfundResponseImpl answer(InvocationOnMock invocation) throws Throwable {
                AvaloqReportRequestImpl req = (AvaloqReportRequestImpl) invocation.getArguments()[0];
                Assert.assertEquals(CashRolloverTemplate.CASH_ROLLOVER, req.getTemplate());

                SuperfundResponseImpl mockResponse = Mockito.mock(SuperfundResponseImpl.class);
                SuperfundDetails details1 = Mockito.mock(SuperfundDetails.class);
                SuperfundDetails details2 = Mockito.mock(SuperfundDetails.class);
                Mockito.when(mockResponse.getSuperfunds()).thenReturn(Arrays.asList(details1, details2));
                return mockResponse;
            }
        });

        List<SuperfundDetails> response = cachedSuperfundService.loadAvailableSuperfunds(new FailFastErrorsImpl());
        Assert.assertNotNull(response);
    }

    @Test
    public void testSubmitRolloverInDetails() {
        final RolloverDetailsImpl response = Mockito.mock(RolloverDetailsImpl.class);
        final RlovInReq request = Mockito.mock(RlovInReq.class);
        Mockito.when(
                webserviceClient.sendToWebService(Mockito.any(RlovInReq.class), Mockito.any(AvaloqOperation.class),
                        Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<RolloverDetailsImpl>() {

                    @Override
                    public RolloverDetailsImpl answer(InvocationOnMock invocation) throws Throwable {
                        RlovInReq req = (RlovInReq) invocation.getArguments()[0];
                        Assert.assertEquals(request, req);
                        return response;
                    }
                });

        Mockito.when(rolloverConverter.toRolloverInRequest(Mockito.any(RolloverDetails.class))).thenReturn(request);

        Mockito.when(
                validationConverter.toValidationError(Mockito.any(TransactionResponse.class),
                        Mockito.anyListOf(TransactionValidation.class))).thenReturn(null);

        rolloverIntegrationService.submitRolloverInDetails(Mockito.mock(RolloverDetailsImpl.class), new FailFastErrorsImpl());
    }

    @Test(expected = ServiceException.class)
    public void testSubmit_fatalError() {
        RolloverDetailsImpl model = new RolloverDetailsImpl();
        model.setErrorMessage("errorMessage");
        model.setFundName("fundName");
        model.setFundId("fundId");

        Mockito.when(
                webserviceClient.sendToWebService(Mockito.any(RlovInReq.class), Mockito.any(AvaloqOperation.class),
                        Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenReturn(model);

        RolloverDetails details = rolloverIntegrationService.submitRolloverInDetails(Mockito.mock(RolloverDetailsImpl.class),
                new FailFastErrorsImpl());
        Assert.assertNotNull(details);
    }

    @Test
    public void testSubmit_SuccessWithError() {
        RolloverDetailsImpl model = new RolloverDetailsImpl();
        model.setFundName("fundName");
        model.setFundId("fundId");

        Mockito.when(
                webserviceClient.sendToWebService(Mockito.any(RlovInReq.class), Mockito.any(AvaloqOperation.class),
                        Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenReturn(model);

        ValidationError err = Mockito.mock(ValidationError.class);
        Mockito.when(
                validationConverter.toValidationError(Mockito.any(TransactionResponse.class),
                        Mockito.anyListOf(TransactionValidation.class))).thenReturn(Collections.singletonList(err));

        RolloverDetails details = rolloverIntegrationService.submitRolloverInDetails(Mockito.mock(RolloverDetailsImpl.class),
                new FailFastErrorsImpl());
        Assert.assertNotNull(((TransactionResponse) details).getValidationErrors());
    }

    @Test
    public void testSaveRolloverInDetails() {
        final RolloverDetailsImpl response = Mockito.mock(RolloverDetailsImpl.class);
        final RlovInReq request = Mockito.mock(RlovInReq.class);
        Mockito.when(
                webserviceClient.sendToWebService(Mockito.any(RlovInReq.class), Mockito.any(AvaloqOperation.class),
                        Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<RolloverDetailsImpl>() {

                    @Override
                    public RolloverDetailsImpl answer(InvocationOnMock invocation) throws Throwable {
                        RlovInReq req = (RlovInReq) invocation.getArguments()[0];
                        Assert.assertEquals(request, req);
                        return response;
                    }
                });

        Mockito.when(rolloverConverter.toSaveRolloverRequest(Mockito.any(RolloverDetails.class))).thenReturn(request);

        rolloverIntegrationService.saveRolloverDetails(Mockito.mock(RolloverDetailsImpl.class), new FailFastErrorsImpl());
    }

    @Test(expected = ServiceException.class)
    public void testSave_fatalError() {
        RolloverDetailsImpl model = new RolloverDetailsImpl();
        model.setErrorMessage("errorMessage");
        model.setFundName("fundName");
        model.setFundId("fundId");

        Mockito.when(
                webserviceClient.sendToWebService(Mockito.any(RlovInReq.class), Mockito.any(AvaloqOperation.class),
                        Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenReturn(model);

        rolloverIntegrationService.saveRolloverDetails(Mockito.mock(RolloverDetailsImpl.class), new FailFastErrorsImpl());
    }

    @Test
    public void testLoadRolloverInDetails() {
        final RolloverDetailsImpl response = Mockito.mock(RolloverDetailsImpl.class);
        final RlovInReq request = Mockito.mock(RlovInReq.class);
        Mockito.when(
                webserviceClient.sendToWebService(Mockito.any(RlovInReq.class), Mockito.any(AvaloqOperation.class),
                        Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<RolloverDetailsImpl>() {

                    @Override
                    public RolloverDetailsImpl answer(InvocationOnMock invocation) throws Throwable {
                        RlovInReq req = (RlovInReq) invocation.getArguments()[0];
                        Assert.assertEquals(request, req);
                        return response;
                    }
                });

        Mockito.when(rolloverConverter.toLoadRolloverRequest(Mockito.anyString())).thenReturn(request);

        rolloverIntegrationService.loadRolloverDetails("rolloverId", new FailFastErrorsImpl());
    }

    @Test(expected = ServiceException.class)
    public void testLoadRolloverInDetails_fatalError() {
        RolloverDetailsImpl model = new RolloverDetailsImpl();
        model.setErrorMessage("errorMessage");
        model.setFundName("fundName");
        model.setFundId("fundId");

        Mockito.when(
                webserviceClient.sendToWebService(Mockito.any(RlovInReq.class), Mockito.any(AvaloqOperation.class),
                        Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenReturn(model);
        rolloverIntegrationService.loadRolloverDetails("rolloverId", new FailFastErrorsImpl());
    }

    @Test
    public void testDiscardRolloverInDetails() {
        final RolloverDetailsImpl response = Mockito.mock(RolloverDetailsImpl.class);
        final RlovInReq request = Mockito.mock(RlovInReq.class);
        Mockito.when(
                webserviceClient.sendToWebService(Mockito.any(RlovInReq.class), Mockito.any(AvaloqOperation.class),
                        Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<RolloverDetailsImpl>() {

                    @Override
                    public RolloverDetailsImpl answer(InvocationOnMock invocation) throws Throwable {
                        RlovInReq req = (RlovInReq) invocation.getArguments()[0];
                        Assert.assertEquals(request, req);
                        return response;
                    }
                });

        Mockito.when(rolloverConverter.toDiscardRolloverRequest(Mockito.anyString())).thenReturn(request);

        rolloverIntegrationService.discardRolloverDetails("rolloverId", new FailFastErrorsImpl());
    }

    @Test(expected = ServiceException.class)
    public void testDiscardRolloverInDetails_fatalError() {
        RolloverDetailsImpl model = new RolloverDetailsImpl();
        model.setErrorMessage("errorMessage");
        model.setFundName("fundName");
        model.setFundId("fundId");

        Mockito.when(
                webserviceClient.sendToWebService(Mockito.any(RlovInReq.class), Mockito.any(AvaloqOperation.class),
                        Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenReturn(model);
        rolloverIntegrationService.discardRolloverDetails("rolloverId", new FailFastErrorsImpl());
    }

    @Test(expected = ServiceException.class)
    public void test_verifyTxnServiceWithEmptyStringDetails() {
        RolloverDetailsImpl model = new RolloverDetailsImpl();
        model.setErrorMessage("errorMessage");

        rolloverIntegrationService.verifyTransactionServiceResponse(model, new FailFastErrorsImpl(), null);
    }
}
