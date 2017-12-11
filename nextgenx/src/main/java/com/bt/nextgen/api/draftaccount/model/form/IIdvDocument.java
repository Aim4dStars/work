package com.bt.nextgen.api.draftaccount.model.form;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Created by m040398 on 14/03/2016.
 */
public interface IIdvDocument {
    public String getDocumentType();

    public String getName();

    public XMLGregorianCalendar getDocumentDate();

    public String getDocumentNumber();

    public String getVerifiedFrom();
}
