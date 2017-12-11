package com.bt.nextgen.api.statements.decorator;

import com.bt.nextgen.api.statements.util.DocumentsDtoServiceUtil;
import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.cmis.constants.DocumentCategories;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.financialdocument.Document;

/**
 * Decorator for the Financial documents
 */
public class FinancialDocumentDecorator implements DtoDecorator {
    private DocumentDto documentDto;
    private Document document;
    private DtoDecorator dtoDecorator;
    private WrapAccount account;
    private UserExperience userExperience;

    public FinancialDocumentDecorator(DocumentDto documentDto, Document document, DtoDecorator dtoDecorator,
                                      WrapAccount account, UserExperience userExperience) {
        this.documentDto = documentDto;
        this.document = document;
        this.dtoDecorator = dtoDecorator;
        this.account = account;
        this.userExperience = userExperience;
    }

    @Override
    public DocumentDto decorate() {
        if (documentDto == null) {
            documentDto = new DocumentDto();
        }
        DocumentCategories documentType = DocumentCategories.forCode(document.getDocumentType());
        if (account != null &&DocumentCategories.STATEMENTS.equals(documentType)) {
            documentDto.setSupplimentaryDocumentList(DocumentsDtoServiceUtil.getSupplementaryDocuments(document, account, userExperience));
        }
        if (dtoDecorator != null) {
            documentDto = dtoDecorator.decorate();
        }
        return documentDto;
    }
}
