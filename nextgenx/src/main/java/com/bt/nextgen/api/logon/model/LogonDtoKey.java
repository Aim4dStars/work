package com.bt.nextgen.api.logon.model;

import com.bt.nextgen.core.web.model.UserReset;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;

public class LogonDtoKey
{
    private String credentialId;

    public LogonDtoKey(String credentialId) {
        this.credentialId = credentialId;
    }

    public String getCredentialId() {
        return credentialId;
    }



}
