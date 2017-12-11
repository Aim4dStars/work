package com.bt.nextgen.service.avaloq.basil;

import org.joda.time.DateTime;

/**
 * Created by M035995 on 27/09/2016.
 */
public interface DocumentProperties {

    public String getDocumentPropertyStringValue();

    public DateTime getDocumentPropertyDateValue();

    public DocumentProperty getDocumentPropertyName();
}
