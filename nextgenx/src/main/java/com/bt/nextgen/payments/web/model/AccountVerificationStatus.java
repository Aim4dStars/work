package com.bt.nextgen.payments.web.model;

/**
 * Created by M041926 on 23/12/2016.
 */
public class AccountVerificationStatus {

    private String ruleID;

    private boolean authenticationDone;

    public String getRuleID() {
        return ruleID;
    }

    public boolean isAuthenticationDone() {
        return authenticationDone;
    }

    public void setAuthenticationDone(boolean authenticationDone) {
        this.authenticationDone = authenticationDone;
    }

    public AccountVerificationStatus(String ruleID, boolean authenticationDone) {
        this.authenticationDone = authenticationDone;
        this.ruleID = ruleID;
    }
}
