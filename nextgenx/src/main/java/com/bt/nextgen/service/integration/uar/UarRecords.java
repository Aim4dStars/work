package com.bt.nextgen.service.integration.uar;


import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.ServiceErrors;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by l069679 on 15/06/2016.
 */
public class UarRecords {
    private String brokerId;
    private String brokerName;
    private String jobId;
    private String personId;
    private String personName;
    private String permissionId;
    private String permissionName;
    private String currPermissionId;
    private String currPermissionName;
    private DateTime lastUarDate;
    private String lastUarOrderId;
    private String decisionId;
    private String decisionDocId;
    private String approverUserId;
    private Date uarDoneDate;
    private Boolean isInvalid;
    private Boolean isFrozen;
    private String recordType;
    private BigDecimal recordIndex;

    public BigDecimal getRecordIndex() {
        return recordIndex;
    }

    public void setRecordIndex(BigDecimal recordIndex) {
        this.recordIndex = recordIndex;
    }

    private List<ServiceError> errors;

    public String getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(String brokerId) {
        this.brokerId = brokerId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }

    public DateTime getLastUarDate() {
        return lastUarDate;
    }

    public void setLastUarDate(DateTime lastUarDate) {
        this.lastUarDate = lastUarDate;
    }

    public String getLastUarOrderId() {
        return lastUarOrderId;
    }

    public void setLastUarOrderId(String lastUarOrderId) {
        this.lastUarOrderId = lastUarOrderId;
    }

    public String getDecisionId() {
        return decisionId;
    }

    public void setDecisionId(String decisionId) {
        this.decisionId = decisionId;
    }

    public String getDecisionDocId() {
        return decisionDocId;
    }

    public void setDecisionDocId(String decisionDocId) {
        this.decisionDocId = decisionDocId;
    }

    public String getApproverUserId() {
        return approverUserId;
    }

    public void setApproverUserId(String approverUserId) {
        this.approverUserId = approverUserId;
    }

    public Date getUarDoneDate() {
        return uarDoneDate;
    }

    public void setUarDoneDate(Date uarDoneDate) {
        this.uarDoneDate = uarDoneDate;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getCurrPermissionId() {
        return currPermissionId;
    }

    public void setCurrPermissionId(String currPermissionId) {
        this.currPermissionId = currPermissionId;
    }

    public String getCurrPermissionName() {
        return currPermissionName;
    }

    public void setCurrPermissionName(String currPermissionName) {
        this.currPermissionName = currPermissionName;
    }

    public Boolean getIsInvalid() {
        return isInvalid;
    }

    public void setIsInvalid(Boolean isInvalid) {
        this.isInvalid = isInvalid;
    }

    public Boolean getIsFrozen() {
        return isFrozen;
    }

    public void setIsFrozen(Boolean isFrozen) {
        this.isFrozen = isFrozen;
    }

    public List<ServiceError> getErrors() {
        return errors;
    }

    public void setErrors(List<ServiceError> errors) {
        this.errors = errors;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }
}
