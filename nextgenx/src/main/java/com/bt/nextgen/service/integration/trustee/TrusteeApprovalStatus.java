package com.bt.nextgen.service.integration.trustee;

/**
 * Trustee approval status
 * <p>
 * Mapped from table btfg$code_trustee_aprv
 */
public enum TrusteeApprovalStatus {
    APPROVED("1", "aprv"),
    PENDING("2", "pend"),
    DECLINED("3", "decl"),
    NOT_APPLICABLE("21", "na");

    private String id;
    private String intlId;

    TrusteeApprovalStatus(String id, String intlId) {
        this.id = id;
        this.intlId = intlId;
    }

    public static TrusteeApprovalStatus forId(String id) {
        for (TrusteeApprovalStatus status : TrusteeApprovalStatus.values()) {
            if (status.id.equals(id)) {
                return status;
            }
        }

        return null;
    }

    public static TrusteeApprovalStatus forIntlId(String intlId) {
        for (TrusteeApprovalStatus status : TrusteeApprovalStatus.values()) {
            if (status.intlId.equals(intlId)) {
                return status;
            }
        }

        return null;
    }

    public static TrusteeApprovalStatus forName(String name) {
        for (TrusteeApprovalStatus status : TrusteeApprovalStatus.values()) {
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
