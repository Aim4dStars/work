package com.bt.nextgen.api.policy.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.policy.model.CustomerKey;
import com.bt.nextgen.api.policy.model.PolicyTrackingIdentifier;
import com.bt.nextgen.api.policy.model.PolicyUnderwritingDto;
import com.bt.nextgen.api.policy.util.PolicyUtil;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyApplications;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyTracking;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyUnderWritingNotesImpl;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyUnderwriting;
import com.bt.nextgen.service.avaloq.insurance.service.PolicyIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.concurrent.AbstractConcurrentComplete;
import com.btfin.panorama.core.concurrent.Concurrent;
import com.btfin.panorama.core.concurrent.ConcurrentCallable;
import com.btfin.panorama.core.concurrent.ConcurrentComplete;
import com.btfin.panorama.core.concurrent.ConcurrentResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.core.IsEqual.*;

/**
 * Service created for Policy tracking screen
 */
@Service
public class PolicyTrackingDtoServiceImpl implements PolicyTrackingDtoService {

    private static final int THREAD_POOL_SIZE = 3;

    @Autowired
    private PolicyIntegrationService policyIntegrationService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Autowired
    private PolicyUserPreferencesService policyUserPreferencesService;

    @Autowired
    private PolicyUtility policyUtility;

    @Override
    public List<PolicyTrackingIdentifier> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        String fNumber = null;
        String brokerId = null;
        for (ApiSearchCriteria searchCriteria : criteriaList) {
            if (Attribute.INSURANCE_FNUMBER.equals(searchCriteria.getProperty())) {
                fNumber = searchCriteria.getValue();
            }
            else if (Attribute.BROKER_ID.equals(searchCriteria.getProperty())) {
                brokerId = searchCriteria.getValue();
            }
        }
        List<String> fNumbers = PolicyUtil.getSortedFNumbers(fNumber);

