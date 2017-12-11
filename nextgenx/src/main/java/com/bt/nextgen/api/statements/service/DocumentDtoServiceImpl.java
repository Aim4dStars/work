package com.bt.nextgen.api.statements.service;

/**
 * This class implements DocumentDtoService Interface and provides implementation of the functionalities specified in DocumentApiController
 * In Addition these methods It also provides implementation for checking the duplicateFileName in the file net .This class calls the-
 * -methods in CmisDocumentIntegration class for interacting with FileNet
 */

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.statements.decorator.DtoDecorator;
import com.bt.nextgen.api.statements.decorator.KeyDecorator;
import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.api.statements.model.DocumentKey;
import com.bt.nextgen.api.statements.permission.DocumentRequestManager;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.financialdocument.Document;
import com.bt.nextgen.service.integration.financialdocument.DocumentIntegrationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class DocumentDtoServiceImpl implements DocumentDtoService {

    @Autowired
    private DocumentIntegrationService documentIntegrationService;

    @Autowired
    private DocumentDtoConverter documentDtoConverter;

    @Autowired
    private UserProfileService profileService;

    @Override
    public List<DocumentDto> search(DocumentKey key, ServiceErrors serviceErrors) {
        List<ApiSearchCriteria> criteria = new ArrayList<>();
        return search(key, criteria, serviceErrors);
    }

    @Override
    public List<DocumentDto> search(DocumentKey key, List<ApiSearchCriteria> criteria, ServiceErrors serviceErrors) {
        DocumentRoleMatcher documentVisibilityUtil = new DocumentRoleMatcher(profileService);
        FilterBuilder filterUtil = new FilterBuilder(profileService, documentIntegrationService.getParentFolderId());
        String accountNumber = documentDtoConverter.getAccountNumber(key, serviceErrors);
        Collection<Document> documents = documentIntegrationService.getDocuments(filterUtil.createDocumentsFilterCriteria(accountNumber, criteria));
        List<DocumentDto> documentList = documentDtoConverter.getDocumentDtoList(documents, key.getAccountId());
        return Lambda.filter(documentVisibilityUtil, documentList);
    }

    @Override
    public DocumentDto find(DocumentKey key, ServiceErrors serviceErrors) {
        DocumentDto dto = DocumentRequestManager.getDocument(key);
        if (dto == null) {
            dto =  getDocument(key);
        }
        return dto;
    }

    private DocumentDto getDocument(DocumentKey key) {
        FilterBuilder filterUtil = new FilterBuilder(profileService, documentIntegrationService.getParentFolderId());
        Collection<Document> documents = documentIntegrationService.getDocuments(filterUtil.createCriteriaForDocumentInfo(key));
        if (!documents.isEmpty()) {
            return documentDtoConverter.getDocumentDto(documents.iterator().next(), key.getAccountId());
        }
        return null;
    }

    @Override
    public DocumentDto update(DocumentDto keyedObject, ServiceErrors serviceErrors) {
        Document document = documentIntegrationService.updateDocument(documentDtoConverter.getDocumentToUpdate(keyedObject));
        String documentId = new EncodedString(keyedObject.getKey().getDocumentId()).plainText();
        if (document == null || document.getDocumentKey().getId() == null || !documentId.equals(document.getDocumentKey().getId())) {
            serviceErrors.addError(new ServiceErrorImpl("Error while updating document"));
        } else {
            keyedObject.setChangeToken(document.getChangeToken());
        }
        return keyedObject;
    }

    @Override
    public DocumentDto loadDocument(DocumentKey documentKey) throws IOException {
        DocumentDto dto = DocumentRequestManager.getDocument(documentKey);
        if (dto == null) {
            FilterBuilder filterUtil = new FilterBuilder(profileService, documentIntegrationService.getParentFolderId());
            Collection<Document> documents = documentIntegrationService.getDocuments(filterUtil.createCriteriaForDocumentInfo(documentKey));
            Document document = documents.iterator().next();
            dto = documentDtoConverter.getDocumentDto(document, documentKey.getAccountId());
        }
        String documentId = new EncodedString(dto.getKey().getDocumentId()).plainText();
        Document doc = documentIntegrationService.getDocumentData(com.bt.nextgen.service.integration.financialdocument.DocumentKey.valueOf(documentId));
        dto.setFileType(doc.getMimeType());
        dto.setDocumentBytes(doc.getData());
        return dto;
    }

    @Override
    public DocumentDto loadDocuments(String... documentIds) throws IOException {
        List<DocumentDto> documents = new ArrayList<>();
        for (String documentId : documentIds) {
            DocumentKey key = new DocumentKey();
            key.setDocumentId(documentId);
            documents.add(loadDocument(key));
        }
        return documentDtoConverter.loadDocumentZipped(documents, new ServiceErrorsImpl());
    }

    @Override
    public List<DocumentDto> getVersions(DocumentKey documentKey) {
        String documentId = new EncodedString(documentKey.getDocumentId()).plainText();
        Collection<Document> documentList = documentIntegrationService.getDocumentVersions(com.bt.nextgen.service.integration.financialdocument.DocumentKey.valueOf(documentId));
        return documentDtoConverter.getDocumentDtoList(documentList, documentKey.getAccountId());
    }

    @Override
    public List<DocumentDto> getFilteredValue(DocumentKey key, List<ApiSearchCriteria> criteria, String queryString, ServiceErrors serviceErrors) {
        List<DocumentDto> documentDtoList = search(key, criteria, serviceErrors);
        return documentDtoConverter.filteredDocuments(documentDtoList, queryString);
    }

    public List<DocumentDto> getDocumentsForDuplicateNameCheck(DocumentDto dto, ServiceErrors serviceErrors) {
        FilterBuilder filterUtil = new FilterBuilder(profileService, documentIntegrationService.getParentFolderId());
        DocumentRoleMatcher documentVisibilityUtil = new DocumentRoleMatcher(profileService);
        String accountNumber;
        if(!profileService.isServiceOperator()&&"OFFAPR".equals(dto.getDocumentTitleCode())){
            accountNumber = EncodedString.toPlainText(dto.getRelationshipId());
        } else {
            accountNumber = documentDtoConverter.getAccountNumber(dto.getKey(), serviceErrors);
        }
        String documentId = dto.getKey().getDocumentId() != null ? documentDtoConverter.getDecodedString(dto.getKey().getDocumentId()) : StringUtils.EMPTY;
        Collection<Document> documents = documentIntegrationService.getDocuments(filterUtil.createDuplicateNameCheckCriteria(dto, accountNumber, documentId));
        List<DocumentDto> documentList = documentDtoConverter.getDocumentDtoList(documents, dto.getKey().getAccountId());
        return Lambda.filter(documentVisibilityUtil, documentList);
    }

    @Override
    public boolean deleteDocument(String documentId) {
        return documentIntegrationService.deleteDocument(
                com.bt.nextgen.service.integration.financialdocument.DocumentKey.valueOf(new EncodedString(documentId).plainText()));
    }
    @Override
    public DocumentDto loadDocumentVersion(DocumentKey documentKey) throws IOException {
        String documentId = new EncodedString(documentKey.getDocumentId()).plainText();
        Document doc = documentIntegrationService.getDocumentData(com.bt.nextgen.service.integration.financialdocument.DocumentKey.valueOf(documentId));
        DocumentDto documentDto = new DocumentDto();
        DtoDecorator decorator = new KeyDecorator(doc.getDocumentKey().getId(), documentKey.getAccountId(), documentDto);
        documentDto = decorator.decorate();
        documentDto.setDocumentName(doc.getDocumentName());
        documentDto.setFileType(doc.getMimeType());
        documentDto.setDocumentBytes(doc.getData());
        return documentDto;
    }
    /**
     * SoftDelete meethod be causefull with above method
     *
     * @param key
     */
    @Override
    public boolean softDeleteDocument(DocumentKey key) {
        Document document = documentDtoConverter.getMetaDataFieldsForDelete(key);
        documentIntegrationService.updateDocument(document);
        return getDocument(key) == null;
    }
}