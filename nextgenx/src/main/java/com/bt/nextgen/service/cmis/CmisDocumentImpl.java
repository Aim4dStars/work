package com.bt.nextgen.service.cmis;

import com.bt.nextgen.service.cmis.annotation.Column;
import com.bt.nextgen.service.cmis.constants.DocumentConstants;
import com.bt.nextgen.service.cmis.converter.ConverterMapper;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.financialdocument.Document;
import com.bt.nextgen.service.integration.financialdocument.DocumentKey;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigInteger;

/**
 * Concrete implementation of Document interface.
 */
@SuppressWarnings("findbugs:EI_EXPOSE_REP2")
public class CmisDocumentImpl implements Document {

    private AccountKey accountKey;
    @Column(name = DocumentConstants.COLUMN_OBJECT_ID, converter = ConverterMapper.CmisPropertyId, updatable = false)
    private DocumentKey documentKey;
    @Column(name = DocumentConstants.COLUMN_RELATIONSHIP_ID)
    private String relationshipId;
    @Column(name = DocumentConstants.COLUMN_DOCUMENT_NAME)
    private String documentName;
    @Column(name = DocumentConstants.COLUMN_DOCUMENT_FILENAME)
    private String fileName;
    @Column(name = DocumentConstants.COLUMN_DOCUMENT_STATUS)
    private String status;
    @Column(name = DocumentConstants.COLUMN_DOCUMENT_TYPE)
    private String documentType;
    @Column(name = DocumentConstants.COLUMN_DOCUMENT_TITLE_CODE)
    private String documentTitleCode;
    @Column(name = DocumentConstants.COLUMN_AUDIT, converter = ConverterMapper.CmisPropertyBoolean)
    private Boolean audit;
    @Column(name = DocumentConstants.COLUMN_ADDEDBY_ID)
    private String uploadedBy;
    @Column(name = DocumentConstants.COLUMN_FILE_SIZE, converter = ConverterMapper.CmisPropertyInteger, updatable = false)
    private BigInteger size;
    @Column(name = DocumentConstants.COLUMN_MIME_TYPE, updatable = false)
    private String mimeType;
    @Column(name = DocumentConstants.COLUMN_UPLOADED_DATE, converter = ConverterMapper.CmisPropertyDateTime)
    private DateTime uploadedDate;
    @Column(name = DocumentConstants.COLUMN_FINANCIAL_YEAR, converter = ConverterMapper.CmisPropertyEmptyString)
    private String financialYear;
    @Column(name = DocumentConstants.COLUMN_BUSINESS_AREA)
    private String panoramaipBusinessArea;
    @Column(name = DocumentConstants.COLUMN_RELATIONSHIP_TYPE)
    private String panoramaipRelationshipType;
    @Column(name = DocumentConstants.COLUMN_START_DATE, converter = ConverterMapper.CmisPropertyDateTimeToString)
    private String startDate;
    @Column(name = DocumentConstants.COLUMN_END_DATE, converter = ConverterMapper.CmisPropertyDateTimeToString)
    private String endDate;
    @Column(name = DocumentConstants.COLUMN_ADDEDBY_ROLE)
    private String uploadedRole;
    @Column(name = DocumentConstants.COLUMN_FILE_EXTENSION)
    private String fileExtension;
    @Column(name = DocumentConstants.COLUMN_VISIBILITY, converter = ConverterMapper.CmisPropertyInteger)
    private BigInteger visibility;
    @Column(name = DocumentConstants.COLUMN_ORDER_ID)
    private String orderId;
    @Column(name = DocumentConstants.COLUMN_ADDEDBY_NAME)
    private String addedByName;
    @Column(name = DocumentConstants.COLUMN_CHANGE_TOKEN, updatable = false)
    private String changeToken;
    @Column(name = DocumentConstants.COLUMN_SOURCE_ID, converter = ConverterMapper.CmisPropertyEmptyString)
    private String sourceId;
    @Column(name = DocumentConstants.COLUMN_SUB_CATEGORY)
    private String documentSubType;
    @Column(name = DocumentConstants.COLUMN_EXTERNAL_ID)
    private String externalId;
    @Column(name = DocumentConstants.COLUMN_BATCH_ID)
    private String batchId;
    @Column(name = DocumentConstants.COLUMN_EXPIRY_DATE, converter = ConverterMapper.CmisPropertyDateTimeToString)
    private String expiryDate;
    @Column(name = DocumentConstants.COLUMN_ACTIVITY)
    private String activity;
    @Column(name = DocumentConstants.COLUMN_MODEL_REPORT_ID)
    private String modelReportId;
    @Column(name = DocumentConstants.COLUMN_SUB_CATEGORY_2)
    private String documentSubType2;
    @Column(name = DocumentConstants.COLUMN_DELETEDBY_ID)
    private String deletedByUserId;
    @Column(name = DocumentConstants.COLUMN_RESTOREDBY_ID)
    private String restoredByUserId;
    @Column(name = DocumentConstants.COLUMN_DELETED_ON, converter = ConverterMapper.CmisPropertyDateTime)
    private DateTime deletedOn;
    @Column(name = DocumentConstants.COLUMN_RESTORED_ON, converter = ConverterMapper.CmisPropertyDateTime)
    private DateTime restoredOn;
    @Column(name = DocumentConstants.COLUMN_DELETEDBY_ROLE)
    private String deletedByRole;
    @Column(name = DocumentConstants.COLUMN_RESTOREDBY_ROLE)
    private String restoreByRole;
    @Column(name = DocumentConstants.COLUMN_DELETEDBY_NAME)
    private String deletedByName;
    @Column(name = DocumentConstants.COLUMN_RESTOREDBY_NAME)
    private String restoreByName;
    @Column(name = DocumentConstants.COLUMN_DELETED)
    private String deleted;
    @Column(name = DocumentConstants.COLUMN_PERMANENT)
    private String permanent;
    @Column(name = DocumentConstants.COLUMN_UPDATEDBY_ID)
    private String updatedByID;
    @Column(name = DocumentConstants.COLUMN_UPDATEDBY_ROLE)
    private String updatedByRole;
    @Column(name = DocumentConstants.COLUMN_UPDATEDBY_NAME)
    private String updatedByName;
    @Column(name = DocumentConstants.COLUMN_LAST_MODIFIED_DATE, converter = ConverterMapper.CmisPropertyDateTime, updatable = false)
    private DateTime lastModificationDate;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");


