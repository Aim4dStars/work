package com.bt.nextgen.service.avaloq.insurance.service;

import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyrequest.v4_2.ClientToPolicyRelationshipType;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyrequest.v4_2.ObjectFactory;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyrequest.v4_2.PolicyByPolicyPartySearchFilterExtType;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyrequest.v4_2.PolicyByPolicyPartySearchFilterType;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyrequest.v4_2.RecentLivesInsuredByAdviserSearchFilterExtType;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyrequest.v4_2.RecentLivesInsuredByAdviserSearchFilterType;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyrequest.v4_2.RetrieveUnderwritingByPolicyNumberRequestMsgType;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyrequest.v4_2.SearchPolicyByCustomerNumberRequestMsgType;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyrequest.v4_2.SearchRecentLivesInsuredByAdviserRequestMsgType;
import ns.btfin_com.product.insurance.lifeinsuranceservice.policy.v4_2.LifeInsuranceCustomerNumberIssuerType;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.GregorianCalendar;
import java.util.List;

public class InsuranceTrackingRequestBuilder extends InsuranceRequestBuilder {

    private static final Logger logger = LoggerFactory.getLogger(InsuranceTrackingRequestBuilder.class);

    private InsuranceTrackingRequestBuilder() {
    }

    /**
     * Request Message generation for SearchPolicyByCustomerNumberRequest
     *
     * @param fNumbers
     * @param customerNumber
     * @param requesterGcmId
     *
     * @return
     */
    public static SearchPolicyByCustomerNumberRequestMsgType createSearchPolicyByCustomerNumberRequest(List<String> fNumbers, String customerNumber, String requesterGcmId, ClientToPolicyRelationshipType relationshipType) {
        ObjectFactory objectFactory = new ObjectFactory();
        SearchPolicyByCustomerNumberRequestMsgType requestMsgType = objectFactory.createSearchPolicyByCustomerNumberRequestMsgType();
        requestMsgType.setContext(getPolicyRequestContext(requesterGcmId));

        PolicyByPolicyPartySearchFilterType.Advisers advisers = new PolicyByPolicyPartySearchFilterType.Advisers();
        List<String> adviserList = advisers.getAdviserNumber();
        adviserList.addAll(fNumbers); //Not more than 10 Fnumbers - validation ?
        PolicyByPolicyPartySearchFilterExtType policyByPolicyPartySearchFilterExtType = objectFactory.createPolicyByPolicyPartySearchFilterExtType();
        policyByPolicyPartySearchFilterExtType.setCustomerNumber(customerNumber);
        policyByPolicyPartySearchFilterExtType.setCustomerNumberIssuer(LifeInsuranceCustomerNumberIssuerType.CLOAS);
        PolicyByPolicyPartySearchFilterType policyByPolicyPartySearchFilterType = objectFactory.createPolicyByPolicyPartySearchFilterType();
        policyByPolicyPartySearchFilterType.setPolicyPartyFilter(policyByPolicyPartySearchFilterExtType);
        policyByPolicyPartySearchFilterType.setAdvisers(advisers);
        if (relationshipType != null) {
            policyByPolicyPartySearchFilterType.setClientToPolicyRelationship(relationshipType);
        }
        requestMsgType.setSearchFilter(policyByPolicyPartySearchFilterType);
        return requestMsgType;
    }

    /**
     * Request Message generation for SearchRecentLivesInsuredByAdviserRequest
     *
     * @param fNumbers
     * @param requesterGcmId
     *
     * @return
     */
    public static SearchRecentLivesInsuredByAdviserRequestMsgType createSearchRecentLivesInsuredByAdviserRequest(List<String> fNumbers,
                                                                                                                 String requesterGcmId) {
        ObjectFactory objectFactory = new ObjectFactory();
        SearchRecentLivesInsuredByAdviserRequestMsgType requestMsgType = objectFactory.createSearchRecentLivesInsuredByAdviserRequestMsgType();
        requestMsgType.setContext(getPolicyRequestContext(requesterGcmId));
        RecentLivesInsuredByAdviserSearchFilterExtType recentLivesInsuredByAdviserSearchFilterExtType = objectFactory.createRecentLivesInsuredByAdviserSearchFilterExtType();
        recentLivesInsuredByAdviserSearchFilterExtType.setInForceOrDeclinedFromDate(getXMLGregorianCalenderDate(new DateTime().minusDays(60)));
        recentLivesInsuredByAdviserSearchFilterExtType.setInForceOrDeclinedToDate(getXMLGregorianCalenderDate(new DateTime()));
        RecentLivesInsuredByAdviserSearchFilterType policyByPolicyPartySearchFilterExtType = objectFactory.createRecentLivesInsuredByAdviserSearchFilterType();
        RecentLivesInsuredByAdviserSearchFilterType.Advisers advisers = new RecentLivesInsuredByAdviserSearchFilterType.Advisers();
        List<String> adviserList = advisers.getAdviserNumber();
        adviserList.addAll(fNumbers); //Not more than 10 Fnumbers - validation ?
        policyByPolicyPartySearchFilterExtType.setAdvisers(advisers);
        policyByPolicyPartySearchFilterExtType.setPolicyFilter(recentLivesInsuredByAdviserSearchFilterExtType);
        requestMsgType.setSearchFilter(policyByPolicyPartySearchFilterExtType);
        return requestMsgType;
    }

    public static RetrieveUnderwritingByPolicyNumberRequestMsgType createRetrieveUnderwritingByPolicyNumberRequest(String policyNumBer,
                                                                                                                   String requesterGcmId) {
        ObjectFactory objectFactory = new ObjectFactory();
        RetrieveUnderwritingByPolicyNumberRequestMsgType underwritingByPolicyNumberRequestMsgType =
                objectFactory.createRetrieveUnderwritingByPolicyNumberRequestMsgType();
        underwritingByPolicyNumberRequestMsgType.setContext(getPolicyRequestContext(requesterGcmId));
        underwritingByPolicyNumberRequestMsgType.setPolicyNumber(policyNumBer);
        return underwritingByPolicyNumberRequestMsgType;
    }

    private static XMLGregorianCalendar getXMLGregorianCalenderDate(DateTime dateTime) {
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(dateTime.getMillis());
        XMLGregorianCalendar xmlGregorianCalendar = null;
        try {
            xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        }
        catch (DatatypeConfigurationException e) {
            logger.warn("Error while converting date" + e);
        }
        return xmlGregorianCalendar;
    }
}
