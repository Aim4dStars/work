package com.bt.nextgen.service.btesb.supermatch;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.btesb.base.model.EsbError;
import com.bt.nextgen.service.btesb.gateway.WebServiceHandler;
import com.bt.nextgen.service.btesb.supermatch.model.SuperMatchResponseHolderImpl;
import com.bt.nextgen.service.integration.supermatch.StatusSummary;
import com.bt.nextgen.service.integration.supermatch.SuperFundAccount;
import com.bt.nextgen.service.integration.supermatch.SuperMatchDetails;
import com.bt.nextgen.service.integration.supermatch.SuperMatchIntegrationService;
import com.bt.nextgen.service.integration.supermatch.SuperMatchResponseHolder;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.superannuationmatchservice.superannuationmatchrequest.v1_0.MaintainECOCustomerRequestMsgType;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.superannuationmatchservice.superannuationmatchrequest.v1_0.ObjectFactory;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.superannuationmatchservice.superannuationmatchrequest.v1_0.RetrieveDetailsRequestMsgType;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.superannuationmatchservice.superannuationmatchrequest.v1_0.SMRequestContextType;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.superannuationmatchservice.superannuationmatchrequest.v1_0.UpdateRolloverStatusRequestMsgType;
import ns.btfin_com.product.superannuationretirement.superannuationmatch.superannuationmatchservice.superannuationmatchrequest.v1_0.UpsertStatusSummaryRequestMsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation for {@link SuperMatchIntegrationService}
 */
@Service
public class SuperMatchIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements SuperMatchIntegrationService {

    @Autowired
    @Qualifier("btEsbWebServiceHandler")
    private WebServiceHandler webServiceHandler;

    @Autowired
    private SuperMatchRequestBuilder requestBuilder;

    private static final String RETRIEVE_SUPER_DETAILS_KEY = "retrieveSuperDetails";
    private static final String UPDATE_ROLLOVER_STATUS_KEY = "updateRollOverStatus";
    private static final String UPDATE_STATUS_SUMMARY_KEY = "updateStatusSummary";
    private static final String CREATE_MEMBER_KEY = "createMember";

    private static final String STATUS_ERROR = "Error";

    private static final Logger logger = LoggerFactory.getLogger(SuperMatchIntegrationServiceImpl.class);

    /**
     * Gets the super details for a customer in ECO
     *
     * @param customerId       - Customer identifier
     * @param superFundAccount - Super fund account {@link SuperFundAccount}
     * @param serviceErrors    - Object to capture service errors
     */
    @Override
    public List<SuperMatchDetails> retrieveSuperDetails(String customerId, SuperFundAccount superFundAccount, ServiceErrors serviceErrors) {
        final ObjectFactory objectFactory = new ObjectFactory();
        final RetrieveDetailsRequestMsgType requestMsg = objectFactory.createRetrieveDetailsRequestMsgType();

        requestMsg.setContext(requestBuilder.createRequestContext(objectFactory, customerId));
        requestMsg.setSuperannuationMatch(requestBuilder.createRequestForRetrieveDetails(objectFactory, superFundAccount));
        requestMsg.setFilter(requestBuilder.createFilterDetails(objectFactory));

        final SuperMatchResponseHolder response = webServiceHandler.sendToWebServiceAndParseResponseToDomain(RETRIEVE_SUPER_DETAILS_KEY, requestMsg,
                SuperMatchResponseHolderImpl.class, serviceErrors);

        return processResponse(requestMsg.getContext(), response, serviceErrors);
    }

    /**
     * Triggers the super roll-over for a customer
     *
     * @param customerId       - Customer identifier
     * @param superFundAccount - Super fund account {@link SuperFundAccount}
     * @param rollOverFunds    - Super funds to roll over
     * @param serviceErrors    - Object to capture service errors
     */
    @Override
    public List<SuperMatchDetails> updateRollOverStatus(String customerId, SuperFundAccount superFundAccount, List<SuperFundAccount> rollOverFunds, ServiceErrors serviceErrors) {
        final ObjectFactory objectFactory = new ObjectFactory();
        final UpdateRolloverStatusRequestMsgType requestMsg = objectFactory.createUpdateRolloverStatusRequestMsgType();

        requestMsg.setContext(requestBuilder.createRequestContext(objectFactory, customerId));
        requestMsg.setSuperannuationMatch(requestBuilder.createRequestForUpdateRollOver(objectFactory, superFundAccount, rollOverFunds));
        requestMsg.setChannelDetails(requestBuilder.createChannelDetail(objectFactory));

        final SuperMatchResponseHolder response = webServiceHandler.sendToWebServiceAndParseResponseToDomain(UPDATE_ROLLOVER_STATUS_KEY, requestMsg,
                SuperMatchResponseHolderImpl.class, serviceErrors);

        return processResponse(requestMsg.getContext(), response, serviceErrors);

    }

