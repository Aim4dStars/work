package com.bt.nextgen.api.saml.model;

import com.bt.nextgen.service.group.customer.CustomerUsernameUpdateRequest;

/**
 * Created by F030695 on 18/01/2016.
 */
public class SamlTokenRefreshRequest implements CustomerUsernameUpdateRequest {

    private String newUserName;
    private String credentialId;

    @Override
    public String getNewUserName() {
        return newUserName;
    }

    @Override
    public void setNewUserName(String newUserName) {
        this.newUserName = newUserName;
    }

    @Override
    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }
}
