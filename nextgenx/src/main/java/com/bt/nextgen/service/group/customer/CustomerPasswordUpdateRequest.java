package com.bt.nextgen.service.group.customer;

public interface CustomerPasswordUpdateRequest extends com.btfin.panorama.core.security.integration.customer.CredentialIdentifier
{
    String getConfirmPassword();
    
    void setConfirmPassword(String confirmPassword);
    
    String getPassword();
    
    void setPassword(String password);
    
    String getHalgm();
    
    void setHalgm(String halgm);

    String getRequestedAction();
    
    void setRequestedAction(String requestedAction);
}
