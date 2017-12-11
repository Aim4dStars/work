package com.bt.nextgen.serviceops.service;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ServiceOpsClientApplicationStatus {
    APPROVED("Approved"),
    FAILED("Failed");

    private String status;

    private ServiceOpsClientApplicationStatus(final String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }

    @JsonValue
    public String getValue() {
        return toString();
    }

}
