/**
 *
 */
package com.bt.nextgen.service.onboarding.btesb;

import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.onboarding.FirstTimeRegistrationResponse;
import ns.btfin_com.party.v3_0.CustomerIdentifier;
import ns.btfin_com.party.v3_0.CustomerNoAllIssuerType;
import ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.OTPPartyDetailsType;
import ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.StatusTypeCode;
import ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.ValidatePartyRegistrationResponseMsgType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingresponse.v3_0.ValidatePartySMSOneTimePasswordChallengeResponseMsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static ns.btfin_com.product.panorama.onboardingservice.onboardingresponse.v3_0.StatusTypeCode.SUCCESS;

/**
 * @author L055011
 */
public class ValidatePartyAndSmsAdapter extends ResponseAdapter implements FirstTimeRegistrationResponse {
    private static final Logger logger = LoggerFactory.getLogger(ValidatePartyAndSmsAdapter.class);

    private String sessionId;
    private String transactionId;
    private String deviceToken;
    private String userName;
    private String deviceId;
    private String zNumber;
    private String cisKey;
    private OTPPartyDetailsType invalidPartyDetails;

    public ValidatePartyAndSmsAdapter() {
        setServiceErrors(new ServiceErrorsImpl());
    }

    public ValidatePartyAndSmsAdapter(ValidatePartyRegistrationResponseMsgType response) {
        this();

        if (response.getStatus() == StatusTypeCode.SUCCESS) {
            if (null != response.getResponseDetails().getSuccessResponse().getCustomerIdentifiers()) {
                initFromCustomerIdentifiers(response.getResponseDetails().getSuccessResponse().getCustomerIdentifiers().getCustomerIdentifier());
            }
            this.deviceId = response.getResponseDetails().getSuccessResponse().getMFAArrangementID();
            this.setInvalidPartyDetails(response.getResponseDetails().getSuccessResponse().getInvalidPartyDetails());
        } else {
            setServiceErrors(response.getResponseDetails().getErrorResponses().getErrorResponse());
        }
    }

    /**
     * @param jaxbResponse
     */
    public ValidatePartyAndSmsAdapter(ValidatePartySMSOneTimePasswordChallengeResponseMsgType jaxbResponse) {
        this();

        if (null != jaxbResponse) {
            if (jaxbResponse.getStatus() == SUCCESS) {
                createSuccessResponse(jaxbResponse);
            } else {
                setServiceErrors(jaxbResponse.getResponseDetails().getErrorResponses().getErrorResponse());
            }
        }
    }

    public ValidatePartyAndSmsAdapter(ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.ValidatePartySMSOneTimePasswordChallengeResponseMsgType response) {
        this();

        if (null != response) {
            if (response.getStatus() == StatusTypeCode.SUCCESS) {
                createSuccessResponse(response);
            } else {
                setServiceErrors(response.getResponseDetails().getErrorResponses().getErrorResponse());
            }
        }
    }

    /**
     * method creating a success pojo response
     *
     * @param jaxbResponse
     */
    @SuppressWarnings("  checkstyle:com.puppycrawl.tools.checkstyle.checks.coding.IllegalCatchCheck  ")
    private void createSuccessResponse(ValidatePartySMSOneTimePasswordChallengeResponseMsgType jaxbResponse) {
        this.sessionId = jaxbResponse.getResponseDetails().getSuccessResponse().getDeviceDetails().getSessionID();
        this.transactionId = jaxbResponse.getResponseDetails().getSuccessResponse().getDeviceDetails().getTransactionID();
        this.deviceToken = jaxbResponse.getResponseDetails().getSuccessResponse().getDeviceDetails().getDeviceTokenCookie();
        logger.info("ValidatePartyAndSmsAdapter.createSuccessResponse(): Going to all customer identifiers returned by ValidatePartySMSOneTimePasswordChallengeResponse");
        initFromCustomerIdentifiers(jaxbResponse.getResponseDetails().getSuccessResponse().getCustomerIdentifiers().getCustomerIdentifier());
        this.deviceId = jaxbResponse.getResponseDetails().getSuccessResponse().getArrangementID();
    }

