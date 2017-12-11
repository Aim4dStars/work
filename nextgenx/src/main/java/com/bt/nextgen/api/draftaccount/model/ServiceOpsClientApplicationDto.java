package com.bt.nextgen.api.draftaccount.model;


import org.joda.time.DateTime;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.serviceops.service.ServiceOpsClientApplicationStatus;

public class ServiceOpsClientApplicationDto extends BaseDto implements KeyedDto<ClientApplicationKey> {

    private String adviserName;
    private String referenceNumber;
    private ClientApplicationKey key;
    private ServiceOpsClientApplicationStatus status;
    private DateTime lastModified;
    private String lastModifiedByName;
    private String productName;
    private String failureMessage;
    private String accountType;
    private String accountName;
    private String accountNumber;

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

	public String getFailureMessage()
	{
		return failureMessage;
	}

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }


    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAdviserName() {
        return adviserName;
    }

    public void setAdviserName(String adviserName) {
        this.adviserName = adviserName;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public ClientApplicationKey getKey() {
        return key;
    }

    public void setKey(ClientApplicationKey key) {
        this.key = key;
    }

    public ServiceOpsClientApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceOpsClientApplicationStatus status) {
        this.status = status;
    }

    public DateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(DateTime lastModified) {
        this.lastModified = lastModified;
    }

    public String getLastModifiedByName() {
        return lastModifiedByName;
    }

    public void setLastModifiedByName(String lastModifiedByName) {
        this.lastModifiedByName = lastModifiedByName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
