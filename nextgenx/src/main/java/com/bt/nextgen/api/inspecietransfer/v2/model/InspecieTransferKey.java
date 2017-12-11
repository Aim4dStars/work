package com.bt.nextgen.api.inspecietransfer.v2.model;

/**
 * @deprecated Use V3
 */
@Deprecated
public class InspecieTransferKey {

    private String accountId;
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

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getTransferId() {
        return transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

}
