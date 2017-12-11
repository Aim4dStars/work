package com.bt.nextgen.service.avaloq.deposit;

import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.NotificationIntegrationServiceImpl;
import com.bt.nextgen.service.integration.PositionIdentifier;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.deposit.DepositConverter;
import com.bt.nextgen.service.integration.deposit.DepositDetails;
import com.bt.nextgen.service.integration.deposit.DepositIntegrationService;
import com.bt.nextgen.service.integration.deposit.RecurringDepositDetails;
import com.btfin.abs.trxservice.inpay.v1_0.InpayRsp;

/**
 * @deprecated Use package com.bt.nextgen.service.avaloq.movemoney.DepositIntegrationServiceImpl
 */
@Deprecated
@Service
public class DepositIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements DepositIntegrationService {
    @Autowired
    private AvaloqGatewayHelperService webserviceClient;

    @Autowired
    private BankDateIntegrationService bankDateIntegrationService;

    private static final Logger logger = LoggerFactory.getLogger(NotificationIntegrationServiceImpl.class);

    @Override
    public DepositDetails validateDeposit(final DepositDetails deposit, final ServiceErrors serviceErrors) {
        logger.info("Entered validateDeposit Method");
        return new IntegrationSingleOperation<DepositDetails>("validateDeposit", serviceErrors) {
            @Override
            public DepositDetails performOperation() {
                boolean isFuture = deposit.getTransactionDate()
                        .after(bankDateIntegrationService.getBankDate(serviceErrors).toDate());
                InpayRsp inpayRsp = webserviceClient.sendToWebService(
                        DepositConverter.toValidateDepositRequest(deposit, isFuture, serviceErrors), AvaloqOperation.INPAY_REQ,
                        serviceErrors);
                DepositDetails validationResponse = DepositConverter.toValidateDepositResponse(deposit, inpayRsp, serviceErrors);
                logger.info("Exit of validateDeposit Method");
                return validationResponse;
            }
        }.run();

    }

    @Override
    public DepositDetails submitDeposit(final DepositDetails deposit, final ServiceErrors serviceErrors) {
        logger.info("Entered submitDeposit Method");
        return new IntegrationSingleOperation<DepositDetails>("submitDeposit", serviceErrors) {
            @Override
            public DepositDetails performOperation() {
                /* TODO: If need to consider Overridable Errors */
                /*
                 * for (OverridableServiceErrorIdentifier err : deposit.getOverridableErrorList()) {
                 * deposit.getOverridableErrorList().add((OverridableServiceErrorIdentifier)err);
                 * 
                 * }
                 */
                boolean isFuture = deposit.getTransactionDate()
                        .after(bankDateIntegrationService.getBankDate(serviceErrors).toDate());
                InpayRsp inpayRsp = webserviceClient.sendToWebService(
                        DepositConverter.toSubmitDepositRequest(deposit, isFuture, serviceErrors), AvaloqOperation.INPAY_REQ,
                        serviceErrors);
                DepositDetails depositResponse = DepositConverter.toSubmitDepositResponse(deposit, inpayRsp, serviceErrors);
                logger.info("Exit of submitDeposit Method");
                return depositResponse;
            }
        }.run();
    }

    @Override
    public RecurringDepositDetails validateDeposit(final RecurringDepositDetails deposit, final ServiceErrors serviceErrors) {
        logger.info("Entered validateDeposit Method");
        return new IntegrationSingleOperation<RecurringDepositDetails>("validateDeposit", serviceErrors) {
            @Override
            public RecurringDepositDetails performOperation() {
                InpayRsp inpayRsp = webserviceClient.sendToWebService(
                        DepositConverter.toValidateRecurringDepositRequest(deposit, serviceErrors), AvaloqOperation.INPAY_REQ,
                        serviceErrors);
                RecurringDepositDetails validationResponse = DepositConverter.toValidateRecurringDepositResponse(deposit,
                        inpayRsp, serviceErrors);
                logger.info("Exit of validateDeposit Method");
                return validationResponse;
            }
        }.run();

    }

    @Override
    public RecurringDepositDetails submitDeposit(final RecurringDepositDetails deposit, final ServiceErrors serviceErrors) {
        logger.info("Entered submitDeposit Method");
        return new IntegrationSingleOperation<RecurringDepositDetails>("submitDeposit", serviceErrors) {
            @Override
            public RecurringDepositDetails performOperation() {
                /* TODO: If need to consider Overridable Errors */
                /*
                 * for (OverridableServiceErrorIdentifier err : deposit.getOverridableErrorList()) {
                 * deposit.getOverridableErrorList().add((OverridableServiceErrorIdentifier)err);
                 * 
                 * }
                 */
                InpayRsp inpayRsp = webserviceClient.sendToWebService(
                        DepositConverter.toSubmitRecurringDepositRequest(deposit, serviceErrors), AvaloqOperation.INPAY_REQ,
                        serviceErrors);
                RecurringDepositDetails depositResponse = DepositConverter.toSubmitRecurringDepositResponse(deposit, inpayRsp,
                        serviceErrors);
                logger.info("Exit of submitDeposit Method");
                return depositResponse;
            }
        }.run();
    }

    @Override
    public TransactionStatus stopDeposit(final PositionIdentifier positionIdentifier, final ServiceErrors serviceErrors) {
        logger.info("Entered stopDeposit Method");
        return new IntegrationSingleOperation<TransactionStatus>("stopDeposit", serviceErrors) {
            @Override
            public TransactionStatus performOperation() {
                InpayRsp inpayRsp = webserviceClient.sendToWebService(
                        DepositConverter.toStopDepositRequest(positionIdentifier.getPositionId(), serviceErrors),
                        AvaloqOperation.INPAY_REQ, serviceErrors);
                logger.info("Exit of stopDeposit Method");
                return DepositConverter.toStopDepositResponse(inpayRsp, serviceErrors);
            }
        }.run();

    }
}
