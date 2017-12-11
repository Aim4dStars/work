package com.bt.nextgen.api.fees.model;

public class OneOffFeesKey {
    private String accountId;

    public OneOffFeesKey(String accountId) {
        super();
        this.accountId = accountId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
