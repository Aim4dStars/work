package com.bt.nextgen.api.draftaccount.model;


public class ClientApplicationKey {

    private Long clientApplicationKey;

    public ClientApplicationKey(Long clientApplicationKey) {
        this.clientApplicationKey = clientApplicationKey;
    }

    public ClientApplicationKey() {
    }

    public Long getClientApplicationKey() {
        return clientApplicationKey;
    }

    public void setClientApplicationKey(Long clientApplicationKey) {
        this.clientApplicationKey = clientApplicationKey;
    }

    @Override
    public String toString() {
        return "ClientApplicationKey("+ getClientApplicationKey()+")";
    }
}
