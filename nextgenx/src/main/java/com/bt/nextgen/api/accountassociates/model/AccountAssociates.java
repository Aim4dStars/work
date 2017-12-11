package com.bt.nextgen.api.accountassociates.model;

import java.util.List;

import com.bt.nextgen.core.type.ConsistentEncodedString;

public class AccountAssociates {

    private List<ConsistentEncodedString> encryptedAccountKeys;

    public List<ConsistentEncodedString> getEncryptedAccountKeys() {
        return encryptedAccountKeys;
    }

    public void setEncryptedAccountKeys(List<ConsistentEncodedString> encryptedAccountKeys) {
        this.encryptedAccountKeys = encryptedAccountKeys;
    }
}
