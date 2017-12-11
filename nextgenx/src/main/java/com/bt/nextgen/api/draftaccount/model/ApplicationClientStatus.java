package com.bt.nextgen.api.draftaccount.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ApplicationClientStatus  {

    NOT_REGISTERED("notRegistered"),
    REGISTERED("registered"),
    AWAITING_APPROVAL("awaitingApproval"),
    APPROVED("approved"),
    FAILED_EMAIL("failedEmail"),
    TECHNICAL_ERROR("technicalError"),
    PROCESSING("processing");

    private String jsonName;
    ApplicationClientStatus(String jsonName) {
        this.jsonName = jsonName;
    }

    @Override
    public String toString() {
        return jsonName;
    }

    @JsonValue
    public String getValue() {
        return jsonName;
    }

    public static ApplicationClientStatus getStatus(boolean isApprover, boolean isRegistered, boolean hasApproved) {
        if (isRegistered) {
            if (isApprover) {
                return hasApproved ? APPROVED : AWAITING_APPROVAL;
            }
            return REGISTERED;
        }
        return NOT_REGISTERED;
    }
}
