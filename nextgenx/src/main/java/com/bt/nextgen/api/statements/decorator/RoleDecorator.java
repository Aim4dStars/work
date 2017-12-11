package com.bt.nextgen.api.statements.decorator;

import com.bt.nextgen.api.statements.model.DocumentDto;
import com.btfin.panorama.core.util.StringUtil;
import com.bt.nextgen.service.cmis.constants.VisibilityRoles;
import com.bt.nextgen.service.integration.financialdocument.Document;

/**
 *
 */
public class RoleDecorator implements DtoDecorator {
    private DtoDecorator decorator;
    private DocumentDto documentDto;
    private Document document;

    public RoleDecorator(DocumentDto documentDto, Document document) {
        this.documentDto = documentDto;
        this.document = document;
    }

    public RoleDecorator(DtoDecorator decorator, DocumentDto documentDto, Document document) {
        this.decorator = decorator;
        this.documentDto = documentDto;
        this.document = document;
    }

    @Override
    public DocumentDto decorate() {
        if(decorator!= null) {
            documentDto = decorator.decorate();
        }
        VisibilityRoles role = StringUtil.isNotNullorEmpty(document.getUploadedRole()) ?
                VisibilityRoles.valueOf(document.getUploadedRole().toUpperCase()) : VisibilityRoles.PANORAMA;
        documentDto.setUploadedRole(role.getDescription());
        return documentDto;
    }
}
