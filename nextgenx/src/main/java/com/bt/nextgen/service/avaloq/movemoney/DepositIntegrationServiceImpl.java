package com.bt.nextgen.service.avaloq.movemoney;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.NotificationIntegrationServiceImpl;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.integration.PositionIdentifier;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.movemoney.DepositConverter;
import com.bt.nextgen.service.integration.movemoney.DepositDetails;
import com.bt.nextgen.service.integration.movemoney.DepositHolder;
import com.bt.nextgen.service.integration.movemoney.DepositIntegrationService;
import com.bt.nextgen.service.integration.movemoney.RecurringDepositDetails;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import com.btfin.abs.trxservice.inpay.v1_0.InpayReq;
import com.btfin.abs.trxservice.inpay.v1_0.InpayRsp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service Implementation for Deposit Service.
 * 
 */
@Service("DepositIntegrationServiceImplV2")
public class DepositIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements DepositIntegrationService {
    @Autowired
    private AvaloqExecute avaloqExecute;

    @Autowired
    private AvaloqGatewayHelperService webserviceClient;

    @Autowired
    private BankDateIntegrationService bankDateIntegrationService;

    @Autowired
    private DepositConverter depositConverter;

    private static final Logger logger = LoggerFactory.getLogger(NotificationIntegrationServiceImpl.class);

    @Override
    public DepositDetails validateDeposit(final DepositDetails deposit, final ServiceErrors serviceErrors) {
        logger.info("Entered validateDeposit Method");
        return new IntegrationSingleOperation<DepositDetails>("validateDeposit", serviceErrors) {
            @Override
            public DepositDetails performOperation() {
                boolean isFuture = deposit.getTransactionDate().isAfter(bankDateIntegrationService.getBankDate(serviceErrors));
                InpayReq inpayReq = depositConverter.toValidateDepositRequest(deposit, isFuture, serviceErrors);
                InpayRsp inpayRsp = webserviceClient.sendToWebService(inpayReq, AvaloqOperation.INPAY_REQ, serviceErrors);
                DepositDetails validationResponse = depositConverter.toValidateDepositResponse(inpayRsp, serviceErrors);
                logger.info("Exit of validateDeposit Method");
                return validationResponse;
            }
        }.run();
    }

    @Override
    public RecurringDepositDetails validateDeposit(final RecurringDepositDetails deposit, final ServiceErrors serviceErrors) {
        logger.info("Entered validateDeposit Method");
        return new IntegrationSingleOperation<RecurringDepositDetails>("validateDeposit", serviceErrors) {
            @Override
            public RecurringDepositDetails performOperation() {
                InpayReq inpayReq = depositConverter.toValidateRecurringDepositRequest(deposit, serviceErrors);
                InpayRsp inpayRsp = webserviceClient.sendToWebService(inpayReq, AvaloqOperation.INPAY_REQ, serviceErrors);
                RecurringDepositDetails validationResponse = depositConverter.toValidateRecurringDepositResponse(inpayRsp,
                        serviceErrors);
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
                boolean isFuture = deposit.getTransactionDate().isAfter(bankDateIntegrationService.getBankDate(serviceErrors));
                InpayReq inpayReq = depositConverter.toSubmitDepositRequest(deposit, isFuture, serviceErrors);
                InpayRsp inpayRsp = webserviceClient.sendToWebService(inpayReq, AvaloqOperation.INPAY_REQ, serviceErrors);
                DepositDetails depositResponse = depositConverter.toSubmitDepositResponse(inpayRsp, serviceErrors);
                logger.info("Exit of submitDeposit Method");
                return depositResponse;
            }
        }.run();
    }

    @Override
    public RecurringDepositDetails submitDeposit(final RecurringDepositDetails deposit, final ServiceErrors serviceErrors) {
        logger.info("Entered submitDeposit Method");
        return new IntegrationSingleOperation<RecurringDepositDetails>("submitDeposit", serviceErrors) {
            @Override
            public RecurringDepositDetails performOperation() {
                InpayReq inpayReq = depositConverter.toSubmitRecurringDepositRequest(deposit, serviceErrors);
                InpayRsp inpayRsp = webserviceClient.sendToWebService(inpayReq, AvaloqOperation.INPAY_REQ, serviceErrors);
                RecurringDepositDetails depositResponse = depositConverter.toSubmitRecurringDepositResponse(inpayRsp,
                        serviceErrors);
                logger.info("Exit of submitDeposit Method");
                return depositResponse;
            }
        }.run();
    }

    @Override
    public DepositDetails createDeposit(final DepositDetails deposit, final ServiceErrors serviceErrors) {
        logger.info("Entered createDeposit Method");
        return new IntegrationSingleOperation<DepositDetails>("createDeposit", serviceErrors) {
            @Override
            public DepositDetails performOperation() {
                boolean isFuture = deposit.getTransactionDate().isAfter(bankDateIntegrationService.getBankDate(serviceErrors));
                InpayReq inpayReq = depositConverter.toSaveDepositRequest(deposit, isFuture, serviceErrors);
                InpayRsp inpayRsp = webserviceClient.sendToWebService(inpayReq, AvaloqOperation.INPAY_REQ, serviceErrors);
                DepositDetails depositResponse = depositConverter.toSaveDepositResponse(inpayRsp, serviceErrors);
                logger.info("Exit of submitDeposit Method");
                return depositResponse;
            }
        }.run();
    }

