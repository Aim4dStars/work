package com.bt.nextgen.api.tracking.model;

import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.core.repository.OnboardingApplicationStatus;

public class DirectTrackingDto extends BaseDto implements KeyedDto<ClientApplicationKey> {

    private String encryptedAccountId;
    private ClientApplicationKey key;
    private OnboardingApplicationStatus status;

    @Override
    public ClientApplicationKey getKey() {
        return key;
    }

    public String getEncryptedAccountId() {
        return encryptedAccountId;
    }

    public void setEncryptedAccountId(String encryptedAccountId) {
        this.encryptedAccountId = encryptedAccountId;
    }

    public void setKey(ClientApplicationKey key) {
        this.key = key;
    }

    public OnboardingApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(OnboardingApplicationStatus status) {
        this.status = status;
    }
}
