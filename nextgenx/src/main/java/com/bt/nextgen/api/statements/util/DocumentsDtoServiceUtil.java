package com.bt.nextgen.api.statements.util;


import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.statements.model.SupplimentaryDocument;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.financialdocument.BaseCmisDocument;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentType;
import com.bt.nextgen.service.integration.financialdocument.FinancialSupplementaryDocumentType;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.convert;

/* Utility class for the documents dto services*/

public class DocumentsDtoServiceUtil {

    private static final String FINANCIAL_DOCUMENTS_PATH = "aem.financialDocs.path";
    private static final String DOCUMENT_EXTENSION = ".pdf";

    private DocumentsDtoServiceUtil() {
        // private constructor to hide the implicit public one(sonar)
    }

    /**
     * Gets the list of supplementary documents for the Financial statements
     *
     * @param document - The Financial year statement document
     */
    public static List<SupplimentaryDocument> getSupplementaryDocuments(BaseCmisDocument document, WrapAccount account, UserExperience userExperience) {
        final List<SupplimentaryDocument> supplementaryDocuments = new ArrayList<>();
        if (account != null && userExperience != null && document != null && document.getPeriodEndDate() != null) {
            final FinancialDocumentType documentType = FinancialDocumentType.forCode(document.getDocumentTitleCode());
            final Set<FinancialSupplementaryDocumentType> supplDocuments =
                    FinancialSupplementaryDocumentType.getSupplementaryDocuments(documentType, account.getAccountStructureType(), userExperience);
            supplementaryDocuments.addAll(convertDocuments(supplDocuments, document));
        }
        return supplementaryDocuments;
    }

    /**
     * Converts supplementary documents to {@link SupplimentaryDocument}
     *
     * @param supplDocuments - The list of the supplementary documents
     * @param parentDocument - The financial year statement document (parent of the supplementary documents)
     */
    private static List<SupplimentaryDocument> convertDocuments(Collection<FinancialSupplementaryDocumentType> supplDocuments, final BaseCmisDocument parentDocument) {
        final DateTime endDate = parentDocument.getPeriodEndDate();
        final DateTime startDate = parentDocument.getPeriodStartDate() != null ? parentDocument.getPeriodStartDate() : endDate.minusYears(1);

        return convert(supplDocuments, new Converter<FinancialSupplementaryDocumentType, SupplimentaryDocument>() {
            @Override
            public SupplimentaryDocument convert(FinancialSupplementaryDocumentType documentType) {
                return new SupplimentaryDocument(documentType.getDescription(), Properties.getString(FINANCIAL_DOCUMENTS_PATH) +
                        documentType.getDocumentName() + "_" + startDate.getYear() + "-" + endDate.getYear() + DOCUMENT_EXTENSION);
            }
        });
    }
}