package com.bt.nextgen.api.draftaccount.model.form.v1;

import javax.xml.datatype.XMLGregorianCalendar;

import com.bt.nextgen.api.draftaccount.model.form.IIdentityDocument;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.DocumentId;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.IdentificationTypeEnum;
import com.bt.nextgen.api.draftaccount.util.XMLGregorianCalendarUtil;

/**
 * Created by F030695 on 29/03/2016.
 */
class IdentityDocument implements IIdentityDocument {

    private final DocumentId documentId;
    private final IdentificationTypeEnum identificationType;

    public IdentityDocument(DocumentId documentId, IdentificationTypeEnum identificationType) {
        this.documentId = documentId;
        this.identificationType = identificationType;
    }

    @Override
    public String getDocumentNumber() {
        return documentId.getDocumentNumber();
    }

    @Override
    public String getDocumentIssuer() {
        return documentId.getDocumentIssuer();
    }

    @Override
    public XMLGregorianCalendar getIssueDate() {
        return XMLGregorianCalendarUtil.date(documentId.getIssueDate(), "dd/MM/yyyy");
    }

    @Override
    public XMLGregorianCalendar getExpiryDate() {
        return XMLGregorianCalendarUtil.date(documentId.getExpiryDate(), "dd/MM/yyyy");
    }

    @Override
    public String getEnglishTranslation() {
        return documentId.getEnglishTranslation();
    }

    @Override
    public boolean isEnglishTranslationSighted() {
        return Boolean.parseBoolean(documentId.getEnglishTranslation());
    }

    @Override
    public String getVerificationSource() {
        return documentId.getVerificationSource();
    }

    @Override
    public boolean isVerificationSourceOriginal() {
        return Boolean.parseBoolean(documentId.getVerificationSource());
    }

    @Override
    public IdentificationTypeEnum getIdentificationType() {
        return identificationType;
    }
}