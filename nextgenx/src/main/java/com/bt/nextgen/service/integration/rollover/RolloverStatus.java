package com.bt.nextgen.service.integration.rollover;


public enum RolloverStatus {
    NA("btfg$req_na", "N/A"),
    PENDING("btfg$req_pending", "Pending"),
    COMPLETE("btfg$req_complete", "Complete"),
    SUBMITTED("btfg$req_submitted", "Submitted"),
    NOT_SUBMITTED("btfg$req_notsubmitted", "Not submitted");

    private String code;
    private String displayName;

    private RolloverStatus(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return code;
    }
}