package com.bt.nextgen.api.statements.service;

import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.api.statements.model.DocumentKey;
import com.bt.nextgen.api.statements.model.SupportedDocumentType;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.cmis.constants.DocumentConstants;
import com.bt.nextgen.service.cmis.constants.VisibilityRoles;
import com.bt.nextgen.service.integration.financialdocument.Document;
import com.btfin.panorama.core.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by L081361 on 27/11/2015.
 */



@Component
public class UserDocumentDtoConverter {


    public List<DocumentDto> getDocumentDtoList(Collection<Document> documents) {
        List<DocumentDto> userDocumentDtoList = new ArrayList<DocumentDto>();
        for (Document document : documents) {
            DocumentDto documentDto = getDocumentDto(document);
            userDocumentDtoList.add(documentDto);
        }
        return userDocumentDtoList;
    }


    public DocumentDto getDocumentDto(Document document) {
        DocumentDto documentDto = new DocumentDto();
        DocumentKey key = new DocumentKey();
        key.setDocumentId(EncodedString.fromPlainText(document.getDocumentKey().getId()).toString());
        documentDto.setKey(key);
        documentDto.setStatus(StringUtils.isEmpty(document.getStatus()) ? DocumentConstants.DOCUMENT_STATUS_FINAL : document.getStatus());
        VisibilityRoles role = StringUtil.isNotNullorEmpty(document.getUploadedRole()) ?
                VisibilityRoles.valueOf(document.getUploadedRole().toUpperCase()) : VisibilityRoles.PANORAMA;
        documentDto.setUploadedRole(role.getDescription());
        documentDto.setDocumentName(getDocumentName(document));
        documentDto.setFileType(document.getMimeType());
        documentDto.setFileName(document.getFileName());
        documentDto.setFileExtension(document.getFileExtension());
        documentDto.setSize(document.getSize());
        documentDto.setUploadedDate(document.getUploadedDate());
        documentDto.setLastModificationDate(document.getLastModificationDate());

        return documentDto;
    }

    private String getDocumentName(Document document) {
        String documentName = document.getDocumentName();
        String extension = SupportedDocumentType.forCode(document.getMimeType()).name().toLowerCase();
        if (!StringUtils.endsWith(documentName, ("." + extension))) {
            documentName = documentName + "." + extension;
        }
        return documentName;
    }
}



