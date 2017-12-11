package com.bt.nextgen.service.avaloq.transfer.transfergroup;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationConverter;
import com.bt.nextgen.service.integration.transfer.transfergroup.TransferGroupDetails;
import com.btfin.abs.trxservice.xferbdl.v1_0.XferBdlReq;
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
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AvaloqTransferGroupIntegrationServiceImplTest {

    @InjectMocks
    private AvaloqTransferGroupIntegrationServiceImpl transferGroupService;

    @Mock
    private TransferGroupConverter transferConverter;

    @Mock
    private TransactionValidationConverter validationConverter;

    @Mock
    private AvaloqGatewayHelperService webserviceClient;

    @Test
    public void testWhenValidateTransfer_thenTransferDetailsReturned() {
        final TransferGroupDetailsImpl response = Mockito.mock(TransferGroupDetailsImpl.class);
        Mockito.when(response.isErrorResponse()).thenReturn(false);
        Mockito.when(response.getErrorMessage()).thenReturn(null);

        final XferBdlReq request = Mockito.mock(XferBdlReq.class);
        Mockito.when(
                webserviceClient.sendToWebService(Mockito.any(XferBdlReq.class), Mockito.any(AvaloqOperation.class),
                        Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenReturn(response);

        Mockito.when(transferConverter.toValidateTransferRequest(Mockito.any(TransferGroupDetailsImpl.class)))
                .thenReturn(request);

        Mockito.when(transferConverter.replaceSponsorDetailPid(Mockito.any(TransferGroupDetails.class),
                        Mockito.any(TransferGroupDetailsImpl.class))).thenReturn(response);

        TransferGroupDetails result = transferGroupService.validateTransfer(Mockito.mock(TransferGroupDetailsImpl.class),
                new FailFastErrorsImpl());
        Assert.assertNotNull(result);
    }

    @Test
    public void testWhenSubmitTransfer_thenTransferDetailsReturned() {
        final TransferGroupDetailsImpl response = Mockito.mock(TransferGroupDetailsImpl.class);
        Mockito.when(response.isErrorResponse()).thenReturn(false);
        Mockito.when(response.getErrorMessage()).thenReturn(null);

        final XferBdlReq request = Mockito.mock(XferBdlReq.class);
        Mockito.when(
                webserviceClient.sendToWebService(Mockito.any(XferBdlReq.class), Mockito.any(AvaloqOperation.class),
                        Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenReturn(response);

        Mockito.when(transferConverter.toSubmitTransferRequest(Mockito.any(TransferGroupDetailsImpl.class)))
                .thenReturn(request);

        Mockito.when(transferConverter.replaceSponsorDetailPid(Mockito.any(TransferGroupDetails.class),
                        Mockito.any(TransferGroupDetailsImpl.class))).thenReturn(response);

        TransferGroupDetails result = transferGroupService.submitTransfer(Mockito.mock(TransferGroupDetailsImpl.class),
                new FailFastErrorsImpl());
        Assert.assertNotNull(result);
    }

    @Test
    public void testWhenLoadTransfer_thenTransferDetailsReturned() {
        final TransferGroupDetailsImpl response = Mockito.mock(TransferGroupDetailsImpl.class);
        Mockito.when(response.isErrorResponse()).thenReturn(false);
        Mockito.when(response.getErrorMessage()).thenReturn(null);

        final XferBdlReq request = Mockito.mock(XferBdlReq.class);
        Mockito.when(
                webserviceClient.sendToWebService(Mockito.any(XferBdlReq.class), Mockito.any(AvaloqOperation.class),
                        Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenReturn(response);

        Mockito.when(transferConverter.toLoadTransferRequest(Mockito.anyString())).thenReturn(request);

        TransferGroupDetails result = transferGroupService.loadTransferDetails(Mockito.anyString(), new FailFastErrorsImpl());
        Assert.assertNotNull(result);
    }

    @Test(expected = ServiceException.class)
    public void testWhenValidateTransferWithError_thenErrorIsCaught() {
        final TransferGroupDetailsImpl response = Mockito.mock(TransferGroupDetailsImpl.class);
        Mockito.when(response.isErrorResponse()).thenReturn(true);
        Mockito.when(response.getErrorMessage()).thenReturn("error message");

        final XferBdlReq request = Mockito.mock(XferBdlReq.class);

        Mockito.when(
                webserviceClient.sendToWebService(Mockito.any(XferBdlReq.class), Mockito.any(AvaloqOperation.class),
                        Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenReturn(response);

        Mockito.when(transferConverter.toValidateTransferRequest(Mockito.any(TransferGroupDetailsImpl.class)))
                .thenReturn(request);

        transferGroupService.validateTransfer(Mockito.mock(TransferGroupDetailsImpl.class),
                new FailFastErrorsImpl());
    }

    @Test(expected = ServiceException.class)
    public void testWhenSubmitTransferWithError_thenErrorIsCaught() {
        final TransferGroupDetailsImpl response = Mockito.mock(TransferGroupDetailsImpl.class);
        Mockito.when(response.isErrorResponse()).thenReturn(true);
        Mockito.when(response.getErrorMessage()).thenReturn("error message");

        final XferBdlReq request = Mockito.mock(XferBdlReq.class);

        Mockito.when(
                webserviceClient.sendToWebService(Mockito.any(XferBdlReq.class), Mockito.any(AvaloqOperation.class),
                        Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenReturn(response);

        Mockito.when(transferConverter.toSubmitTransferRequest(Mockito.any(TransferGroupDetailsImpl.class))).thenReturn(request);

        transferGroupService.submitTransfer(Mockito.mock(TransferGroupDetailsImpl.class),
                new FailFastErrorsImpl());
    }

    @Test(expected = ServiceException.class)
    public void testWhenLoadTransferWithError_thenErrorIsCaught() {
        final TransferGroupDetailsImpl response = Mockito.mock(TransferGroupDetailsImpl.class);
        Mockito.when(response.isErrorResponse()).thenReturn(true);
        Mockito.when(response.getErrorMessage()).thenReturn("error message");

        final XferBdlReq request = Mockito.mock(XferBdlReq.class);

        Mockito.when(
                webserviceClient.sendToWebService(Mockito.any(XferBdlReq.class), Mockito.any(AvaloqOperation.class),
                        Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenReturn(response);

        Mockito.when(transferConverter.toLoadTransferRequest(Mockito.anyString())).thenReturn(request);

        transferGroupService.loadTransferDetails(Mockito.anyString(), new FailFastErrorsImpl());
    }
}
