package com.bt.nextgen.api.statements.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

/**
 * Represents Document from from filenet. <b>Make sure while modifying none of the bean property are primitive type -
 * this impacts the ability to modify the document properties in filenet.</>
 */

@SuppressWarnings("checkstyle:com.puppycrawl.tools.checkstyle.checks.sizes.MethodCountCheck")
public class DocumentDto extends BaseDto implements KeyedDto<DocumentKey> {
    private DocumentKey key;
    @NotNull
    private String documentName;
    private String documentType;
    private String financialYear;
    private DateTime uploadedDate;
    private String uploadedBy;
    @NotNull
    private String status;
    private Boolean favourite;
    private Boolean audit;
    private Integer visible;
    private String version;
    private String fileType;
    private BigInteger size;
    private String documentTitleCode;
    private String uploadedRole;
    private String fileExtension;
    private String changeToken;
    private String documentSubType;
    private String fileName;
    private List<DomainApiErrorDto> warnings;

    private String businessArea;
    private String relationshipId;
    private String relationshipType;
    private String sourceId;
    private String externalId;
    private String batchId;
    private String abcOrderId;
    private String expiryDate;
    private String activity;
    private String modelReportId;
    private String documentSubType2;
    private String startDate;
    private String endDate;
    private String addedByName;
    private boolean deletable;
    private String documentTypeLabel;
    private boolean softDeleted;
    private boolean restoredDeleted;
    private boolean permanent;

    private String deletedByUserId;
    private String restoredByUserId;
    private DateTime deletedOn;
    private DateTime restoredOn;
    private String deletedByRole;
    private String restoreByRole;
    private String deletedByName;
    private String restoreByName;
    private String updatedByID;
    private String updatedByRole;
    private String updatedByName;
    private DateTime lastModificationDate;
    private String documentSubType2Label;

    private List<SupplimentaryDocument> supplimentaryDocumentList;

    @SuppressWarnings("findbugs:EI_EXPOSE_REP")
    public byte[] getDocumentBytes() {
        return documentBytes;
    }

    @SuppressWarnings("findbugs:EI_EXPOSE_REP2")
    public void setDocumentBytes(byte[] documentBytes) {
        this.documentBytes = documentBytes;
    }

    private byte[] documentBytes;

    public String getUploadedRole() {
        return uploadedRole;
    }

    public void setUploadedRole(String uploadedRole) {
        this.uploadedRole = uploadedRole;
    }

    @Override
    public DocumentKey getKey() {
        return key;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public void setKey(DocumentKey key) {
        this.key = key;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getFinancialYear() {
        return financialYear;
    }

    public void setFinancialYear(String financialYear) {
        this.financialYear = financialYear;
    }

    public DateTime getUploadedDate() {
        return uploadedDate;
    }

    public void setUploadedDate(DateTime uploadedDate) {
        this.uploadedDate = uploadedDate;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public Boolean getAudit() {
        return audit;
    }

    public void setAudit(Boolean audit) {
        this.audit = audit;
    }

    public Integer getVisible() {
        return visible;
    }

    public void setVisible(Integer visible) {
        this.visible = visible;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public BigInteger getSize() {
        return size;
    }

    public void setSize(BigInteger size) {
        this.size = size;
    }

    public String getDocumentTitleCode() {
        return documentTitleCode;
    }

    public void setDocumentTitleCode(String documentTitleCode) {
        this.documentTitleCode = documentTitleCode;
    }

    public String getChangeToken() {
        return changeToken;
    }

    public void setChangeToken(String changeToken) {
        this.changeToken = changeToken;
    }

    public String getDocumentSubType() {
        return documentSubType;
    }

    public void setDocumentSubType(String documentSubType) {
        this.documentSubType = documentSubType;
    }

    public List<SupplimentaryDocument> getSupplimentaryDocumentList() {
        return supplimentaryDocumentList;
    }

    public void setSupplimentaryDocumentList(List<SupplimentaryDocument> supplimentaryDocumentList) {
        this.supplimentaryDocumentList = supplimentaryDocumentList;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<DomainApiErrorDto> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<DomainApiErrorDto> warnings) {
        this.warnings = warnings;
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

    public String getAddedByName() {
        return addedByName;
    }

    public void setAddedByName(String addedByName) {
        this.addedByName = addedByName;
    }

    public String getRelationshipId() {
        return relationshipId;
    }

    public void setRelationshipId(String relationshipId) {
        this.relationshipId = relationshipId;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getAbcOrderId() {
        return abcOrderId;
    }

    public void setAbcOrderId(String abcOrderId) {
        this.abcOrderId = abcOrderId;
    }

    public String getBusinessArea() {
        return businessArea;
    }

    public void setBusinessArea(String businessArea) {
        this.businessArea = businessArea;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    public String getDocumentTypeLabel() {
        return documentTypeLabel;
    }

    public void setDocumentTypeLabel(String documentTypeLabel) {
        this.documentTypeLabel = documentTypeLabel;
    }

    public boolean isSoftDeleted() {
        return softDeleted;
    }

    public void setSoftDeleted(boolean softDeleted) {
        this.softDeleted = softDeleted;
    }

    public boolean isRestoredDeleted() {
        return restoredDeleted;
    }

    public void setRestoredDeleted(boolean restoredDeleted) {
        this.restoredDeleted = restoredDeleted;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
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

    public String getDeletedByRole() {
        return deletedByRole;
    }

    public void setDeletedByRole(String deletedByRole) {
        this.deletedByRole = deletedByRole;
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

    public DateTime getLastModificationDate() {
        return lastModificationDate;
    }

    public String getDocumentSubType2Label() {
        return documentSubType2Label;
    }

    public void setDocumentSubType2Label(String documentSubType2Label) {
        this.documentSubType2Label = documentSubType2Label;
    }

    public void setLastModificationDate(DateTime lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }
}