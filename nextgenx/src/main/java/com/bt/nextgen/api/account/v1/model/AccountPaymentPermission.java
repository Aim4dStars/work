package com.bt.nextgen.api.account.v1.model;

/**
 * @deprecated Use V2
 */
@Deprecated
public enum AccountPaymentPermission {

    NO_PAYMENTS("No Payments", "No Payments"),

    PAYMENTS_DEPOSITS_TO_LINKED_ACCOUNTS("Linked accounts only", "Linked accounts only"),

    PAYMENTS_DEPOSITS_TO_ALL("All payments (linked accounts, BPAY and Pay Anyone)", "All payments"),

    NA("NA", "NA");

    /** The permission desc. */
    private String permissionDesc;

    /** The adviser permission desc. */
    private String adviserPermissionDesc;

    /**
     * Instantiates a new account payment permission.
     *
     * @param permissionDesc
     *            the permission desc
     * @param adviserPermissionDesc
     *            the adviser permission desc
     */
    private AccountPaymentPermission(String permissionDesc, String adviserPermissionDesc) {
        this.permissionDesc = permissionDesc;
        this.adviserPermissionDesc = adviserPermissionDesc;
    }

    /**
     * Gets the permission desc.
     *
     * @return the permission desc
     */
    public String getPermissionDesc() {
        return permissionDesc;
    }

    /**
     * Sets the permission desc.
     *
     * @param permissionDesc
     *            the new permission desc
     */
    public void setPermissionDesc(String permissionDesc) {
        this.permissionDesc = permissionDesc;
    }

    /**
     * Gets the adviser permission desc.
     *
     * @return the adviser permission desc
     */
    public String getAdviserPermissionDesc() {
        return adviserPermissionDesc;
    }

    /**
     * Sets the adviser permission desc.
     *
     * @param adviserPermissionDesc
     *            the new adviser permission desc
     */
    public void setAdviserPermissionDesc(String adviserPermissionDesc) {
        this.adviserPermissionDesc = adviserPermissionDesc;
    }
}
