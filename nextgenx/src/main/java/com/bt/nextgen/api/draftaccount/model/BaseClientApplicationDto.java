package com.bt.nextgen.api.draftaccount.model;

import org.joda.time.DateTime;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.draftaccount.repository.ClientApplicationStatus;

/**
 * Base ClientApplicationDto POJO impl. Specific implemenations (old Map, Advised or Direct) must extend this class
 */
public abstract class BaseClientApplicationDto extends BaseDto implements ClientApplicationDto {

    private String adviserId;
    private String adviserName;
    private String referenceNumber;
    private ClientApplicationKey key;
    private boolean offline;
    private ClientApplicationStatus status;
    private DateTime lastModified;
    private String lastModifiedByName;
    private String productName;
    private String productId;

    public BaseClientApplicationDto() {
    }

    public BaseClientApplicationDto(ClientApplicationKey key) {
        this.key = key;
    }

    @Override
    public String getAdviserId() {
        return adviserId;
    }

    @Override
    public void setAdviserId(String adviserId) {
        this.adviserId = adviserId;
    }

    @Override
    public ClientApplicationKey getKey() {
        return key;
    }

    @Override
    public void setKey(ClientApplicationKey key) {
        this.key = key;
    }

    @Override
    public ClientApplicationStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(ClientApplicationStatus status) {
        this.status = status;
    }

    @Override
    public String getLastModifiedByName() {
        return lastModifiedByName;
    }

    @Override
    public void setLastModifiedByName(String lastModifiedByName) {
        this.lastModifiedByName = lastModifiedByName;
    }

    @Override
    public String getReferenceNumber() {
        return referenceNumber;
    }

    @Override
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    @Override
    public String getAdviserName() {
        return adviserName;
    }

    @Override
    public void setAdviserName(String adviserName) {
        this.adviserName = adviserName;
    }

    @Override
    public DateTime getLastModified() {
        return lastModified;
    }

    @Override
    public void setLastModified(DateTime lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public String getProductName() {
        return productName;
    }

    @Override
    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public String getProductId() {
        return productId;
    }

    @Override
    public void setProductId(String productId) {
        this.productId = productId;
    }

    @Override
    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    @Override
    public boolean isOffline() {
        return offline;
    }

    protected String toString(Object o) {
        return o == null ? null : o.toString();
    }
}
