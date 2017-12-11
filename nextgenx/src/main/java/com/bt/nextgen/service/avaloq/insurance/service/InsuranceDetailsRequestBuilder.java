package com.bt.nextgen.service.avaloq.insurance.service;

import com.bt.nextgen.service.integration.account.AccountStructureType;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyrequest.v4_2.ObjectFactory;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyrequest.v4_2.RequestPolicyPaymentMethodType;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyrequest.v4_2.RetrievePolicyByPolicyNumberRequestMsgType;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyrequest.v4_2.SearchPolicyByPaymentAccountRequestMsgType;
import ns.btfin_com.product.insurance.lifeinsuranceservice.policy.v4_2.NameOfInstitutionType;
import ns.btfin_com.sharedservices.common.payment.v1_1.FirstAgentIdentificationType;
import ns.btfin_com.sharedservices.common.payment.v1_1.WorkingCashAccountType;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class InsuranceDetailsRequestBuilder extends InsuranceRequestBuilder{

    private static final String ACCOUNTNUMBER_XMLELEMENT = "AccountNumber";

    private static final String PANORAMA_IP_BSB = "262-786";

    private static final String PANORAMA_SUPER_BSB = "262-750";

    private InsuranceDetailsRequestBuilder()
    {
    }

    /**
     * Create request message for retrieving policies by policy number service
     *
     * @param policyNumber
     * @param requesterGcmId gcm_id of the currently logged in person performing this request
     * @return
     */
    public static RetrievePolicyByPolicyNumberRequestMsgType createPolicyRequest(String policyNumber, String requesterGcmId) {
        ObjectFactory objectFactory = new ObjectFactory();

        RetrievePolicyByPolicyNumberRequestMsgType requestMsgType = objectFactory.createRetrievePolicyByPolicyNumberRequestMsgType();
        requestMsgType.setContext(getPolicyRequestContext(requesterGcmId));
        requestMsgType.setPolicyNumber(policyNumber);

        return requestMsgType;
    }

    /**
     * Create request message for retrieving policies by account number
     *
     * @param accountNumber
     * @param requesterGcmId gcm_id of the currently logged in person performing this request
     * @return
     */
    public static SearchPolicyByPaymentAccountRequestMsgType createAccountRequest(String accountNumber, String requesterGcmId, AccountStructureType accountStructure) {
        ns.btfin_com.sharedservices.common.payment.v1_1.ObjectFactory paymentObjectFactory = new ns.btfin_com.sharedservices.common.payment.v1_1.ObjectFactory();

        WorkingCashAccountType workingCashAccountType = paymentObjectFactory.createWorkingCashAccountType();
        JAXBElement<String> inputAccountNumber = new JAXBElement<String>(new QName(ACCOUNTNUMBER_XMLELEMENT), String.class, accountNumber);
        workingCashAccountType.setAccountNumber(inputAccountNumber);
        workingCashAccountType.setPaymentType(ns.btfin_com.sharedservices.common.payment.v1_1.PaymentTypeCode.WCACC);

        FirstAgentIdentificationType firstAgentIdentificationType = paymentObjectFactory.createFirstAgentIdentificationType();
        firstAgentIdentificationType.setNameOfInstitution(NameOfInstitutionType.PANORAMA.value());

        String bsb = (accountStructure.equals(AccountStructureType.SUPER) ? PANORAMA_SUPER_BSB : PANORAMA_IP_BSB);

        firstAgentIdentificationType.setBSB(bsb);
        workingCashAccountType.setFirstAgentIdentification(firstAgentIdentificationType);

        ObjectFactory policyObjectFactory = new ObjectFactory();
        RequestPolicyPaymentMethodType methodType = policyObjectFactory.createRequestPolicyPaymentMethodType();
        methodType.setWorkingCashAccount(workingCashAccountType);

        SearchPolicyByPaymentAccountRequestMsgType requestMsgType = policyObjectFactory.createSearchPolicyByPaymentAccountRequestMsgType();
        requestMsgType.setPaymentMethod(methodType);

        requestMsgType.setContext(getPolicyRequestContext(requesterGcmId));

        return requestMsgType;
    }
}
