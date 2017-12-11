package com.bt.nextgen.api.policy.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.policy.model.PolicyDocumentDto;
import com.bt.nextgen.api.policy.util.PolicyUtil;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.basil.BasilIntegrationService;
import com.bt.nextgen.service.avaloq.basil.ImageDetails;
import com.bt.nextgen.service.avaloq.insurance.model.Policy;
import com.bt.nextgen.service.avaloq.insurance.service.PolicyIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PolicyDocumentDtoServiceImpl implements PolicyDocumentDtoService {

    @Autowired
    private PolicyIntegrationService policyIntegrationService;

    @Autowired
    private BasilIntegrationService basilIntegrationService;

    /**
     * Service created for Basil insurance documents.
     */
    @Override
    public List<PolicyDocumentDto> search(AccountKey key, ServiceErrors serviceErrors) {
        final String decodedAccountId = EncodedString.toPlainText(key.getAccountId());
        final List<Policy> policies = policyIntegrationService.retrievePoliciesByAccountNumber(decodedAccountId, serviceErrors);
        final List<String> policyNumbers = new ArrayList<>();
        final Set<String> portfolioNumbers = new HashSet<>();
        PolicyUtil.populatePolicyNumbersAndPortfolioNumbers(policies, policyNumbers, portfolioNumbers);
        final List<ImageDetails> imageResponse = basilIntegrationService.getInsuranceDocuments(policyNumbers, portfolioNumbers, serviceErrors);
        return PolicyDtoBuilder.getPolicyDocumentDtos(imageResponse);
    }

    @Override
    public PolicyDocumentDto find(AccountKey key, ServiceErrors serviceErrors) {
        return null;
    }
}
