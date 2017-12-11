package com.bt.nextgen.serviceops.model;

/**
 * Created by L072457 on 6/07/2015.
 */
public class DocumentFilterModel {

    private String accountId;
    private String documentId;
    private String name;
    private String accountNumber;
    private String documentType;
    private String financialYear;
    private String uploadedBy;
    private String fromDate;
    private String toDate;
    private String relationshipType;
    private String documentStatus;
    private String auditFlag;
    private String nameSearchToken;
    private String documentSubType;
    private String softDeleted;
    private String documentSubSubType;

    public DocumentFilterModel() {
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
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

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    public String getDocumentStatus() {
        return documentStatus;
    }

    public void setDocumentStatus(String documentStatus) {
        this.documentStatus = documentStatus;
    }

    public String getAuditFlag() {
        return auditFlag;
    }

    public void setAuditFlag(String auditFlag) {
        this.auditFlag = auditFlag;
    }

    public String getNameSearchToken() {
        return nameSearchToken;
    }

    public void setNameSearchToken(String nameSearchToken) {
        this.nameSearchToken = nameSearchToken;
    }

    public String getDocumentSubType() {
        return documentSubType;
    }

    public void setDocumentSubType(String documentSubType) {
        this.documentSubType = documentSubType;
    }

    public String getSoftDeleted() {
        return softDeleted;
    }

    public void setSoftDeleted(String softDeleted) {
        this.softDeleted = softDeleted;
    }

    public String getDocumentSubSubType() {
        return documentSubSubType;
    }

    public void setDocumentSubSubType(String documentSubSubType) {
        this.documentSubSubType = documentSubSubType;
    }
}
