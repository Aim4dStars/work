package com.bt.nextgen.service.integration.uar;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.google.api.client.util.DateTime;

import java.util.Date;
import java.util.List;

// Created by L081012 on 13/01/2016.



@ServiceBean(xpath = "uar_rec")
public class UarDetailImpl implements UarDetail {

    @ServiceElement(xpath = "oe_id/val")
    private String brokerId;

    @ServiceElement(xpath = "oe_id/annot/displ_text")
    private String brokerName;

    @ServiceElement(xpath = "job_id/val")
    private String jobId;

    @ServiceElement(xpath = "person_id/val")
    private String personId;

    @ServiceElement(xpath = "person_id/annot/displ_text")
    private String personName;

    @ServiceElement(xpath = "job_oe_auth_role_id/val")
    private String permissionId;

    @ServiceElement(xpath = "job_oe_auth_role_id/annot/displ_text")
    private String permissionName;

    @ServiceElement(xpath = "curr_job_oe_auth_role_id/val")
    private String currPermissionId;

    @ServiceElement(xpath = "curr_job_oe_auth_role_id/annot/displ_text")
    private String currPermissionName;

    @ServiceElement(xpath = "last_uar_date/val")
    private Date lastUarDate;

    @ServiceElement(xpath = "last_uar_doc_id/val")
    private String lastUarOrderId;

    @ServiceElement(xpath="decsn_id/val")
    private String decisionId;

    @ServiceElement(xpath="decsn_doc_id/val")
    private String decisionDocId;

    @ServiceElement(xpath="user_id/val")
    private String approverUserId;

    @ServiceElement(xpath="timestamp/val")
    private Date uarDoneDate;

    @ServiceElement(xpath="is_invalid/val")
    private Boolean isInvalid;

    @ServiceElement(xpath="is_frozen/val")
    private Boolean isFrozen;

    @ServiceElementList(xpath="err_list/err_rec", type = UarErrorImpl.class)
    private List<UarError> errorList;

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

    public Date getLastUarDate() {
        return lastUarDate;
    }

    public void setLastUarDate(Date lastUarDate) {
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

    @Override
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

    public List<UarError> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<UarError> errorList) {
        this.errorList = errorList;
    }
}
