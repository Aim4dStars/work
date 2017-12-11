package com.bt.nextgen.api.statements.decorator;

import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.cmis.constants.DocumentCategories;
import com.bt.nextgen.service.cmis.constants.VisibilityRoles;
import com.bt.nextgen.service.integration.financialdocument.Document;
import org.apache.commons.lang3.StringUtils;

/**
 * Checks if the document is not Permanent and sets the deletable property of documentDto true / false.
 * Use case: if document is
 *
 */
public class DeletableDecorator implements DtoDecorator {

    private DocumentDto documentDto;
    private DtoDecorator decorator;
    private Document document;
    private JobRole activeRole;

    public DeletableDecorator(DocumentDto documentDto, DtoDecorator decorator, Document document, JobRole activeRole) {
        this.documentDto = documentDto;
        this.decorator = decorator;
        this.document = document;
        this.activeRole = activeRole;
    }

    @Override
    public DocumentDto decorate() {
        if (!DocumentCategories.STATEMENTS.equals(DocumentCategories.forCode(document.getDocumentType()))
                && (StringUtils.isEmpty(document.getPermanent())
                || "N".equalsIgnoreCase(document.getPermanent()))
                && !VisibilityRoles.PANORAMA.equals(VisibilityRoles.forRole(activeRole))
                ) {
            boolean isDeletable = VisibilityRoles.forRole(activeRole).isDeleteAllowed(document.getUploadedRole() == null ? VisibilityRoles.PANORAMA.name() : document.getUploadedRole());
            documentDto.setDeletable(isDeletable);
        }
        if (decorator != null) {
            documentDto = decorator.decorate();
        }
        return documentDto;
    }
}
