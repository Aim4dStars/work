package com.bt.nextgen.api.statements.service;

import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.api.statements.model.DocumentKey;
import com.bt.nextgen.api.statements.model.SupportedDocumentType;
import com.bt.nextgen.service.integration.financialdocument.Document;
import com.bt.nextgen.service.integration.financialdocument.DocumentIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This Class provides the implemetaions for the methods mentioned in the DocumentUploadApiController
 */
@Service
public class DocumentUploadDtoServiceImpl implements DocumentUploadDtoService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentUploadDtoServiceImpl.class);

    @Autowired
    private DocumentIntegrationService documentIntegrationService;

    @Autowired
    private DocumentDtoConverter documentDtoConverter;

    @Override
    public DocumentDto upload(DocumentDto dto) {
        logger.info("DocumentUploadDtoServiceImpl::upload: method invoked, document name {}",dto.getDocumentName());
        SupportedDocumentType fileExtension = SupportedDocumentType.getFileExtension(dto.getDocumentName());
        dto.setFileExtension(fileExtension.name());
        dto.setFileName(dto.getDocumentName());
        dto.setFileType(fileExtension.getMimetype());
        Document document = documentIntegrationService.createNewDocument(documentDtoConverter.getDocument(dto));
        DocumentKey key = new DocumentKey();
        key.setDocumentId(document.getDocumentKey().getId());
        logger.info("DocumentUploadDtoServiceImpl::upload: method complete");
        return dto;
    }

    @Override
    public DocumentDto uploadNewVersion(DocumentDto dto) {
        logger.info("DocumentUploadDtoServiceImpl::uploadNewVersion: method invoked document name {}", dto.getDocumentName());
        SupportedDocumentType fileExtension = SupportedDocumentType.getFileExtension(dto.getDocumentName());
        dto.setFileExtension(fileExtension.name());
        dto.setFileName(dto.getDocumentName());
        dto.setFileType(fileExtension.getMimetype());
        Document document = documentIntegrationService.uploadNewVersionOfDocument(documentDtoConverter.getDocument(dto));
        DocumentKey key = new DocumentKey();
        key.setDocumentId(document.getDocumentKey().getId());
        logger.info("DocumentUploadDtoServiceImpl::uploadNewVersion: method complete");
        return dto;
    }
}
