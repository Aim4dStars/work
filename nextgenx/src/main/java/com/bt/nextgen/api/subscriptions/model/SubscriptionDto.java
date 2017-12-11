package com.bt.nextgen.api.subscriptions.model;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import org.joda.time.DateTime;

import java.util.List;

public class SubscriptionDto extends BaseDto implements KeyedDto<AccountKey> {
    private AccountKey key;
    private String accountName;
    private String adviserFirstName;
    private String adviserLastName;
    private String adviserName;
    private String serviceName;
    private String serviceType;
    private String status;
    private DateTime submitDate;
    private List<WorkFlowStatusDto> states;
    private String orderNumber;
    private DateTime lastUpdateDate;

    @Override
    public AccountKey getKey() {
        return key;
    }

    public void setKey(AccountKey key) {
        this.key = key;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAdviserFirstName() {
        return adviserFirstName;
    }

    public void setAdviserFirstName(String adviserFirstName) {
        this.adviserFirstName = adviserFirstName;
    }

    public String getAdviserLastName() {
        return adviserLastName;
    }

    public void setAdviserLastName(String adviserLastName) {
        this.adviserLastName = adviserLastName;
    }

    public String getAdviserName() {
        return adviserName;
    }

    public void setAdviserName(String adviserName) {
        this.adviserName = adviserName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<WorkFlowStatusDto> getStates() {
        return states;
    }

    public void setStates(List<WorkFlowStatusDto> states) {
        this.states = states;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public DateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(DateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public DateTime getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(DateTime submitDate) {
        this.submitDate = submitDate;
    }
}