package com.bt.nextgen.service.integration.accountingsoftware.model;

import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.integration.account.AccountKey;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 *
 */
public class AccountingSoftwareImpl implements AccountingSoftware {

    @NotNull
    private AccountKey key;
    private SoftwareFeedStatus softwareFeedStatus;
    private AccountingSoftwareType softwareName;
    private List<ValidationError> validationErrors;


    public AccountKey getKey() {
        return key;
    }

    public SoftwareFeedStatus getSoftwareFeedStatus() {
        return softwareFeedStatus;
    }

    public AccountingSoftwareType getSoftwareName() {
        return softwareName;
    }

    public void setKey(AccountKey key) {
        this.key = key;
    }

    public void setSoftwareFeedStatus(SoftwareFeedStatus softwareFeedStatus) {
        this.softwareFeedStatus = softwareFeedStatus;
    }

    public void setSoftwareName(AccountingSoftwareType softwareName) {
        this.softwareName = softwareName;
    }

    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }
}
