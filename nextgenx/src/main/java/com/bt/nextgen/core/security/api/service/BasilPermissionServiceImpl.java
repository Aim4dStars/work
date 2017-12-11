package com.bt.nextgen.core.security.api.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bt.nextgen.api.policy.service.PolicyUtility;
import com.bt.nextgen.api.policy.util.PolicyUtil;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.basil.BasilIntegrationService;
import com.bt.nextgen.service.avaloq.basil.ImageDetails;
import com.bt.nextgen.service.avaloq.insurance.model.Policy;
import com.bt.nextgen.service.avaloq.insurance.service.PolicyIntegrationService;

@Component("basilPermissionService")
@Transactional(value = "springJpaTransactionManager")
class BasilPermissionServiceImpl implements BasilPermissionService {

    @Autowired
    private PolicyIntegrationService policyIntegrationService;

    @Autowired
    private BasilIntegrationService basilIntegrationService;

    /**
     * Checks whether the accountID(adviser) has access to the document
     *
     * @param accountId
     *
     * @return boolean
     */
    public boolean hasBasilDocumentAccess(String accountId, String documentId) {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        final String decodedAccountId = EncodedString.toPlainText(accountId);
        final List<Policy> policies = policyIntegrationService.retrievePoliciesByAccountNumber(decodedAccountId, serviceErrors);
        final List<String> policyNumbers = new ArrayList<>();
        final Set<String> portfolioNumbers = new HashSet<>();
        PolicyUtil.populatePolicyNumbersAndPortfolioNumbers(policies, policyNumbers, portfolioNumbers);
        final List<ImageDetails> imageResponse = basilIntegrationService.getInsuranceDocuments(policyNumbers, portfolioNumbers, serviceErrors);
        return PolicyUtility.verifyDocumentExists(imageResponse, documentId);
    }
}