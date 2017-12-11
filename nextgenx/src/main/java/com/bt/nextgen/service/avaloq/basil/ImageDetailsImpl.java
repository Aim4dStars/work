package com.bt.nextgen.service.avaloq.basil;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by M035995 on 27/09/2016.
 */
public class ImageDetailsImpl implements ImageDetails {

    private String documentId;

    private String documentURL;

    private String mimeType;

    private DateTime documentEntryDate;

    private List<DocumentProperties> documentPropertiesList = new ArrayList<>();

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void setDocumentURL(String documentURL) {
        this.documentURL = documentURL;
    }

    public void setDocumentEntryDate(DateTime documentEntryDate) {
        this.documentEntryDate = documentEntryDate;
    }

    public void setDocumentPropertiesList(List<DocumentProperties> documentPropertiesList) {
        this.documentPropertiesList = documentPropertiesList;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getDocumentURL() {
        return documentURL;
    }

    public DateTime getDocumentEntryDate() {
        return documentEntryDate;
    }

    public List<DocumentProperties> getDocumentPropertiesList() {
        return documentPropertiesList;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
