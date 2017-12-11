package com.bt.nextgen.api.accountassociates.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

public class AccountAssociateDto extends BaseDto implements KeyedDto<String> {
    private String encryptedAccountKey;
    private String accountName;
    private String clientName;
    private String encryptedClientKey;
    private Boolean isOwner;

    public Boolean getOwner() {
        return isOwner;
    }

    public void setOwner(Boolean owner) {
        isOwner = owner;
    }

    public String getEncryptedAccountKey() {
        return encryptedAccountKey;
    }

    public void setEncryptedAccountKey(String encryptedAccountKey) {
        this.encryptedAccountKey = encryptedAccountKey;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getEncryptedClientKey() {
        return encryptedClientKey;
    }

    public void setEncryptedClientKey(String clientKey) {
        encryptedClientKey = clientKey;
    }

    @Override
    public String getKey() {
        return encryptedClientKey;
    }
}
