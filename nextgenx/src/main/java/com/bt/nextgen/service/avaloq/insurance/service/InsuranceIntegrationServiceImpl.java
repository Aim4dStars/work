package com.bt.nextgen.service.avaloq.insurance.service;


import ch.lambdaj.Lambda;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.UserCacheService;
import com.bt.nextgen.service.avaloq.insurance.model.InsuranceResponseHolder;
import com.bt.nextgen.service.avaloq.insurance.model.InsuranceTrackingResponseHolder;
import com.bt.nextgen.service.avaloq.insurance.model.Policy;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyApplicationTrackingResponseHolder;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyApplications;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyTracking;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyTrackingCustomerResponseHolder;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyUnderwriting;
import com.bt.nextgen.service.btesb.gateway.WebServiceHandler;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.concurrent.AbstractConcurrentComplete;
import com.btfin.panorama.core.concurrent.Concurrent;
import com.btfin.panorama.core.concurrent.ConcurrentCallable;
import com.btfin.panorama.core.concurrent.ConcurrentComplete;
import com.btfin.panorama.core.concurrent.ConcurrentResult;
import com.btfin.panorama.core.validation.Validator;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import ns.btfin_com.product.common.investmentaccount.investmentaccountservice.investmentaccountrequest.v1_0.SearchAccessibleAccountsRequestMsgType;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyreply.v4_2.RetrieveUnderwritingByPolicyNumberResponseMsgType;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyreply.v4_2.SearchPolicyByPaymentAccountResponseMsgType;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyrequest.v4_2.ClientToPolicyRelationshipType;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyrequest.v4_2.RetrievePolicyByPolicyNumberRequestMsgType;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyrequest.v4_2.RetrieveUnderwritingByPolicyNumberRequestMsgType;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyrequest.v4_2.SearchPolicyByAdviserRequestMsgType;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyrequest.v4_2.SearchPolicyByCustomerNumberRequestMsgType;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyrequest.v4_2.SearchPolicyByPaymentAccountRequestMsgType;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyrequest.v4_2.SearchRecentLivesInsuredByAdviserRequestMsgType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

