package com.bt.nextgen.service.avaloq.rollover;

import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationConverter;
import com.bt.nextgen.service.integration.rollover.CashRolloverService;
import com.bt.nextgen.service.integration.rollover.RolloverDetails;
import com.bt.nextgen.service.integration.rollover.SuperfundDetails;
import com.btfin.panorama.core.validation.Validator;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service("avaloqCashRolloverIntegrationService")
public class AvaloqCashRolloverServiceImpl extends AbstractAvaloqIntegrationService implements CashRolloverService {

    private static final Logger logger = LoggerFactory.getLogger(AvaloqCashRolloverServiceImpl.class);

    @Autowired
    private AvaloqReportService avaloqService;

    @Autowired
    private AvaloqExecute avaloqExecute;

    @Autowired
    private AvaloqGatewayHelperService webserviceClient;

    @Autowired
    private CacheSuperfundIntegrationServiceImpl cachedSuperfundService;

    @Autowired
    private RolloverConverter rolloverConverter;

    @Autowired
    private TransactionValidationConverter validationConverter;

    @Autowired
    private Validator validator;

    @Override
    public List<SuperfundDetails> loadAvailableSuperfunds(ServiceErrors serviceErrors) {
        try {
            return cachedSuperfundService.loadAvailableSuperfunds(serviceErrors);
        } catch (ClassCastException cce) {
            logger.warn("Caught ClassCastException when casting avaloq response to report type", cce);
            return Collections.emptyList();
        }
    }

    @Override
    public RolloverDetails submitRolloverInDetails(final RolloverDetails rolloverDetails, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<RolloverDetails>("submitRolloverDetails", serviceErrors) {
            @Override
            public RolloverDetails performOperation() {
                RolloverDetailsImpl rollIn = webserviceClient.sendToWebService(
                        rolloverConverter.toRolloverInRequest(rolloverDetails), AvaloqOperation.RLOV_IN_REQ,
                        RolloverDetailsImpl.class, serviceErrors);

                verifyTransactionServiceResponse(rollIn, serviceErrors, "submitRolloverDetails",
                        getRolloverDetailsString(rolloverDetails));
                rollIn.setValidationErrors(validationConverter.toValidationError(rollIn, rollIn.getWarnings()));
                return rollIn;
            }
        }.run();
    }

    @Override
    public RolloverDetails saveRolloverDetails(final RolloverDetails rolloverDetails, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<RolloverDetails>("saveRolloverDetails", serviceErrors) {

            @Override
            public RolloverDetails performOperation() {
                RolloverDetailsImpl rollIn = webserviceClient.sendToWebService(
                        rolloverConverter.toSaveRolloverRequest(rolloverDetails), AvaloqOperation.RLOV_IN_REQ,
                        RolloverDetailsImpl.class, serviceErrors);
                verifyTransactionServiceResponse(rollIn, serviceErrors, "saveRolloverDetails",
                        getRolloverDetailsString(rolloverDetails));
                return rollIn;
            }
        }.run();
    }

    @Override
    public RolloverDetails loadRolloverDetails(final String rolloverId, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<RolloverDetails>("loadRolloverDetails", serviceErrors) {

            @Override
            public RolloverDetails performOperation() {
                RolloverDetailsImpl rollIn = webserviceClient.sendToWebService(
                        rolloverConverter.toLoadRolloverRequest(rolloverId), AvaloqOperation.RLOV_IN_REQ,
                        RolloverDetailsImpl.class, serviceErrors);
                verifyTransactionServiceResponse(rollIn, serviceErrors, "loadRolloverDetails", rolloverId);
                return rollIn;
            }
        }.run();
    }

    @Override
    public RolloverDetails discardRolloverDetails(final String rolloverId, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<RolloverDetails>("discardRolloverDetails", serviceErrors) {

            @Override
            public RolloverDetails performOperation() {
                RolloverDetailsImpl rollIn = webserviceClient.sendToWebService(
                        rolloverConverter.toDiscardRolloverRequest(rolloverId), AvaloqOperation.RLOV_IN_REQ,
                        RolloverDetailsImpl.class, serviceErrors);
                verifyTransactionServiceResponse(rollIn, serviceErrors, "discardRolloverDetails", rolloverId);
                return rollIn;
            }
        }.run();
    }

    /**
     * Handle error (if any) in a transaction service. Error message will be logged and added to the serviceErrors. This will
     * cause a red Technical Error on the front end because the error is added to the serviceErrors list.
     * 
     * @param rollIn
     * @param serviceErrors
     * @param strings
     */
    protected void verifyTransactionServiceResponse(final RolloverDetailsImpl rollIn, final ServiceErrors serviceErrors,
            String... strings) {
        if (rollIn.isErrorResponse()) {
            StringBuilder builder = new StringBuilder();
            if (strings != null) {
                for (String str : strings) {
                    builder.append(" ");
                    builder.append(str.trim());
                }
            }
            builder.append(" Error encountered: ");
            builder.append(rollIn.getErrorMessage());
            logger.error(builder.toString());
            serviceErrors.addError(new ServiceErrorImpl(builder.toString()));
        }
    }

    /**
     * Construct a String containing details of the rolloverDetails specified. Used for logging error in transaction.
     * 
     * @param rolloverDetails
     * @return A String containing the fundName and fundId.
     */
    private String getRolloverDetailsString(final RolloverDetails rolloverDetails) {
        StringBuilder builder = new StringBuilder();
        builder.append(rolloverDetails.getFundName() + ", id: ");
        builder.append(rolloverDetails.getFundId());
        return builder.toString();
    }

}
