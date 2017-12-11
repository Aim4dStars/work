package com.bt.nextgen.api.draftaccount.model.form;

import com.bt.nextgen.api.draftaccount.schemas.v1.base.IdentificationTypeEnum;
import com.bt.nextgen.api.draftaccount.util.XMLGregorianCalendarUtil;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Map;

/**
 * @deprecated Should be using the v1 version of this class instead.
 */
@Deprecated
class IdentityDocument implements IIdentityDocument{

    private final IdentificationTypeEnum identificationType;
    private final Map<String, String> documentDetails;

    public IdentityDocument(IdentificationTypeEnum identificationType, Map<String, String> documentDetails) {

        this.identificationType = identificationType;
        this.documentDetails = documentDetails;
    }

    public String getDocumentNumber() {
        return documentDetails.get("documentNumber");
    }

    public String getDocumentIssuer() {
        return documentDetails.get("documentIssuer");
    }

    public XMLGregorianCalendar getIssueDate() {
        return XMLGregorianCalendarUtil.date(documentDetails.get("issueDate"), "dd/MM/yyyy");
    }

    public XMLGregorianCalendar getExpiryDate() {
        return XMLGregorianCalendarUtil.date(documentDetails.get("expiryDate"), "dd/MM/yyyy");
    }

    public String getEnglishTranslation() {
        return documentDetails.get("englishTranslation");
    }

    public boolean isEnglishTranslationSighted() {
        return "sighted".equalsIgnoreCase(this.getEnglishTranslation());
    }

    public String getVerificationSource() {
        return documentDetails.get("verificationSource");
    }

    public boolean isVerificationSourceOriginal() {
        return "original".equals(this.getVerificationSource());
    }


    public IdentificationTypeEnum getIdentificationType() {
        return identificationType;
    }
}