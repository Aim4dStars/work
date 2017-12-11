package com.bt.nextgen.service.integration.uar;

import com.google.api.client.util.DateTime;

import java.util.Date;
import java.util.List;

/**
 * Created by L081012 on 13/01/2016.
 */

public interface UarDetail {

    public String getBrokerId();
    public void setBrokerId(String brokerId);
    public String getJobId();
    public void setJobId(String jobId);
    public String getPersonId();
    public void setPersonId(String personId);
    public String getPermissionId();
    public void setPermissionId(String permissionId);
    public Date getLastUarDate();
    public void setLastUarDate(Date lastUarDate);
    public String getLastUarOrderId();
    public void setLastUarOrderId(String lastUarOrderId);
    public String getDecisionId();
    public void setDecisionId(String decisionId);
    public String getDecisionDocId();
    public void setDecisionDocId(String decisionDocId);
    public String getApproverUserId();
    public void setApproverUserId(String approverUserId);
    public Date getUarDoneDate();
    public void setUarDoneDate(Date uarDoneDate);
    public String getBrokerName();
    public void setBrokerName(String brokerName);
    public String getPersonName();
    public void setPersonName(String personName);
    public String getPermissionName();
    public void setPermissionName(String permissionName);
    public String getCurrPermissionId();
    public void setCurrPermissionId(String currPermissionId);
    public String getCurrPermissionName();
    public void setCurrPermissionName(String currPermissionName);
    public Boolean getIsInvalid();
    public void setIsInvalid(Boolean isInvalid);
    public Boolean getIsFrozen();
    public void setIsFrozen(Boolean isFrozen);
    public List<UarError> getErrorList();
    public void setErrorList(List<UarError> errorList);
}
