package com.bt.nextgen.api.tracking.model;


import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

public class OnboardingClientStatusDto extends BaseDto implements KeyedDto<ClientApplicationKey> {

    private boolean clientOnboardingCompleted;
    private ClientApplicationKey key;

    public OnboardingClientStatusDto(boolean clientOnboardingCompleted) {
        this.clientOnboardingCompleted = clientOnboardingCompleted;
    }

    @Override
    public ClientApplicationKey getKey() {
        return key;
    }

    public void setKey(ClientApplicationKey key) {
        this.key = key;
    }

    public boolean isClientOnboardingCompleted() {
        return clientOnboardingCompleted;
    }
}