    @Override
    public RecurringDepositDetails createDeposit(final RecurringDepositDetails deposit, final ServiceErrors serviceErrors) {
        logger.info("Entered createDeposit Method");
        return new IntegrationSingleOperation<RecurringDepositDetails>("createDeposit", serviceErrors) {
            @Override
            public RecurringDepositDetails performOperation() {
                InpayReq inpayReq = depositConverter.toSaveRecurringDepositRequest(deposit, serviceErrors);
                InpayRsp inpayRsp = webserviceClient.sendToWebService(inpayReq, AvaloqOperation.INPAY_REQ, serviceErrors);
                RecurringDepositDetails depositResponse = depositConverter.toSaveRecurringDepositResponse(inpayRsp,
                        serviceErrors);
                logger.info("Exit of createDeposit Method");
                return depositResponse;
            }
        }.run();
    }

    @Override
    public DepositDetails updateDeposit(final DepositDetails deposit, final ServiceErrors serviceErrors) {
        logger.info("Entered updateDeposit Method");
        return new IntegrationSingleOperation<DepositDetails>("updateDeposit", serviceErrors) {
            @Override
            public DepositDetails performOperation() {
                boolean isFuture = deposit.getTransactionDate().isAfter(bankDateIntegrationService.getBankDate(serviceErrors));
                InpayReq inpayReq = depositConverter.toSaveDepositRequest(deposit, isFuture, serviceErrors);
                InpayRsp inpayRsp = webserviceClient.sendToWebService(inpayReq, AvaloqOperation.INPAY_REQ, serviceErrors);
                DepositDetails depositResponse = depositConverter.toSaveDepositResponse(inpayRsp, serviceErrors);
                logger.info("Exit of createDeposit Method");
                return depositResponse;
            }
        }.run();
    }

    @Override
    public RecurringDepositDetails updateDeposit(final RecurringDepositDetails deposit, final ServiceErrors serviceErrors) {
        logger.info("Entered updateDeposit Method");
        return new IntegrationSingleOperation<RecurringDepositDetails>("updateDeposit", serviceErrors) {
            @Override
            public RecurringDepositDetails performOperation() {
                InpayReq inpayReq = depositConverter.toSaveRecurringDepositRequest(deposit, serviceErrors);
                InpayRsp inpayRsp = webserviceClient.sendToWebService(inpayReq, AvaloqOperation.INPAY_REQ, serviceErrors);
                RecurringDepositDetails depositResponse = depositConverter.toSaveRecurringDepositResponse(inpayRsp,
                        serviceErrors);
                logger.info("Exit of updateDeposit Method");
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
                        depositConverter.toStopDepositRequest(positionIdentifier.getPositionId(), serviceErrors),
                        AvaloqOperation.INPAY_REQ, serviceErrors);
                logger.info("Exit of stopDeposit Method");
                return depositConverter.toStopDepositResponse(inpayRsp, serviceErrors);
            }
        }.run();
    }

    @Override
    public void deleteDeposit(final String depositId, final ServiceErrors serviceErrors) {
        new IntegrationOperation("deleteDeposit", serviceErrors) {
            @Override
            public void performOperation() {
                InpayReq inpayReq = depositConverter.toDeleteDepositRequest(depositId, Constants.DELETE_DEPOSIT_EXISTING,
                        serviceErrors);
                InpayRsp inpayRsp = webserviceClient.sendToWebService(inpayReq, AvaloqOperation.INPAY_REQ, serviceErrors);
                depositConverter.processDeleteResponse(inpayRsp, serviceErrors);
            }
        }.run();
    }

    @Override
    public void deleteRecurringDeposit(final String depositId, final ServiceErrors serviceErrors) {
        new IntegrationOperation("deleteRecurringDeposit", serviceErrors) {
            @Override
            public void performOperation() {
                InpayReq inpayReq = depositConverter.toDeleteDepositRequest(depositId,
                        Constants.DELETE_DEPOSIT_EXISTING_RECURRING, serviceErrors);
                InpayRsp inpayRsp = webserviceClient.sendToWebService(inpayReq, AvaloqOperation.INPAY_REQ, serviceErrors);
                depositConverter.processDeleteResponse(inpayRsp, serviceErrors);
            }
        }.run();
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public List<DepositDetails> loadSavedDeposits(WrapAccountIdentifier identifier, ServiceErrors serviceErrors) {
        List<DepositDetails> deposits = new ArrayList<>();

        DepositHolder depositHolder = avaloqExecute.executeReportRequestToDomain(
                new AvaloqReportRequest(Template.SAVED_DEPOSITS.getName()).forAccount(identifier.getAccountIdentifier()),
                DepositHolderImpl.class, serviceErrors);

        if (depositHolder != null && depositHolder.getDeposits() != null) {
            deposits.addAll(depositHolder.getDeposits());
        }

        return deposits;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public DepositDetails loadSavedDeposit(String depositId, ServiceErrors serviceErrors) {
        DepositDetails deposit = new DepositDetailsImpl();

        DepositHolder depositHolder = avaloqExecute
                .executeReportRequestToDomain(new AvaloqReportRequest(Template.SAVED_DEPOSITS.getName())
                        .forDocumentIdList(Collections.singletonList(depositId)), DepositHolderImpl.class, serviceErrors);

        if (depositHolder != null && depositHolder.getDeposits() != null) {
            deposit = (DepositDetails) depositHolder.getDeposits().get(0);
        }

        return deposit;
    }
}
