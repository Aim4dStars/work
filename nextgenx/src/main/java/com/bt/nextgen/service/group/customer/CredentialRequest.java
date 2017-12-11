package com.bt.nextgen.service.group.customer;

import com.bt.nextgen.service.integration.userinformation.BankReferenceIdentifier;

public interface CredentialRequest extends BankReferenceIdentifier
{
    /**
     *
     * Set The GCM Id in the bank
     */
    void setBankReferenceId(String Id);
}