        if (isFNumbersValid(fNumbers, brokerId, serviceErrors)) {
            List<PolicyApplications> insurancesTrackings = policyIntegrationService.getRecentLivesInsured(fNumbers, serviceErrors);
            saveLastAccessedDetails(fNumbers, brokerId);
            return PolicyTrackingDtoConverter.policyApplicationTrackingDtos(insurancesTrackings);
        }
        else {
            throw new IllegalArgumentException("FNumber is not valid for user");
        }
    }

    private void saveLastAccessedDetails(List<String> fNumbers, String brokerId) {
        if (CollectionUtils.isNotEmpty(fNumbers) && fNumbers.size() == 1) {
            policyUserPreferencesService.saveLastAccessedFNumber(fNumbers.get(0));
        }
        policyUserPreferencesService.saveLastAccessedAdviser(brokerId);
    }

    @Override
    public List<PolicyTrackingIdentifier> search(CustomerKey key, List<ApiSearchCriteria> criteria, ServiceErrors serviceErrors) {
        String fNumber = null;
        String brokerId = null;
        for (ApiSearchCriteria searchCriteria : criteria) {
            if (Attribute.INSURANCE_FNUMBER.equals(searchCriteria.getProperty())) {
                fNumber = searchCriteria.getValue();
            }
            else if (Attribute.BROKER_ID.equals(searchCriteria.getProperty())) {
                brokerId = searchCriteria.getValue();
            }
        }
        List<String> fNumbers = PolicyUtil.getSortedFNumbers(fNumber);
        String customerNumber = new EncodedString(key.getCustomerNumber()).plainText();
        List<PolicyApplications> applications = getRecentApplications(fNumbers, brokerId, customerNumber, serviceErrors);
        if (applications != null) {
            List<PolicyTracking> policyTrackings = policyIntegrationService.getPolicyByCustomerNumber(fNumbers, customerNumber, serviceErrors);
            policyTrackings = PolicyUtil.getCustomerRecentInsurances(customerNumber, policyTrackings, applications);
            Map<String, PolicyTracking> policyMap = Lambda.index(policyTrackings, Lambda.on(PolicyTracking.class).getPolicyNumber());
            List<PolicyTrackingIdentifier> policyTrackingIdentifiers = new ArrayList<>();
            if (!policyMap.isEmpty()) {
                List<ConcurrentCallable<?>> concurrentCallables = new ArrayList<>();
                for (String policyNumber : policyMap.keySet()) {
                    concurrentCallables.add(loadUnderwritingNotes(policyNumber,serviceErrors));
                }
                Concurrent.when(THREAD_POOL_SIZE, concurrentCallables.toArray(new ConcurrentCallable<?>[0]))
                        .done(processResults(policyTrackingIdentifiers, policyMap, brokerId, serviceErrors)).execute();
            }
            return policyTrackingIdentifiers;
        }
        else {
            throw new IllegalArgumentException("Requested parameters are not valid");
        }
    }

    private boolean isFNumbersValid(List<String> reqeustFNumbers, String brokerId, ServiceErrors serviceErrors) {
        List<PolicyTracking> policyTrackingList = new ArrayList<>();
        // Get AdviserPPID from policyUtility generic method
        final String adviserPpId = policyUtility.getAdviserPpId(brokerId, serviceErrors);
        if (StringUtils.isNotEmpty(adviserPpId)) {
            policyTrackingList = policyIntegrationService.getFNumbers(adviserPpId, serviceErrors);
        }

        List<String> validFNumbersForUser = Lambda.collect(policyTrackingList, Lambda.on(PolicyTracking.class).getFNumber());

        boolean isValid = false;
        for (String requestedFNumber : reqeustFNumbers) {
            if (validFNumbersForUser.contains(requestedFNumber)) {
                isValid = true;
            }
            else {
                isValid = false;
                break;
            }
        }
        return isValid;
    }

    private List<PolicyApplications> getRecentApplications(List<String> reqeustFNumbers, String brokerId, String customerNumber, ServiceErrors serviceErrors) {
        if (isFNumbersValid(reqeustFNumbers, brokerId, serviceErrors)) {
            List<PolicyApplications> insurancesTrackings = policyIntegrationService.getRecentLivesInsured(reqeustFNumbers, serviceErrors);
            return Lambda.filter(Lambda.having(on(PolicyApplications.class).getCustomerNumber(), equalTo(customerNumber)), insurancesTrackings);
        }
        return null;
    }

    private ConcurrentCallable<PolicyUnderwriting> loadUnderwritingNotes(final String policyNumber, final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<PolicyUnderwriting>() {
            @Override
            public PolicyUnderwriting call() {
                return policyIntegrationService.getUnderwritingNotes(policyNumber, serviceErrors);
            }
        };
    }

    private ConcurrentComplete processResults(final List<PolicyTrackingIdentifier> results,
                                              final Map<String, PolicyTracking> policyTrackings,
                                              final String brokerId,
                                              final ServiceErrors serviceErrors) {
        return new AbstractConcurrentComplete() {
            @Override
            public void run() {
                List<? extends ConcurrentResult<?>> r = this.getResults();
                List<PolicyTracking> policyDetails = new ArrayList<>();
                List<PolicyUnderWritingNotesImpl> underWritingNotes = new ArrayList<>();
                for (ConcurrentResult concurrentResult : r) {
                    PolicyUnderwriting policyUnderwriting = (PolicyUnderwriting)concurrentResult.getResult();
                    if (policyUnderwriting.getPolicyDetails() != null)
                        policyDetails.addAll(policyUnderwriting.getPolicyDetails());
                    if (policyUnderwriting.getUnderWritingNotes() != null)
                        underWritingNotes.addAll(policyUnderwriting.getUnderWritingNotes());
                }

                Map<AccountKey, WrapAccount> accounts = accountIntegrationService.loadWrapAccountWithoutContainers(serviceErrors);
                PolicyUnderwritingDto underwritingDto = PolicyTrackingDtoConverter.getUnderwritingDetails(policyDetails, underWritingNotes, policyTrackings,accounts);
                underwritingDto.setAdviserName(getBrokerUser(brokerId, serviceErrors).getFullName());
                results.add(underwritingDto);
            }

            private BrokerUser getBrokerUser(String brokerId, ServiceErrors serviceErrors) {
                if (brokerId != null ) {
                    String plaintextBrokerId = new ConsistentEncodedString(brokerId).plainText();
                    return brokerIntegrationService.getAdviserBrokerUser(BrokerKey.valueOf(plaintextBrokerId), serviceErrors);
                }
                else {
                    return brokerIntegrationService.getBrokerUser(userProfileService.getActiveProfile(), serviceErrors);
                }
            }
        };
    }
}