    private void createSuccessResponse(ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.ValidatePartySMSOneTimePasswordChallengeResponseMsgType jaxbResponse) {
        this.sessionId = jaxbResponse.getResponseDetails().getSuccessResponse().getDeviceDetails().getSessionID();
        this.transactionId = jaxbResponse.getResponseDetails().getSuccessResponse().getDeviceDetails().getTransactionID();
        this.deviceToken = jaxbResponse.getResponseDetails().getSuccessResponse().getDeviceDetails().getDeviceTokenCookie();
        logger.info("ValidatePartyAndSmsAdapter.createSuccessResponse(): Going to all customer identifiers returned by ValidatePartySMSOneTimePasswordChallengeResponse");
        initFromCustomerIdentifiers(jaxbResponse.getResponseDetails().getSuccessResponse().getCustomerIdentifiers().getCustomerIdentifier());
        this.deviceId = jaxbResponse.getResponseDetails().getSuccessResponse().getMFAArrangementID();
    }

    private void initFromCustomerIdentifiers(List<CustomerIdentifier> customerIdentifiers) {
        try {
            for (CustomerIdentifier customerIdentifier : customerIdentifiers) {
                if (customerIdentifier.getCustomerNumberIdentifier().getCustomerNumberIssuer().equals(CustomerNoAllIssuerType.BT_PANORAMA)) {
                    logger.info("Customer Issuer Type found is: {}", CustomerNoAllIssuerType.BT_PANORAMA);
                    this.userName = customerIdentifier.getCustomerNumberIdentifier().getCustomerNumber();
                    logger.info("Customer Number is: {}", customerIdentifier.getCustomerNumberIdentifier().getCustomerNumber());
                } else if (customerIdentifier.getCustomerNumberIdentifier().getCustomerNumberIssuer().equals(CustomerNoAllIssuerType.WESTPAC)) {
                    logger.info("Customer Issuer Type found is: {}", CustomerNoAllIssuerType.WESTPAC);
                    this.zNumber = customerIdentifier.getCustomerNumberIdentifier().getCustomerNumber();
                    logger.info("Z-Number is: {}", customerIdentifier.getCustomerNumberIdentifier().getCustomerNumber());
                } else if (customerIdentifier.getCustomerNumberIdentifier().getCustomerNumberIssuer().equals(CustomerNoAllIssuerType.WESTPAC_LEGACY)) {
                    logger.info("Customer Issuer Type found is: {}", CustomerNoAllIssuerType.WESTPAC_LEGACY);
                    this.cisKey = customerIdentifier.getCustomerNumberIdentifier().getCustomerNumber();
                    logger.info("CIS Key is: {}", customerIdentifier.getCustomerNumberIdentifier().getCustomerNumber());
                }
            }
        } catch (Exception ex) {
            logger.error("Problem in loading the customer number or z-number from response.", ex);
        }
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.btesb.FirstTimeRegistrationResponse#getSessionId()
     */
    @Override
    public String getSessionId() {
        return sessionId;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.btesb.FirstTimeRegistrationResponse#setSessionId(java.lang.String)
     */
    @Override
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.btesb.FirstTimeRegistrationResponse#getTransactionId()
     */
    @Override
    public String getTransactionId() {
        return transactionId;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.btesb.FirstTimeRegistrationResponse#setTransactionId(java.lang.String)
     */
    @Override
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.btesb.FirstTimeRegistrationResponse#getDeviceToken()
     */
    @Override
    public String getDeviceToken() {
        return deviceToken;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.btesb.FirstTimeRegistrationResponse#setDeviceToken(java.lang.String)
     */
    @Override
    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.btesb.FirstTimeRegistrationResponse#getUserName()
     */
    @Override
    public String getUserName() {
        return userName;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.btesb.FirstTimeRegistrationResponse#setUserName(java.lang.String)
     */
    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.btesb.FirstTimeRegistrationResponse#getDeviceId()
     */
    @Override
    public String getDeviceId() {
        return deviceId;
    }

    /* (non-Javadoc)
     * @see com.bt.nextgen.service.onboarding.btesb.FirstTimeRegistrationResponse#setDeviceId(java.lang.String)
     */
    @Override
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getzNumber() {
        return zNumber;
    }

    public void setzNumber(String zNumber) {
        this.zNumber = zNumber;
    }

    public String getCisKey() {
        return cisKey;
    }

    public void setCisKey(String cisKey) {
        this.cisKey = cisKey;
    }

    public OTPPartyDetailsType getInvalidPartyDetails() {
        return invalidPartyDetails;
    }

    public void setInvalidPartyDetails(OTPPartyDetailsType invalidPartyDetails) {
        this.invalidPartyDetails = invalidPartyDetails;
    }
}
