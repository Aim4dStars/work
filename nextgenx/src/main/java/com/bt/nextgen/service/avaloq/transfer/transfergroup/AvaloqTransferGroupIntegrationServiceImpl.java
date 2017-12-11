package com.bt.nextgen.service.avaloq.transfer.transfergroup;

import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationConverter;
import com.bt.nextgen.service.integration.transfer.transfergroup.TransferGroupDetails;
import com.bt.nextgen.service.integration.transfer.transfergroup.TransferGroupIntegrationService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("avaloqTransferGroupIntegrationService")
public class AvaloqTransferGroupIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements
        TransferGroupIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(AvaloqTransferGroupIntegrationServiceImpl.class);

    @Autowired
    private AvaloqReportService avaloqService;

    @Autowired
    private TransferGroupConverter transferConverter;

    @Autowired
    private TransactionValidationConverter validationConverter;

    @Autowired
    private AvaloqGatewayHelperService webserviceClient;

    @Override
    public TransferGroupDetails validateTransfer(final TransferGroupDetails transferGroup, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<TransferGroupDetails>("validateTransferGroupDetails", serviceErrors) {
            @Override
            public TransferGroupDetails performOperation() {
                TransferGroupDetailsImpl details = webserviceClient.sendToWebService(
                        transferConverter.toValidateTransferRequest(transferGroup), AvaloqOperation.XFER_BDL_REQ,
                        TransferGroupDetailsImpl.class, serviceErrors);

                if (details.isErrorResponse()) {
                    logger.error("Error occurred after sending XFER_BDL_REQ to Avaloq for validation: "
                            + details.getErrorMessage());
                    serviceErrors.addError(new ServiceErrorImpl(details.getErrorMessage()));
                    return null;
                } else {
                    details = transferConverter.replaceSponsorDetailPid(transferGroup, details);
                    details.setValidationErrors(validationConverter.toValidationError(details, details.getWarnings()));
                    return details;
                }
            }
        }.run();
    }

    @Override
    public TransferGroupDetails submitTransfer(final TransferGroupDetails transfer, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<TransferGroupDetails>("submitTransferGroupDetails", serviceErrors) {
            @Override
            public TransferGroupDetails performOperation() {
                TransferGroupDetailsImpl details = webserviceClient.sendToWebService(
                        transferConverter.toSubmitTransferRequest(transfer), AvaloqOperation.XFER_BDL_REQ,
                        TransferGroupDetailsImpl.class, serviceErrors);

                if (details.isErrorResponse()) {
                    logger.error("Error occurred after sending XFER_BDL_REQ to Avaloq for submission: "
                            + details.getErrorMessage());
                    serviceErrors.addError(new ServiceErrorImpl(details.getErrorMessage()));
                    return null;
                } else {
                    details = transferConverter.replaceSponsorDetailPid(transfer, details);
                    details.setValidationErrors(validationConverter.toValidationError(details, details.getWarnings()));
                    return details;
                }
            }
        }.run();
    }

    public TransferGroupDetails loadTransferDetails(final String transferId, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<TransferGroupDetails>("loadTransferGroupDetails", serviceErrors) {
            @Override
            public TransferGroupDetails performOperation() {
                TransferGroupDetailsImpl details = webserviceClient.sendToWebService(
                        transferConverter.toLoadTransferRequest(transferId), AvaloqOperation.XFER_BDL_REQ,
                        TransferGroupDetailsImpl.class, serviceErrors);

                if (details.isErrorResponse()) {
                    logger.error("Error occurred after sending XFER_BDL_REQ to Avaloq to retrieve order " + transferId + ": "
                            + details.getErrorMessage());
                    serviceErrors.addError(new ServiceErrorImpl(details.getErrorMessage()));
                    return null;
                } else {
                    return details;
                }
            }
        }.run();
    }

}