@Service
public class InsuranceIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements PolicyIntegrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InsuranceIntegrationServiceImpl.class);
    private static final String STATUS_ERROR = "Error";
    @Autowired
    public UserCacheService userCacheService;
    @Autowired
    private WebServiceProvider provider;
    @Resource(name = "userDetailsService")
    private BankingAuthorityService userSamlService;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    @Qualifier("btEsbWebServiceHandler")
    private WebServiceHandler webServiceHandler;
    @Autowired
    @Qualifier("cacheAvaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;
    @Autowired
    private Validator validator;

    /**
     * Returns all policies for a given panorama account number
     *
     * @param accountNumber unencoded bp_id
     * @param serviceErrors
     *
     * @return
     */
    @Override
    @Cacheable(key = "{#root.target.getActiveProfileCacheKey(), #accountNumber}", value = "com.bt.nextgen.service.avaloq.insurance.service.InsuranceIntegrationServiceImpl.PoliciesByAccountNumber")
    public List<Policy> retrievePoliciesByAccountNumber(String accountNumber, ServiceErrors serviceErrors) {
        final WrapAccount account = accountIntegrationService.loadWrapAccountWithoutContainers(AccountKey.valueOf(accountNumber),
                serviceErrors);

        SearchPolicyByPaymentAccountRequestMsgType requestMsgType = InsuranceDetailsRequestBuilder.createAccountRequest(account.getAccountNumber(),
                userProfileService.getGcmId(), account.getAccountStructureType());

        final SearchPolicyByPaymentAccountResponseMsgType searchPolicyByPaymentAccountResponse =
                (SearchPolicyByPaymentAccountResponseMsgType) provider.sendWebServiceWithSecurityHeader(userSamlService.getSamlToken(),
                        Attribute.INSURANCE_SEARCH_KEY, requestMsgType);

        final List<Policy> policyList = new PolicyResponseBuilder().getAllPoliciesForAccount(accountNumber, searchPolicyByPaymentAccountResponse, serviceErrors);
        return policyList;

    }

    /**
     * Returns a specific policy given a policy number
     * Uses operation: <code>RetrievePolicyByPolicyNumberRequest</code> of service: <code>PolicyService</code>
     *
     * @param policyNumber  policy number to retrieve
     * @param serviceErrors
     *
     * @return
     */
    @Override
    @Cacheable(key = "{#root.target.getActiveProfileCacheKey(), #policyNumber}", value = "com.bt.nextgen.service.avaloq.insurance.service.InsuranceIntegrationServiceImpl.PolicyByPolicyNumber")
    public List<Policy> retrievePolicyByPolicyNumber(String policyNumber, ServiceErrors serviceErrors) {
        RetrievePolicyByPolicyNumberRequestMsgType requestMsgType = InsuranceDetailsRequestBuilder.createPolicyRequest
                (policyNumber, userProfileService.getGcmId());
        InsuranceResponseHolder insuranceResponseHolder = webServiceHandler.sendToWebServiceAndParseResponseToDomain(
                Attribute.INSURANCE_RETRIEVE_KEY, requestMsgType, InsuranceResponseHolder.class, serviceErrors);
        if (STATUS_ERROR.equals(insuranceResponseHolder.getStatus())) {
            ServiceErrorImpl error = new ServiceErrorImpl();
            error.setReason(insuranceResponseHolder.getError());
            serviceErrors.addError(error);
            return new ArrayList<>();
        }
        validator.validate(insuranceResponseHolder.getPolicyResponse(), serviceErrors);
        return insuranceResponseHolder.getPolicyResponse();
    }


    /**
     * Returns all Fnumbers that are associated with a ppid ("pin and password id" that are given to advisers)
     * Uses operation: <code>SearchAccessibleAccountsRequest</code> of service: <code>investmentaccountservice</code>
     *
     * @param ppid
     * @param serviceErrors
     *
     * @return
     */
    @Override
    @Cacheable(key = "{#root.target.getActiveProfileCacheKey(), #ppid}", value = "com.bt.nextgen.service.avaloq.insurance.service.InsuranceIntegrationServiceImpl.FNumbers")
    public List<PolicyTracking> getFNumbers(String ppid, ServiceErrors serviceErrors) {
        SearchAccessibleAccountsRequestMsgType requestMsgType = InsuranceSummeryRequestBuilder.createPolicyTrackingRequestForFNumbers(ppid, userProfileService.getGcmId());

        InsuranceTrackingResponseHolder insuranceTrackingResponseHolder = webServiceHandler.sendToWebServiceAndParseResponseToDomain(
                Attribute.INVESTMENT_ACCOUNT_KEY, requestMsgType, InsuranceTrackingResponseHolder.class, serviceErrors);
        if (STATUS_ERROR.equals(insuranceTrackingResponseHolder.getStatus())) {
            ServiceErrorImpl error = new ServiceErrorImpl();
            error.setReason(insuranceTrackingResponseHolder.getError());
            serviceErrors.addError(error);
            return new ArrayList<>();
        }
        return insuranceTrackingResponseHolder.getPolicyTrackingResponse();
    }


    /**
     * Returns all policies that belong under a specific adviser.<p>
     * Uses operation: <code>SearchPolicyByAdviserRequest</code> of service: <code>PolicyService</code>
     *
     * @param fNumber       fnumber that has been assigned to an adviser - these are generated in wrap
     * @param serviceErrors
     *
     * @return
     */
    @Override
    @Cacheable(key = "{#root.target.getActiveProfileCacheKey(), #fNumber}", value = "com.bt.nextgen.service.avaloq.insurance.service.InsuranceIntegrationServiceImpl.PoliciesByAdviser")
    public List<PolicyTracking> getPoliciesForAdviser(String fNumber, ServiceErrors serviceErrors) {
        SearchPolicyByAdviserRequestMsgType requestMsgType = InsuranceSummeryRequestBuilder.createPolicyTrackingRequest(fNumber, userProfileService.getGcmId());

        InsuranceTrackingResponseHolder insuranceTrackingResponseHolder = webServiceHandler.sendToWebServiceAndParseResponseToDomain(
                Attribute.INSURANCE_SEARCH_BY_ADVISER_KEY, requestMsgType, InsuranceTrackingResponseHolder.class, serviceErrors);
        if (STATUS_ERROR.equals(insuranceTrackingResponseHolder.getStatus())) {
            ServiceErrorImpl error = new ServiceErrorImpl();
            error.setReason(insuranceTrackingResponseHolder.getError());
            serviceErrors.addError(error);
            return new ArrayList<>();
        }
        return insuranceTrackingResponseHolder.getPolicyTrackingResponse();
    }

    /**
     * Returns all policies that belong to a specific customer.<p>
     * Uses operation: <code>SearchPolicyByCustomerNumberRequest</code> of service: <code>PolicyService</code>
     *
     * @param fNumbers
     * @param customerNumber
     * @param serviceErrors
     *
     * @return
     */
    @Override
    @Cacheable(key = "{#root.target.getActiveProfileCacheKey(), #customerNumber, #root.target.getFnumbers(#fNumbers)}", value = "com.bt.nextgen.service.avaloq.insurance.service.InsuranceIntegrationServiceImpl.PolicyByCustomerNumber")
    public List<PolicyTracking> getPolicyByCustomerNumber(List<String> fNumbers, String customerNumber, ServiceErrors serviceErrors) {
        List<PolicyTracking> policyTrackings = new ArrayList<>();
        Concurrent.when(3, loadPolicyTrackings(fNumbers, customerNumber, ClientToPolicyRelationshipType.POLICY_OWNER, serviceErrors),
                loadPolicyTrackings(fNumbers, customerNumber, null, serviceErrors))
                .done(processResults(policyTrackings)).execute();
        return policyTrackings;
    }

    private ConcurrentCallable<List<PolicyTracking>> loadPolicyTrackings(final List<String> fNumbers,
                                                                         final String customerNumber,
                                                                         final ClientToPolicyRelationshipType relationshipType,
                                                                         final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<List<PolicyTracking>>() {
            @Override
            public List<PolicyTracking> call() {
                SearchPolicyByCustomerNumberRequestMsgType requestMsgType =
                        InsuranceTrackingRequestBuilder.createSearchPolicyByCustomerNumberRequest(fNumbers, customerNumber,
                                userProfileService.getGcmId(), relationshipType);
                PolicyTrackingCustomerResponseHolder policyTrackingCustomerResponseHolder = webServiceHandler.sendToWebServiceAndParseResponseToDomain(
                        Attribute.INSURANCE_SEARCH_BY_CUSTOMER_NUMBER, requestMsgType, PolicyTrackingCustomerResponseHolder.class, serviceErrors);
                if (STATUS_ERROR.equals(policyTrackingCustomerResponseHolder.getStatus())) {
                    ServiceErrorImpl error = new ServiceErrorImpl();
                    error.setReason(policyTrackingCustomerResponseHolder.getError());
                    serviceErrors.addError(error);
                    return new ArrayList<>();
                }
                return policyTrackingCustomerResponseHolder.getPolicyTrackingResponse();
            }
        };
    }

    private ConcurrentComplete processResults(final List<PolicyTracking> results) {
        return new AbstractConcurrentComplete() {
            @Override
            public void run() {
                List<? extends ConcurrentResult<?>> r = this.getResults();
                List<PolicyTracking> allPolicies = new ArrayList<>();
                for (ConcurrentResult concurrentResult : r) {
                    List<PolicyTracking> policyTrackings = (List<PolicyTracking>) concurrentResult.getResult();
                    if (policyTrackings != null) {
                        allPolicies.addAll(policyTrackings);
                    }
                }
                results.addAll(Lambda.<PolicyTracking>selectDistinct(allPolicies, "policyNumber"));
            }
        };
    }

    /**
     * Returns all policy insurances under process of an adviser. InForce policies are not returned after a specific period(60 days)<p>
     * Uses operation: <code>SearchRecentLivesInsuredByAdviserRequest</code> of service: <code>PolicyService</code>
     *
     * @param fNumbers
     * @param serviceErrors
     *
     * @return
     */
    @Override
    @Cacheable(key = "{#root.target.getActiveProfileCacheKey(), #root.target.getFnumbers(#fNumbers)}", value = "com.bt.nextgen.service.avaloq.insurance.service.InsuranceIntegrationServiceImpl.RecentLivesInsuredPolicies")
    public List<PolicyApplications> getRecentLivesInsured(List<String> fNumbers, ServiceErrors serviceErrors) {
        SearchRecentLivesInsuredByAdviserRequestMsgType requestMsgType =
                InsuranceTrackingRequestBuilder.createSearchRecentLivesInsuredByAdviserRequest(fNumbers, userProfileService.getGcmId());

        PolicyApplicationTrackingResponseHolder policyApplicationTrackingResponseHolder = webServiceHandler.sendToWebServiceAndParseResponseToDomain(
                Attribute.INSURANCE_RECENT_LIVES_BY_ADVISER, requestMsgType, PolicyApplicationTrackingResponseHolder.class, serviceErrors);
        if (STATUS_ERROR.equals(policyApplicationTrackingResponseHolder.getStatus())) {
            ServiceErrorImpl error = new ServiceErrorImpl();
            error.setReason(policyApplicationTrackingResponseHolder.getError());
            serviceErrors.addError(error);
            return new ArrayList<>();
        }
        return policyApplicationTrackingResponseHolder.getPolicyApplicationsResponse();
    }

    @Override
    public PolicyUnderwriting getUnderwritingNotes(String policyNumber, ServiceErrors serviceErrors) {
        RetrieveUnderwritingByPolicyNumberRequestMsgType requestMsgType =
                InsuranceTrackingRequestBuilder.createRetrieveUnderwritingByPolicyNumberRequest(policyNumber, userProfileService.getGcmId());

        RetrieveUnderwritingByPolicyNumberResponseMsgType underwritingByPolicyNumberResponse =
                (RetrieveUnderwritingByPolicyNumberResponseMsgType) provider.sendWebServiceWithSecurityHeader(userSamlService.getSamlToken(),
                        Attribute.INSURANCE_UNDERWRITING_NOTES, requestMsgType);
        return new InsuranceResponseBuilder().getUnderwritingDetails(underwritingByPolicyNumberResponse, serviceErrors);
    }

    public String getActiveProfileCacheKey() {
        return userCacheService.getActiveProfileCacheKey();
    }

    /**
     * Return comman separated fnumbers
     *
     * @param fnumbersList
     *
     * @return
     */

    public String getFnumbers(List<String> fnumbersList) {
        String fnumbers = StringUtils.join(fnumbersList, ",");
        return fnumbers;
    }
}