package com.bt.nextgen.api.account.v3.model;

public enum AccountPaymentPermission {

    NO_PAYMENTS("No Payments", "No Payments"),

    PAYMENTS_DEPOSITS_TO_LINKED_ACCOUNTS("Linked accounts only", "Linked accounts only"),

    PAYMENTS_DEPOSITS_TO_ALL("All payments (linked accounts, BPAY and Pay Anyone)", "All payments"),

    NA("NA", "NA");

    private String permissionDesc;
    private String adviserPermissionDesc;

    private AccountPaymentPermission(String permissionDesc, String adviserPermissionDesc) {
        this.permissionDesc = permissionDesc;
        this.adviserPermissionDesc = adviserPermissionDesc;
    }

    public String getPermissionDesc() {
        return permissionDesc;
    }

    public void setPermissionDesc(String permissionDesc) {
        this.permissionDesc = permissionDesc;
    }

    public String getAdviserPermissionDesc() {
        return adviserPermissionDesc;
    }

    public void setAdviserPermissionDesc(String adviserPermissionDesc) {
        this.adviserPermissionDesc = adviserPermissionDesc;
    }
}
