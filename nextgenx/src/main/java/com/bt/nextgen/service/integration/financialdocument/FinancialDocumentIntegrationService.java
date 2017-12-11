package com.bt.nextgen.service.integration.financialdocument;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.user.UserKey;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.List;

/**
 * Interface to load doucments from the document repository
 */
public interface FinancialDocumentIntegrationService {
    /**
     * Load retrieve the list of documents available to the current user for a single account.
     * 
     * @param accountKey
     *            - account key to load the document list ofr
     * @param fromDate
     *            - the start date of the search
     * @param toDate
     *            - the end date of the search
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
     * @return The list of financial documents for the account available to the current user in the document repository. if no
     *         documents are found, an empty list is returned
     */
    public Collection<FinancialDocument> loadDocuments(AccountKey accountKey, Collection<FinancialDocumentType> documentTypes,
            DateTime fromDate, DateTime toDate, String relationshipType, ServiceErrors serviceErrors);

    /**
     * Load retrieve the list of documents available to the current user for multiple accounts. The user must have access to all
     * accounts or an empty list is returned
     * 
     * @param accountKeys
     *            - account keys to load documents for
     * @param fromDate
     *            - the start date of the search
     * @param toDate
     *            - the end date of the search
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
     * @return The list of financial documents for the account available to the current user in the document repository. if no
     *         documents are found, an empty list is returned
     */
    public Collection<FinancialDocument> loadDocuments(Collection<AccountKey> accountKeys,
            Collection<FinancialDocumentType> documentTypes, DateTime fromDate, DateTime toDate, String relationshipType,
            ServiceErrors serviceErrors);

    /**
     * Retrieve the list of documents available to the current user.
     * 
     * @param accountKey
     *            - plain text avaloq id of the account to load
     * @param fromDate
     *            - the start date of the search
     * @param toDate
     *            - the end date of the search
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
     * @return The list of financial documents for the account available to the current user in the document repository. if no
     *         documents are found, an empty list is returned
     */
    public Collection<FinancialDocument> loadDocuments(Collection<FinancialDocumentType> documentTypes, DateTime fromDate,
            DateTime toDate, String relationshipType, ServiceErrors serviceErrors);

    /**
     * Retrieve the list of documents available to the current user.
     * 
     * @param userKey
     *            - plain text GCM id
     * @param fromDate
     *            - the start date of the search
     * @param toDate
     *            - the end date of the search
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
     * @return The list of fee revenue documents for logged in user. if no documents are found, an empty list is returned
     */
    public Collection<FinancialDocument> loadDocuments(UserKey userKey, Collection<FinancialDocumentType> documentTypes,
            DateTime fromDate, DateTime toDate, String relationshipType, ServiceErrors serviceErrors);

    /**
     * Retrieve the list of documents available to the current user.
     * 
     * @param brokerKey
     *            - plain text dealer group OE id
     * @param fromDate
     *            - the start date of the search
     * @param toDate
     *            - the end date of the search
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
     * @return The list of fee revenue documents for logged in user. if no documents are found, an empty list is returned
     */
    public Collection<FinancialDocument> loadDocuments(BrokerKey brokerKey, Collection<FinancialDocumentType> documentTypes,
            DateTime fromDate, DateTime toDate, List<String> relationshipTypes, ServiceErrors serviceErrors);

    /**
     * Retrieves a requested document from the document repository. If the requested document cannot be found then null
     * 
     * @param documentKey
     *            - the identifier for the current document
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
     * @return The requested document. If no matching document can be found, then null is returned
     */
    public FinancialDocumentData loadDocument(FinancialDocumentKey documentKey, String relationshipType,
            ServiceErrors serviceErrors);

    /**
     * Retrieve all documents for give account for service ops.
     * 
     * @param accountNumber
     * @param relationshipType
     * @param serviceErrors
     * @return The list of documents for service ops user. if no documents are found, an empty list is returned
     */
    public Collection<FinancialDocument> loadAllDocuments(String accountNumber, String relationshipType,
            ServiceErrors serviceErrors);

    /**
     * Retrieves a requested document from the document repository for service ops. If the requested document cannot be found then
     * null
     * 
     * @param documentKey
     *            - the identifier for the current document
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
     * @return The requested document. If no matching document can be found, then null is returned
     */
    public FinancialDocumentData loadDocumentContent(FinancialDocumentKey documentKey, ServiceErrors serviceErrors);

    /**
     * Retrieve all IM Monthly Report for the specified model and relationshipId.
     * 
     * @param documentType
     *            IMMODEL or IMMODELALL
     * @param modelId
     * @param toDate
     * @param relationshipId
     *            9 digit PAN/GCM number of the legal person
     * @param relationshipType
     *            INVST_MGR
     * @param serviceErrors
     * @return A list of IM Monthly Report. If no document is found, an empty list is returned.
     */
    public FinancialDocumentData loadIMDocument(String documentType, String modelId, DateTime toDate, String relationshipId,
            String relationshipType, ServiceErrors serviceErrors);

    public Collection<FinancialDocument> loadDocumentsForBrokers(List<BrokerKey> brokerKeys,
            Collection<FinancialDocumentType> documentTypes, DateTime fromDate, DateTime toDate, List<String> relationshipTypes,
            ServiceErrors serviceErrors);
}
