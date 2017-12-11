package com.bt.nextgen.api.policy.service;

import com.bt.nextgen.api.beneficiary.service.BeneficiaryDtoService;
import com.bt.nextgen.api.policy.model.PolicyDto;
import com.bt.nextgen.api.policy.model.PolicyKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.insurance.model.Policy;
import com.bt.nextgen.service.avaloq.insurance.service.PolicyIntegrationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PolicyDtoServiceImpl implements PolicyDtoService {

    @Autowired
    private PolicyIntegrationService policyIntegrationService;

    @Autowired
    private BeneficiaryDtoService beneficiaryDtoService;

    @Autowired
    private PolicyUtility policyUtility;

    /**
     * Retrieve all insurance policies for a specific account
     *
     * @param key           Policy key containing a valid account-id to retrieve policies for. policy_id is not required.
     * @param serviceErrors
     *
     * @return List of policy dto objects for this account
     */
    @Override
    public List<PolicyDto> search(PolicyKey key, ServiceErrors serviceErrors) {
        final String unencodedAccountId = EncodedString.toPlainText(key.getAccountId());
        List<Policy> policies = policyIntegrationService.retrievePoliciesByAccountNumber(unencodedAccountId, serviceErrors);
        PolicyDtoConverter dtoConverter = policyUtility.getPolicyDtoConverter(serviceErrors);
        return dtoConverter.toPolicyDto(unencodedAccountId, policies, beneficiaryDtoService);
    }

    /**
     * Retrieve a specific insurance policy based on policy number
     *
     * @param key           policy key
     * @param serviceErrors
     *
     * @return
     */
    @Override
    public PolicyDto find(PolicyKey key, ServiceErrors serviceErrors) {
        final String unencodedAccountId = EncodedString.toPlainText(key.getAccountId());
        List<Policy> policies = policyIntegrationService.retrievePolicyByPolicyNumber(key.getPolicyId(), serviceErrors);
        PolicyDtoConverter dtoConverter = policyUtility.getPolicyDtoConverter(serviceErrors);
        List<PolicyDto> policyDtos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(policies)) {
            // We don't need super beneficiary details for this service, hence sending the object as null
            policyDtos = dtoConverter.toPolicyDto(unencodedAccountId, policies, null);
        }
        return CollectionUtils.isNotEmpty(policyDtos) ? policyDtos.get(0) : null;
    }

}