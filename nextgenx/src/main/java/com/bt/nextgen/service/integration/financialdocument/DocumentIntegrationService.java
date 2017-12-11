package com.bt.nextgen.service.integration.financialdocument;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by L062329 on 16/07/2015.
 */
public interface DocumentIntegrationService {

    /**
     * Loads the metadata of the documents based on the provided criteria.
     *
     * @param criteria
     * @return
     */
    Collection<Document> getDocuments(Criteria criteria);

    /**
     * Loads document as byte array from the downstream system.
     *
     * @param documentKey
     * @return byte array of the document
     */
    Document getDocumentData(DocumentKey documentKey) throws IOException;

    /**
     * Updates metadata of the document
     *
     * @param document
     * @return true if successful
     */
    Document updateDocument(Document document);

    /**
     * Creates a new Document in the FileNet
     *
     * @param document
     * @return
     */
    Document createNewDocument(Document document);

    /**
     * Creates a new Version of the document  in the FileNet
     *
     * @param document
     * @return
     */

    Document uploadNewVersionOfDocument(Document document);


    /**
     *
     * @param documentKey
     * @return
     */
    Collection<Document> getDocumentVersions(DocumentKey documentKey);

    /**
     * Delete document against document id
     * @param documentKey
     * @return boolean true if deleted false if not.
     */
     boolean deleteDocument(DocumentKey documentKey);

    String getParentFolderId();
}
