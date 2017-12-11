package com.bt.nextgen.service.avaloq.insurance.service;

import ns.btfin_com.product.common.investmentaccount.investmentaccountservice.investmentaccountrequest.v1_0.AccessibleAccountIDIssuerType;
import ns.btfin_com.product.common.investmentaccount.investmentaccountservice.investmentaccountrequest.v1_0.InvestmentAccountRequestContextType;
import ns.btfin_com.product.common.investmentaccount.investmentaccountservice.investmentaccountrequest.v1_0.SearchAccessibleAccountsRequestMsgType;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyrequest.v4_2.ObjectFactory;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyrequest.v4_2.SearchPolicyByAdviserRequestMsgType;
import ns.btfin_com.sharedservices.utilities.identitymanagement.authentication.authenticationservice.authenticationrequest.v1_0.LoginIDIssuerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.GregorianCalendar;
import java.util.UUID;

public class InsuranceSummeryRequestBuilder extends InsuranceRequestBuilder{

    private static final Logger logger = LoggerFactory.getLogger(InsuranceSummeryRequestBuilder.class);

    private static final String PANORAMA_SUBMITTER = "PANORAMA";

    private static final String TRACKING_VERSION = "1_0";
    private static final String TRACKING_REQUESTING_SYSTEM = "PUBLIC";

    /**
     * Create request message for retrieving fnumbers
     *
     * @param ppid
     * @param requesterGcmId gcm_id of the currently logged in person performing this request
     * @return
     */
    public static SearchAccessibleAccountsRequestMsgType createPolicyTrackingRequestForFNumbers(String ppid, String requesterGcmId) {
        ns.btfin_com.product.common.investmentaccount.investmentaccountservice.investmentaccountrequest.v1_0.ObjectFactory objectFactory =
                new ns.btfin_com.product.common.investmentaccount.investmentaccountservice.investmentaccountrequest.v1_0.ObjectFactory();

        InvestmentAccountRequestContextType context = objectFactory.createInvestmentAccountRequestContextType();
        context.setVersion(TRACKING_VERSION);
        context.setResponseVersion(TRACKING_VERSION);
        context.setSubmitter(PANORAMA_SUBMITTER);
        context.setRequester(requesterGcmId);
        context.setRequestingSystem(TRACKING_REQUESTING_SYSTEM);
        try {
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
            XMLGregorianCalendar creationDate = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
            context.setCreationDateTime(creationDate);
        } catch (DatatypeConfigurationException e) {
            logger.warn("Error while getting current date" + e);
        }
        context.setTrackingID(UUID.randomUUID().toString());

        SearchAccessibleAccountsRequestMsgType requestMsgType = objectFactory.createSearchAccessibleAccountsRequestMsgType();
        requestMsgType.setContext(context);
        requestMsgType.setLoginID(ppid);
        requestMsgType.setLoginIDIssuer(LoginIDIssuerType.PINS_AND_PASSWORDS);
        requestMsgType.setAccountIssuer(AccessibleAccountIDIssuerType.WRAP);

        return requestMsgType;
    }

    public static SearchPolicyByAdviserRequestMsgType createPolicyTrackingRequest(String fNumber, String requesterGcmId) {
        ObjectFactory objectFactory = new ObjectFactory();

        SearchPolicyByAdviserRequestMsgType requestMsgType = objectFactory.createSearchPolicyByAdviserRequestMsgType();
        requestMsgType.setAdviserNumber(fNumber);
        requestMsgType.setContext(getPolicyRequestContext(requesterGcmId));
        return requestMsgType;
    }
}