    public String getDeletedByRole() {
        return deletedByRole;
    }

    public void setDeletedByRole(String deletedByRole) {
        this.deletedByRole = deletedByRole;
    }

    public String getDeletedByUserId() {
        return deletedByUserId;
    }

    public void setDeletedByUserId(String deletedByUserId) {
        this.deletedByUserId = deletedByUserId;
    }

    public String getRestoredByUserId() {
        return restoredByUserId;
    }

    public void setRestoredByUserId(String restoredByUserId) {
        this.restoredByUserId = restoredByUserId;
    }

    public DateTime getDeletedOn() {
        return deletedOn;
    }

    public void setDeletedOn(DateTime deletedOn) {
        this.deletedOn = deletedOn;
    }

    public DateTime getRestoredOn() {
        return restoredOn;
    }

    public void setRestoredOn(DateTime restoredOn) {
        this.restoredOn = restoredOn;
    }

    public String getRestoreByRole() {
        return restoreByRole;
    }

    public void setRestoreByRole(String restoreByRole) {
        this.restoreByRole = restoreByRole;
    }

    public String getDeletedByName() {
        return deletedByName;
    }

    public void setDeletedByName(String deletedByName) {
        this.deletedByName = deletedByName;
    }

    public String getRestoreByName() {
        return restoreByName;
    }