    /**
     * Updates consent status in ECO
     *
     * @param customerId        - Customer identifier
     * @param superFundAccount  - Super fund account {@link SuperFundAccount}
     * @param isConsentProvided - Flag to consent/unconsent for Super check
     * @param serviceErrors     - Object to capture service errors
     */
    @Override
    public List<SuperMatchDetails> updateConsentStatus(String customerId, SuperFundAccount superFundAccount, Boolean isConsentProvided, ServiceErrors serviceErrors) {
        final ObjectFactory objectFactory = new ObjectFactory();
        final UpsertStatusSummaryRequestMsgType requestMsg = objectFactory.createUpsertStatusSummaryRequestMsgType();

        requestMsg.setContext(requestBuilder.createRequestContext(objectFactory, customerId));
        requestMsg.setSuperannuationMatch(requestBuilder.createRequestForUpdateConsent(objectFactory, superFundAccount, isConsentProvided, customerId));
        requestMsg.setChannelDetails(requestBuilder.createChannelDetail(objectFactory));

        final SuperMatchResponseHolder response = webServiceHandler.sendToWebServiceAndParseResponseToDomain(UPDATE_STATUS_SUMMARY_KEY, requestMsg,
                SuperMatchResponseHolderImpl.class, serviceErrors);

        return processResponse(requestMsg.getContext(), response, serviceErrors);
    }

    /**
     * Updates acknowledgement status in ECO
     *
     * @param customerId       - Customer identifier
     * @param superFundAccount - Super fund account {@link SuperFundAccount}
     * @param serviceErrors    - Object to capture service errors
     */
    @Override
    public List<SuperMatchDetails> updateAcknowledgementStatus(String customerId, SuperFundAccount superFundAccount, ServiceErrors serviceErrors) {
        final ObjectFactory objectFactory = new ObjectFactory();
        final UpsertStatusSummaryRequestMsgType requestMsg = objectFactory.createUpsertStatusSummaryRequestMsgType();

        requestMsg.setContext(requestBuilder.createRequestContext(objectFactory, customerId));
        requestMsg.setSuperannuationMatch(requestBuilder.createRequestForAcknowledgement(objectFactory, superFundAccount));
        requestMsg.setChannelDetails(requestBuilder.createChannelDetail(objectFactory));

        final SuperMatchResponseHolder response = webServiceHandler.sendToWebServiceAndParseResponseToDomain(UPDATE_STATUS_SUMMARY_KEY, requestMsg,
                SuperMatchResponseHolderImpl.class, serviceErrors);

        return processResponse(requestMsg.getContext(), response, serviceErrors);
    }

    /**
     * Updates member details in ECO
     *
     * @param customerId       - Customer identifier
     * @param superFundAccount - Super fund account {@link SuperFundAccount}
     * @param serviceErrors    - Object to capture service errors
     */
    @Override
    public boolean createMember(String customerId, SuperFundAccount superFundAccount, ServiceErrors serviceErrors) {
        final ObjectFactory objectFactory = new ObjectFactory();
        final MaintainECOCustomerRequestMsgType requestMsg = objectFactory.createMaintainECOCustomerRequestMsgType();

        requestMsg.setContext(requestBuilder.createRequestContext(objectFactory, customerId));
        requestMsg.setECOAccountDetails(requestBuilder.createCreateMemberRequest(customerId, superFundAccount));
        requestMsg.setChannelDetails(requestBuilder.createChannelDetail(objectFactory));

        final SuperMatchResponseHolder response = webServiceHandler.sendToWebServiceAndParseResponseToDomain(CREATE_MEMBER_KEY, requestMsg,
                SuperMatchResponseHolderImpl.class, serviceErrors);

        processResponse(requestMsg.getContext(), response, serviceErrors);
        return !serviceErrors.hasErrors();
    }

    private List<SuperMatchDetails> processResponse(SMRequestContextType context, SuperMatchResponseHolder response, ServiceErrors serviceErrors) {
        // For failure, set errors in the serviceErrors
        if (STATUS_ERROR.equals(response.getStatus())) {
            final EsbError esbError = response.getError();
            ServiceErrorImpl error = new ServiceErrorImpl();
            error.setErrorCode(esbError.getSubCode());
            error.setReason(esbError.getDescription());
            serviceErrors.addError(error);

            logger.error("Error in Eco service with trackingId:{} - {}", context.getTrackingID(), error);
            return new ArrayList<>();
        }
        return response.getSuperMatchDetails();
    }
}
