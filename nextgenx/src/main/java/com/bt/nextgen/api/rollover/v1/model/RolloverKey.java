package com.bt.nextgen.api.rollover.v1.model;

import com.bt.nextgen.config.JsonViews;
import com.fasterxml.jackson.annotation.JsonView;

public class RolloverKey {

    @JsonView(JsonViews.Write.class)
    private String accountId;
    @JsonView(JsonViews.Write.class)
    private String rolloverId;

    public RolloverKey() {
        super();
    }

    public RolloverKey(String accountId, String rolloverId) {
        this.accountId = accountId;
        this.rolloverId = rolloverId;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getRolloverId() {
        return rolloverId;
    }

}
