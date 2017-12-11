package com.bt.nextgen.service.avaloq.basil;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by M035995 on 27/09/2016.
 */
public interface ImageDetails {

    public String getDocumentId();

    public String getDocumentURL();

    public String getMimeType();

    public DateTime getDocumentEntryDate();

    public List<DocumentProperties> getDocumentPropertiesList();
}
