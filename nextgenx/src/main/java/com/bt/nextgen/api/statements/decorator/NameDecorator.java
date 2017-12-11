package com.bt.nextgen.api.statements.decorator;

import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.api.statements.model.SupportedDocumentType;
import com.bt.nextgen.core.type.DateUtil;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.service.cmis.constants.DocumentCategories;
import com.bt.nextgen.service.integration.financialdocument.Document;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentType;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

/**
 *
 */
public class NameDecorator implements DtoDecorator {

    private DocumentDto documentDto;
    private DtoDecorator decorator;
    private Document document;

    public NameDecorator(DocumentDto documentDto, Document document) {
        this.documentDto = documentDto;
        this.document = document;
    }

    public NameDecorator(DtoDecorator decorator, DocumentDto documentDto, Document document) {
        this.decorator = decorator;
        this.documentDto = documentDto;
        this.document = document;
    }

    @Override
    public DocumentDto decorate() {
        DocumentCategories documentType = DocumentCategories.forCode(document.getDocumentType());
        if (DocumentCategories.STATEMENTS.equals(documentType) &&
                (document.getDocumentTitleCode() == null ||
                !FinancialDocumentType.UNKNOWN.equals(FinancialDocumentType.forCode(document.getDocumentTitleCode())))) {
            documentDto.setDocumentName(generateDocumentName());
        }
        else {
            documentDto.setDocumentName(getDocumentName());
        }
        if (decorator != null) {
            documentDto = decorator.decorate();
        }
        return documentDto;
    }

    private String generateDocumentName() {
        StringBuilder name = new StringBuilder();
        if (document.getDocumentTitleCode() == null) {
            name.append("Statement");
        }
        else {
            name.append(FinancialDocumentType.forCode(document.getDocumentTitleCode()).getDescription());
        }
        if (document.getStartDate() != null && document.getEndDate() != null) {
            DateTime dt = DateUtil.convertToDateTime(document.getStartDate(), "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            DateTime et = DateUtil.convertToDateTime(document.getEndDate(), "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            String startDate = ApiFormatter.asShortDate(dt);
            String endDate = ApiFormatter.asShortDate(et);
            name.append(" (");
            name.append(startDate);
            name.append(" - ");
            name.append(endDate);
            name.append(")");
        }
        name.append(".");
        name.append(SupportedDocumentType.forCode(document.getMimeType()).name().toLowerCase());
        return name.toString();
    }

    private String getDocumentName() {
        String documentName = document.getDocumentName();
        String extension = SupportedDocumentType.forCode(document.getMimeType()).name().toLowerCase();
        if (!StringUtils.endsWith(documentName, ("." + extension))) {
            documentName = documentName + "." + extension;
        }
        return documentName;
    }
}

