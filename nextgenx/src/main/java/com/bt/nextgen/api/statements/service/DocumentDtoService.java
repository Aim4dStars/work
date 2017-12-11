package com.bt.nextgen.api.statements.service;

import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.api.statements.model.DocumentKey;
import com.bt.nextgen.core.api.dto.*;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;

import java.io.IOException;
import java.util.List;

/**
 * Document Dto service
 */
public interface DocumentDtoService extends SearchByPartialKeyCriteriaDtoService<DocumentKey, DocumentDto>,
        SearchByKeyDtoService<DocumentKey, DocumentDto>, UpdateDtoService<DocumentKey, DocumentDto>, ServiceFilterByKeyDtoService<DocumentKey, DocumentDto> {

    /**
     * Method load the document bytes from filenet (actual file - no metadata)
     * @param documentKey
     * @return DocumentDto with file bytes set in get
     * @throws IOException
     */
    DocumentDto loadDocument(DocumentKey documentKey) throws IOException;

    DocumentDto loadDocuments(String... documentIds) throws IOException;

    List<DocumentDto> getVersions(DocumentKey documentKey);

    @Override
    List<DocumentDto> getFilteredValue(DocumentKey key, List<ApiSearchCriteria> criteria, String queryString, ServiceErrors serviceErrors);

    public boolean deleteDocument(String documentId);

    public boolean softDeleteDocument(DocumentKey key);

    public List<DocumentDto> getDocumentsForDuplicateNameCheck(DocumentDto dto, ServiceErrors serviceErrors);

    public DocumentDto loadDocumentVersion(DocumentKey documentKey) throws IOException;
}