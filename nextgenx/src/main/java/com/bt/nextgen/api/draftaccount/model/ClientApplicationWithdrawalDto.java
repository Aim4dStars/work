package com.bt.nextgen.api.draftaccount.model;


import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

public class ClientApplicationWithdrawalDto extends BaseDto implements KeyedDto<ClientApplicationKey> {
    private ClientApplicationKey key;
    private boolean isWithdrawn;

    public ClientApplicationWithdrawalDto(ClientApplicationKey key) {
        this.key = key;
    }

    @Override
    public ClientApplicationKey getKey() {
        return key;
    }

    public void setKey(ClientApplicationKey key) {
        this.key = key;
    }

    public boolean isWithdrawn() {
        return isWithdrawn;
    }

    public void setWithdrawn(boolean isWithdrawn) {
        this.isWithdrawn = isWithdrawn;
    }
}
