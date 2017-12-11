package com.bt.nextgen.api.draftaccount.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;

public class ClientApplicationApprovalDto extends BaseDto implements KeyedDto<OnboardingApplicationKey> {

    private final OnboardingApplicationKey key;
    private Boolean active;

    public ClientApplicationApprovalDto(OnboardingApplicationKey key, Boolean active) {
        this.key = key;
        this.active = active;
    }

    @Override
    public OnboardingApplicationKey getKey() {
        return key;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
