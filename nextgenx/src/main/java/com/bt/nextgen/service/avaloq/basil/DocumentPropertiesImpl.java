package com.bt.nextgen.service.avaloq.basil;

import org.joda.time.DateTime;

/**
 * Created by M035995 on 27/09/2016.
 */
public class DocumentPropertiesImpl implements DocumentProperties {

    private DocumentProperty documentPropertyName;

    private String documentPropertyStringValue;

    private DateTime documentPropertyDateValue;

    public void setDocumentPropertyName(DocumentProperty documentPropertyName) {
        this.documentPropertyName = documentPropertyName;
    }

    public void setDocumentPropertyStringValue(String documentPropertyStringValue) {
        this.documentPropertyStringValue = documentPropertyStringValue;
    }

    public void setDocumentPropertyDateValue(DateTime documentPropertyDateValue) {
        this.documentPropertyDateValue = documentPropertyDateValue;
    }

    public String getDocumentPropertyStringValue() {
        return documentPropertyStringValue;
    }

    public DateTime getDocumentPropertyDateValue() {
        return documentPropertyDateValue;
    }

    @Override
    public DocumentProperty getDocumentPropertyName() {
        return documentPropertyName;
    }
}
