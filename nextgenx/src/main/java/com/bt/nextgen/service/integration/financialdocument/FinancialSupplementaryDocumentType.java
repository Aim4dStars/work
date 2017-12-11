package com.bt.nextgen.service.integration.financialdocument;

import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.account.AccountStructureType;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.bt.nextgen.service.avaloq.userinformation.UserExperience.ADVISED;
import static com.bt.nextgen.service.avaloq.userinformation.UserExperience.ASIM;
import static com.bt.nextgen.service.avaloq.userinformation.UserExperience.DIRECT;
import static com.bt.nextgen.service.integration.account.AccountStructureType.SMSF;
import static com.bt.nextgen.service.integration.financialdocument.FinancialDocumentType.ANNUAL_INVESTMENT_STATEMENT;
import static com.bt.nextgen.service.integration.financialdocument.FinancialDocumentType.ANNUAL_TAX_STATEMENT;
import static java.util.Collections.unmodifiableCollection;

public enum FinancialSupplementaryDocumentType {

    ANNUAL_AUDIT_REPORT("Annual audit report", "Audit_Report", ANNUAL_INVESTMENT_STATEMENT, new UserExperience[]{ADVISED, ASIM, DIRECT}),
    STATEMENT_GUIDE("Statement guide", "BT_Annual_Investor_Statement_Guide", ANNUAL_INVESTMENT_STATEMENT, new UserExperience[]{ADVISED, ASIM}),
    DIRECT_STATEMENT_GUIDE("BT Invest statement guide", "BT_Invest_Annual_Investor_Statement_Guide", ANNUAL_INVESTMENT_STATEMENT, new UserExperience[]{DIRECT}),
    TAX_GUIDE("Tax guide", "BT_Panorama_Tax_Guide", ANNUAL_TAX_STATEMENT, new UserExperience[]{ADVISED, ASIM}),
    SMSF_TAX_GUIDE("SMSF Tax guide", "BT_Panorama_SMSF_Tax_Guide", ANNUAL_TAX_STATEMENT, new UserExperience[]{ADVISED, ASIM}),
    DIRECT_TAX_GUIDE("BT Invest tax guide", "BT_Invest_Tax_Guide", ANNUAL_TAX_STATEMENT, new UserExperience[]{DIRECT});

    private String description;
    private String documentName;
    private FinancialDocumentType parentDocumentType;
    private Collection<UserExperience> userExperiences;

    /**
     * Constructor for the FinancialSupplementaryDocumentType
     *
     * @param description        - Document description
     * @param documentName       - Document name
     * @param parentDocumentType - Document statement category
     * @param userExperiences    - List of valid user experiences for the documents
     */
    FinancialSupplementaryDocumentType(String description, String documentName, FinancialDocumentType parentDocumentType,
                                       UserExperience[] userExperiences) {
        this.description = description;
        this.documentName = documentName;
        this.parentDocumentType = parentDocumentType;
        this.userExperiences = unmodifiableCollection(Arrays.asList(userExperiences));
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return document description
     */
    public String getDocumentName() {
        return documentName;
    }

    /**
     * Gets the map of supplementary documents based on following parameters
     *
     * @param parentDocumentType   - Parent Financial year document type
     * @param accountStructureType - Account structure type (Individual/Joint/SMSF/SUPER etc.) the documents are valid for
     * @param userExperience       - User experience [Direct/ASIM/ADVISED]
     */
    public static Set<FinancialSupplementaryDocumentType> getSupplementaryDocuments(FinancialDocumentType parentDocumentType,
                                                                                    AccountStructureType accountStructureType,
                                                                                    UserExperience userExperience) {
        final Set<FinancialSupplementaryDocumentType> supplementaryDocuments = new LinkedHashSet<>();
        for (FinancialSupplementaryDocumentType supplDocument : FinancialSupplementaryDocumentType.values()) {
            if (supplDocument.parentDocumentType.equals(parentDocumentType) && supplDocument.userExperiences.contains(userExperience)) {
                supplementaryDocuments.add(supplDocument);
            }
        }
        filterTaxGuidesForAccountType(supplementaryDocuments, accountStructureType);
        return supplementaryDocuments;
    }

    /**
     * Gets the specific tax guide based on account-type
     *
     * @param supplementaryDocuments - List of supplementary documents
     * @param accountStructureType   - Account structure type (Individual/Joint/SMSF/SUPER etc.) the documents are valid for
     */
    private static void filterTaxGuidesForAccountType(Set<FinancialSupplementaryDocumentType> supplementaryDocuments,
                                                      AccountStructureType accountStructureType) {
        if (SMSF.equals(accountStructureType)) {
            supplementaryDocuments.remove(TAX_GUIDE);
        } else {
            supplementaryDocuments.remove(SMSF_TAX_GUIDE);
        }
    }
}
