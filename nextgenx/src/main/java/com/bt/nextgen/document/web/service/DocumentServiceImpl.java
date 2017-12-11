package com.bt.nextgen.document.web.service;

import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentData;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentIntegrationService;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentKey;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private FinancialDocumentIntegrationService documentService;

    @Autowired
    private UserProfileService userProfileService;

    public FinancialDocumentData loadDocument(String statementId, FinancialDocumentType financialDocumentType) {
        String relationshipType = getRelationshipType(financialDocumentType);

        if (FinancialDocumentType.FEE_REVENUE_STATEMENT.equals(financialDocumentType)
                && Constants.RELATIONSHIP_TYPE_DG.equals(relationshipType)) {
            return loadFeeRevenueStatementForDG(statementId);
        }

        return documentService.loadDocument(FinancialDocumentKey.valueOf(statementId), relationshipType, null);
    }

    /**
     * Identify the relationship type based on the requested document type and current active user profile
     * 
     * @param financialDocumentType
     * @return relationship type
     */
    private String getRelationshipType(FinancialDocumentType financialDocumentType) {
        String relationshipType = Constants.RELATIONSHIP_TYPE_ACCOUNT;

        if (FinancialDocumentType.FEE_REVENUE_STATEMENT.equals(financialDocumentType)) {
            if (JobRole.ADVISER.equals(userProfileService.getActiveProfile().getJobRole())) {
                relationshipType = Constants.RELATIONSHIP_TYPE_ADVISER;
            } else if (userProfileService.isPortfolioManager() || userProfileService.isInvestmentManager()) {
                relationshipType = Constants.RELATIONSHIP_TYPE_INV_MGR;
            } else {
                relationshipType = Constants.RELATIONSHIP_TYPE_DG;
            }
        }

        return relationshipType;
    }

    /**
     * Load specified document using DG relationship. If not found, load specified document with IM relationship as the document
     * will belong to an IM linked to the DG
     * 
     * @param statementId
     * @return document
     */
    private FinancialDocumentData loadFeeRevenueStatementForDG(String statementId) {
        FinancialDocumentData document = documentService.loadDocument(FinancialDocumentKey.valueOf(statementId),
                Constants.RELATIONSHIP_TYPE_DG, null);

        if (document == null) {
            document = documentService.loadDocument(FinancialDocumentKey.valueOf(statementId),
                    Constants.RELATIONSHIP_TYPE_INV_MGR, null);
        }

        return document;
    }
}
