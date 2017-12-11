package com.bt.nextgen.api.statements.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.api.statements.model.DocumentKey;
import com.bt.nextgen.api.statements.permission.DocumentRequestManager;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.financialdocument.Document;
import com.bt.nextgen.service.integration.financialdocument.DocumentIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Created by L081361 on 27/11/2015.
 */
@Service
@Transactional
public class UserDocumentDtoServiceImpl implements UserDocumentDtoService{

    @Autowired
    private DocumentIntegrationService documentIntegrationService;

    @Autowired
    private UserDocumentDtoConverter userDocumentDtoConverter;

    @Autowired
    private UserProfileService profileService;


    @Override
    public List<DocumentDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {


        DocumentRoleMatcher documentVisibilityUtil = new DocumentRoleMatcher(profileService);
        FilterBuilder filterUtil = new FilterBuilder(profileService, documentIntegrationService.getParentFolderId());
         Collection<Document> documents = documentIntegrationService.getDocuments(filterUtil.createUserFilterCriteria(criteriaList));
        List<DocumentDto> documentList = userDocumentDtoConverter.getDocumentDtoList(documents);
        return Lambda.filter(documentVisibilityUtil, documentList);
    }

    @Override
    public DocumentDto loadDocument(DocumentKey documentKey) throws IOException {
        DocumentDto dto = DocumentRequestManager.getDocument(documentKey);
        if (dto == null) {
            FilterBuilder filterUtil = new FilterBuilder(profileService, documentIntegrationService.getParentFolderId());
            Collection<Document> documents = documentIntegrationService.getDocuments(filterUtil.createCriteriaForDocumentInfo(documentKey));
            Document document = documents.iterator().next();
            dto = userDocumentDtoConverter.getDocumentDto(document);
        }
        String documentId = new EncodedString(dto.getKey().getDocumentId()).plainText();
        Document doc = documentIntegrationService.getDocumentData(com.bt.nextgen.service.integration.financialdocument.DocumentKey.valueOf(documentId));
        dto.setDocumentBytes(doc.getData());
        return dto;
    }
}
