package com.bt.nextgen.api.draftaccount.model.form;

import com.bt.nextgen.api.draftaccount.schemas.v1.base.IdentificationTypeEnum;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Created by m040398 on 14/03/2016.
 */
public interface IIdentityDocument {

    String getDocumentNumber();

    String getDocumentIssuer();

    XMLGregorianCalendar getIssueDate();

    XMLGregorianCalendar getExpiryDate();

    String getEnglishTranslation();

    boolean isEnglishTranslationSighted();

    String getVerificationSource();

    boolean isVerificationSourceOriginal();

    IdentificationTypeEnum getIdentificationType();
}
