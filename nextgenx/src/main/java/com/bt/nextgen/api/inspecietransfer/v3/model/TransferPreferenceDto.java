package com.bt.nextgen.api.inspecietransfer.v3.model;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.config.JsonViews;
import com.fasterxml.jackson.annotation.JsonView;

public class TransferPreferenceDto {

    @JsonView(JsonViews.Write.class)
    private AccountKey issuerKey;

    @JsonView(JsonViews.Write.class)
    private String preference;

    @JsonView(JsonViews.Write.class)
    private String action;

    @JsonView(JsonViews.Write.class)
    private String issuerName;

    public TransferPreferenceDto() {
        super();
    }

    public TransferPreferenceDto(String issuerId, String preference, String action) {
        this.issuerKey = new AccountKey(issuerId);
        this.preference = preference;
        this.action = action;
    }

    public AccountKey getIssuerKey() {
        return issuerKey;
    }

    public String getPreference() {
        return preference;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public String getAction() {
        return action;
    }
}
