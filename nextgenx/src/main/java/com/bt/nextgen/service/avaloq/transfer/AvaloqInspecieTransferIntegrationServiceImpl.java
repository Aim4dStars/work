package com.bt.nextgen.service.avaloq.transfer;

import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.account.AccountParams;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationConverter;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.transfer.InspecieTransferIntegrationService;
import com.bt.nextgen.service.integration.transfer.TransferDetails;
import com.bt.nextgen.service.integration.transfer.TransferOrder;
import com.bt.nextgen.service.request.AvaloqReportRequestImpl;
import com.bt.nextgen.service.request.AvaloqRequest;
import com.btfin.panorama.core.validation.Validator;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("avaloqInspecieTransferIntegrationService")
public class AvaloqInspecieTransferIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements
        InspecieTransferIntegrationService {

    @Autowired
    private AvaloqReportService avaloqService;

    @Autowired
    private Validator validator;

    @Autowired
    private InspecieTransferConverter transferConverter;

    @Autowired
    private TransactionValidationConverter validationConverter;

    @Autowired
    private AvaloqGatewayHelperService webserviceClient;

    public TransferDetails validateTransfer(final TransferDetails transfer, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<TransferDetails>("validateInspecieTransfer", serviceErrors) {
            @Override
            public TransferDetails performOperation() {
                TransferDetailsImpl details = webserviceClient.sendToWebService(
                        transferConverter.toValidateTransferRequest(transfer), AvaloqOperation.MASS_SETTLE_REQ,
                        TransferDetailsImpl.class, serviceErrors);

                details.setAccountKey(transfer.getAccountKey());
                details.setValidationErrors(validationConverter.toValidationError(details, details.getWarnings()));
                return details;
            }
        }.run();
    }

    public TransferDetails submitTransfer(final TransferDetails transfer, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<TransferDetails>("submitInspecieTransfer", serviceErrors) {
            @Override
            public TransferDetails performOperation() {
                TransferDetailsImpl details = webserviceClient.sendToWebService(transferConverter.toSubmitTransfer(transfer),
                        AvaloqOperation.MASS_SETTLE_REQ, TransferDetailsImpl.class, serviceErrors);

                details.setAccountKey(transfer.getAccountKey());
                details.setValidationErrors(validationConverter.toValidationError(details, details.getWarnings()));
                return details;
            }
        }.run();
    }

    @Override
    public TransferDetails loadTransferDetails(final String transferId, final AccountKey accountKey,
            final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<TransferDetails>("loadTransferDetails", serviceErrors) {
            @Override
            public TransferDetails performOperation() {
                TransferDetailsImpl details = webserviceClient.sendToWebService(
                        transferConverter.toLoadTransferDetails(transferId), AvaloqOperation.MASS_SETTLE_REQ,
                        TransferDetailsImpl.class, serviceErrors);
                details.setAccountKey(accountKey);
                return details;
            }
        }.run();
    }

    public List<TransferOrder> loadAccountTransferOrders(final AccountKey accountKey, final ServiceErrors serviceErrors) {

        final List<TransferOrder> orders = new ArrayList<>();
        new IntegrationOperation("loadAccountTransferOrders", serviceErrors) {

            @Override
            public void performOperation() {

                AvaloqRequest avaloqRequest = new AvaloqReportRequestImpl(TransferTemplate.INSPECIE_TRANSFER).forParam(
                        AccountParams.PARAM_ACCOUNT_ID, accountKey.getId());
                TransferResponseImpl response = avaloqService.executeReportRequestToDomain(avaloqRequest,
                        TransferResponseImpl.class, serviceErrors);

                if (response != null && response.getTransferOrders() != null) {
                    orders.addAll(response.getTransferOrders());
                    validator.validate(orders, serviceErrors);
                }
            }
        }.run();

        return orders;
    }
}
