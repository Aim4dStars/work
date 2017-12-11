package com.bt.nextgen.api.uar.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.integration.uar.UarDetail;

import java.math.BigDecimal;
import java.util.List;


/**
 * Created by L069552 on 16/09/2015.
 */
public class UarDto extends BaseDto {

    private String userName;
    private String userPersonId;
    private String userRole;
    private String userAdviserName;
    private String userAdviserId;
    private String userLinkedTo;
    private int daysSinceLastUar;
    private String lastUarDate;
    private String userPermissionLevel;
    private String currUserPermissionLevel;
    private String uarStatus;
    private String uarAction;
    private Boolean isInvalid;
    private Boolean isFrozen;
    private String uarType;
    private String jobId;
    private String brokerId;
    private String rowId;
    private BigDecimal recordIndex;
    private List<ServiceError> errors;

    public String getRowId() {
        return rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserAdviserName() {
        return userAdviserName;
    }

    public void setUserAdviserName(String userAdviserName) {
        this.userAdviserName = userAdviserName;
    }

    public String getUserAdviserId() {
        return userAdviserId;
    }

    public void setUserAdviserId(String userAdviserId) {
        this.userAdviserId = userAdviserId;
    }

    public String getUserLinkedTo() {
        return userLinkedTo;
    }

    public void setUserLinkedTo(String userLinkedTo) {
        this.userLinkedTo = userLinkedTo;
    }

    public int getDaysSinceLastUar() {
        return daysSinceLastUar;
    }

    public void setDaysSinceLastUar(int daysSinceLastUar) {
        this.daysSinceLastUar = daysSinceLastUar;
    }

    public String getLastUarDate() {
        return lastUarDate;
    }

    public void setLastUarDate(String lastUarDate) {
        this.lastUarDate = lastUarDate;
    }

    public String getUserPermissionLevel() {
        return userPermissionLevel;
    }

    public void setUserPermissionLevel(String userPermissionLevel) {
        this.userPermissionLevel = userPermissionLevel;
    }

    public String getUarStatus() {
        return uarStatus;
    }

    public void setUarStatus(String uarStatus) {
        this.uarStatus = uarStatus;
    }

    public String getUarAction() {
        return uarAction;
    }

    public void setUarAction(String uarAction) {
        this.uarAction = uarAction;
    }

    public String getCurrUserPermissionLevel() {
        return currUserPermissionLevel;
    }

    public void setCurrUserPermissionLevel(String currUserPermissionLevel) {
        this.currUserPermissionLevel = currUserPermissionLevel;
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

    public String getUserPersonId() {
        return userPersonId;
    }

    public void setUserPersonId(String userPersonId) {
        this.userPersonId = userPersonId;
    }

    public String getUarType() {
        return uarType;
    }

    public void setUarType(String uarType) {
        this.uarType = uarType;
    }

    public BigDecimal getRecordIndex() {
        return recordIndex;
    }

    public void setRecordIndex(BigDecimal recordIndex) {
        this.recordIndex = recordIndex;
    }
}
