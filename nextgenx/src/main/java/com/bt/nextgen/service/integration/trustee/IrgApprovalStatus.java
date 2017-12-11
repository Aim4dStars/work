package com.bt.nextgen.service.integration.trustee;

/**
 * Trustee approval status
 * <p>
 * Mapped from table btfg$code_trustee_aprv
 */
public enum IrgApprovalStatus {
    APPROVED("1", "irg_aprv"),
    PENDING("2", "irg_pend"),
    DECLINED("3", "irg_decl"),
    NOT_APPLICABLE("4", "na");

    private String id;
    private String intlId;

    IrgApprovalStatus(String id, String intlId) {
        this.id = id;
        this.intlId = intlId;
    }

    public static IrgApprovalStatus forId(String id) {
        for (IrgApprovalStatus status : IrgApprovalStatus.values()) {
            if (status.id.equals(id)) {
                return status;
            }
        }

        return null;
    }

    public static IrgApprovalStatus forIntlId(String intlId) {
        for (IrgApprovalStatus status : IrgApprovalStatus.values()) {
            if (status.intlId.equals(intlId)) {
                return status;
            }
        }

        return null;
    }

    public static IrgApprovalStatus forName(String name) {
        for (IrgApprovalStatus status : IrgApprovalStatus.values()) {
            if (status.name().equals(name)) {
                return status;
            }
        }

        return null;
    }

    public String getId() {
        return id;
    }

    public String getIntlId() {
        return intlId;
    }

    public String getCode() {
        return this.name();
    }

    @Override
    public String toString() {
        return intlId;
    }
}
