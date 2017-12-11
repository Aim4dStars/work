package com.bt.nextgen.api.inspecietransfer.v3.model;

import com.bt.nextgen.config.JsonViews;
import com.fasterxml.jackson.annotation.JsonView;

public class InspecieTransferKey {

    @JsonView(JsonViews.Write.class)
    private String accountId;

    @JsonView(JsonViews.Write.class)
    private String transferId;

    public InspecieTransferKey() {
        super();
    }

    public InspecieTransferKey(String accountId, String transferId) {
        this.accountId = accountId;
        this.transferId = transferId;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getTransferId() {
        return transferId;
    }
}
