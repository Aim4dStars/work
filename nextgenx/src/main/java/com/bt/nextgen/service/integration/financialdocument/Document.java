package com.bt.nextgen.service.integration.financialdocument;

import org.joda.time.DateTime;

import java.math.BigInteger;

/**
 * Document class it's combines property and bytes of the document
 */
public interface Document extends BaseCmisDocument<DocumentKey> {

    Boolean getAudit();

    String getRelationshipId();

    String getStartDate();

    String getEndDate();

    String getFileExtension();

    BigInteger getVisibility();

    byte[] getData();

    String getStatus();

    String getDocumentType();

    String getUploadedBy();

    DateTime getUploadedDate();

    String getFinancialYear();

    String getMimeType();

    String getUploadedRole();

    String getOrderId();

    String getAddedByName();

    String getChangeToken();

    String getSourceId();

    String getDocumentSubType();

    String getFileName();

    String getExternalId();

    String getBatchId();

    String getExpiryDate();

    String getActivity();

    String getModelReportId();

    String getDocumentSubType2();

    String getPanoramaipBusinessArea();

    String getPanoramaipRelationshipType();

    String getPermanent();

    String getDeletedByUserId();

    String getRestoredByUserId();

    DateTime getDeletedOn();

    DateTime getRestoredOn();

    String getDeletedByRole();

    String getRestoreByRole();

    String getDeletedByName();

    String getRestoreByName();

    String getDeleted();

    String getUpdatedByID();

    String getUpdatedByRole();

    String getUpdatedByName();

    DateTime getLastModificationDate();
}
