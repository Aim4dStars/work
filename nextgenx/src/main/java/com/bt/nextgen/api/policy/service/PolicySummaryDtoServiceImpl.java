package com.bt.nextgen.api.policy.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.broker.model.BrokerKey;
import com.bt.nextgen.api.policy.model.PolicyTrackingDto;
import com.bt.nextgen.api.policy.util.PolicyUtil;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.util.StringUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyApplications;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyTracking;
import com.bt.nextgen.service.avaloq.insurance.service.PolicyIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class PolicySummaryDtoServiceImpl implements PolicySummaryDtoService {

    @Autowired
    private PolicyIntegrationService policyIntegrationService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private PolicyUserPreferencesService policyUserPreferencesService;

    @Autowired
    private PolicyUtility policyUtility;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    /**
     * Returns a summary of all policies from either an adviser perspective or a customer perspective.<p>
     * <p>
     * adviser view - (business report screen)
     * customer view - (insurance tracking screen -subsection)
     *
     * @param criteriaList  customer - when present, search for customer policy applications
     *                      fnumber - when present, search for all policies managed by a particular adviser
     * @param serviceErrors
     *
     * @return
     */
    @Override
    public List<PolicyTrackingDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        String fNumber = null;
        String brokerId = null;
        String customerNumber = null;
        for (ApiSearchCriteria searchCriteria : criteriaList) {
            if (Attribute.INSURANCE_FNUMBER.equals(searchCriteria.getProperty())) {
                fNumber = searchCriteria.getValue();
            }
            if (Attribute.BROKER_ID.equals(searchCriteria.getProperty())) {
                brokerId = searchCriteria.getValue();
            }
            if (Attribute.INSURANCE_CUSTOMER_NUMBER.equals(searchCriteria.getProperty())) {
                customerNumber = EncodedString.toPlainText(searchCriteria.getValue());
            }
        }

        List<String> fNumbers = PolicyUtil.getSortedFNumbers(fNumber);
        List<PolicyTracking> insurancesTrackings = new ArrayList<>();
        // policy tracking screen
        boolean displayAccountDetails = false;
        if (isFNumbersValid(fNumbers, brokerId, serviceErrors)) {
            if (StringUtils.isNotEmpty(customerNumber)) { // Tracking screen subsection - multiple Fnumber's possible
                List<PolicyApplications> recentInsurances = policyIntegrationService.getRecentLivesInsured(fNumbers, serviceErrors);
                insurancesTrackings = policyIntegrationService.getPolicyByCustomerNumber(fNumbers, customerNumber, serviceErrors);
                insurancesTrackings = PolicyUtil.getCustomerRecentInsurances(customerNumber, insurancesTrackings, recentInsurances);
                displayAccountDetails = true;
            }
            else { // Business report (Insurance list) screen - only one Fnumber at a time
                if (CollectionUtils.isNotEmpty(fNumbers) && fNumbers.size() == 1) {
                    insurancesTrackings = policyIntegrationService.getPoliciesForAdviser(fNumbers.get(0), serviceErrors);
                    if (brokerId == null) {
                        Collection<BrokerIdentifier> brokers = policyUtility.getBrokers(serviceErrors);
                        if (CollectionUtils.isNotEmpty(brokers)) {
                            brokerId = ConsistentEncodedString.fromPlainText(((BrokerIdentifier) (CollectionUtils.get(brokers, 0))).getKey().getId()).toString();
                        }
                    }
                    saveLastAccessedDetails(fNumbers.get(0), brokerId);
                }
            }
        }
        PolicyDtoConverter dtoConverter = new PolicyDtoConverter(
                accountIntegrationService.loadWrapAccountWithoutContainers(serviceErrors),
                productIntegrationService.loadProductsMap(serviceErrors));
        return dtoConverter.policyTrackingDetailDtos(insurancesTrackings, displayAccountDetails);
    }

    @Override
    public PolicyTrackingDto find(BrokerKey key, ServiceErrors serviceErrors) {
        final String adviserPPId = policyUtility.getAdviserPpId(key.getBrokerId(), serviceErrors);
        List<PolicyTracking> insurancesTrackings = new ArrayList<>();
        if (StringUtils.isNotEmpty(adviserPPId)) {
            insurancesTrackings = policyIntegrationService.getFNumbers(adviserPPId, serviceErrors);
        }
        PolicyTrackingDto trackingDto = PolicyDtoBuilder.policyTrackingDto(insurancesTrackings);
        trackingDto.setLastSelectedFNumber(policyUserPreferencesService.findLastAccessedFNumber());
//        trackingDto.setLastSelectedAdviser(policyUserPreferencesService.findLastAccessedAdviser());
        return trackingDto;
    }

    private void saveLastAccessedDetails(String fNumber, String brokerId) {
        policyUserPreferencesService.saveLastAccessedFNumber(fNumber);
        policyUserPreferencesService.saveLastAccessedAdviser(brokerId);
    }

    @Override
    public PolicyTrackingDto findOne(ServiceErrors serviceErrors) {
        final String adviserPPId = policyUtility.getAdviserPpId(null, serviceErrors);
        List<PolicyTracking> insurancesTrackings = new ArrayList<>();
        if (StringUtil.isNotNullorEmpty(adviserPPId)) {
            insurancesTrackings = policyIntegrationService.getFNumbers(adviserPPId, serviceErrors);
        }
        PolicyTrackingDto trackingDto = PolicyDtoBuilder.policyTrackingDto(insurancesTrackings);
        trackingDto.setLastSelectedFNumber(policyUserPreferencesService.findLastAccessedFNumber());
        //trackingDto.setLastSelectedAdviser(getAdviserName(lastAccessedAdviser, serviceErrors));
        return trackingDto;
    }

    /**
     * Validates the input fnumber access for the logged in paraplanner or adviser
     * with the Fnumber returned from the service for a particular adviser
     *
     * @param requestFNumbers
     * @param brokerId
     * @param serviceErrors
     *
     * @return
     */
    private boolean isFNumbersValid(List<String> requestFNumbers, String brokerId, ServiceErrors serviceErrors) {
        final String adviserPpId = policyUtility.getAdviserPpId(brokerId, serviceErrors);
        List<PolicyTracking> insurancesTrackings = new ArrayList<>();
        if (StringUtil.isNotNullorEmpty(adviserPpId)) {
            insurancesTrackings = policyIntegrationService.getFNumbers(adviserPpId, serviceErrors);
        }
        List<String> validFNumbersForUser = Lambda.collect(insurancesTrackings, Lambda.on(PolicyTracking.class).getFNumber());
        boolean isValid = false;
        for (String requestedFNumber : requestFNumbers) {
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
}