    public void setRestoreByName(String restoreByName) {
        this.restoreByName = restoreByName;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getPermanent() {
        return permanent;
    }

    public void setPermanent(String permanent) {
        this.permanent = permanent;
    }

    public String getUpdatedByID() {
        return updatedByID;
    }

    public void setUpdatedByID(String updatedByID) {
        this.updatedByID = updatedByID;
    }

    public String getUpdatedByRole() {
        return updatedByRole;
    }

    public void setUpdatedByRole(String updatedByRole) {
        this.updatedByRole = updatedByRole;
    }

    public String getUpdatedByName() {
        return updatedByName;
    }

    public void setUpdatedByName(String updatedByName) {
        this.updatedByName = updatedByName;
    }

    public String getUploadedRole() {
        return uploadedRole;
    }

    public void setUploadedRole(String uploadedRole) {
        this.uploadedRole = uploadedRole;
    }

    private byte[] data;

    public AccountKey getAccountKey() {
        return accountKey;
    }

    public void setAccountKey(AccountKey accountKey) {
        this.accountKey = accountKey;
    }

    @Override
    public DocumentKey getDocumentKey() {
        return documentKey;
    }

    public void setDocumentKey(DocumentKey documentKey) {
        this.documentKey = documentKey;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentTitleCode() {
        return documentTitleCode;
    }

    public void setDocumentTitleCode(String documentTitleCode) {
        this.documentTitleCode = documentTitleCode;
    }

    public Boolean getAudit() {
        return audit;
    }

    public void setAudit(Boolean audit) {
        this.audit = audit;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public BigInteger getSize() {
        return size;
    }

    public void setSize(BigInteger size) {
        this.size = size;
    }

    @Override
    public DateTime getPeriodEndDate() {
        return endDate != null ? new DateTime(dateTimeFormatter.parseDateTime(endDate)) : null;
    }

    @Override
    public DateTime getPeriodStartDate() {
        return startDate != null ? new DateTime(dateTimeFormatter.parseDateTime(startDate)) : null;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public DateTime getUploadedDate() {
        return uploadedDate;
    }

    public void setUploadedDate(DateTime uploadedDate) {
        this.uploadedDate = uploadedDate;
    }

    public String getFinancialYear() {
        return financialYear;
    }

    public void setFinancialYear(String financialYear) {
        this.financialYear = financialYear;
    }

    public String getPanoramaipBusinessArea() {
        return panoramaipBusinessArea;
    }

    public void setPanoramaipBusinessArea(String panoramaipBusinessArea) {
        this.panoramaipBusinessArea = panoramaipBusinessArea;
    }

    public String getPanoramaipRelationshipType() {
        return panoramaipRelationshipType;
    }

    public void setPanoramaipRelationshipType(String panoramaipRelationshipType) {
        this.panoramaipRelationshipType = panoramaipRelationshipType;
    }

    public String getRelationshipId() {
        return relationshipId;
    }

    public void setRelationshipId(String relationshipId) {
        this.relationshipId = relationshipId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    @Override
    public BigInteger getVisibility() {
        return visibility;
    }

    public void setVisibility(BigInteger visibility) {
        this.visibility = visibility;
    }

    @Override
    public String getOrderId() {
        return orderId;
    }

    @Override
    public String getAddedByName() {
        return addedByName;
    }

    public void setAddedByName(String addedByName) {
        this.addedByName = addedByName;
    }

    @Override
    public String getChangeToken() {
        return changeToken;
    }

    public void setChangeToken(String changeToken) {
        this.changeToken = changeToken;
    }

    @Override
    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    @SuppressWarnings("findbugs:EI_EXPOSE_REP")
    public byte[] getData() {
        return this.data;
    }

    @SuppressWarnings("findbugs:EI_EXPOSE_REP2")
    public void setData(byte[] data) {
        this.data = data;
    }

    public String getDocumentSubType() {
        return documentSubType;
    }

    public void setDocumentSubType(String documentSubType) {
        this.documentSubType = documentSubType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getModelReportId() {
        return modelReportId;
    }

    public void setModelReportId(String modelReportId) {
        this.modelReportId = modelReportId;
    }

    public String getDocumentSubType2() {
        return documentSubType2;
    }

    public void setDocumentSubType2(String documentSubType2) {
        this.documentSubType2 = documentSubType2;
    }

    public DateTime getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(DateTime lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }
}
