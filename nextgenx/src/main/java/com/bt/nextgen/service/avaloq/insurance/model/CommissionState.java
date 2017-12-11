package com.bt.nextgen.service.avaloq.insurance.model;

public enum CommissionState {

    OPT_IN("Opt In"),
    OPT_OUT("Opt Out");

    private String value;

    CommissionState(String value) {
        this.value = value;
    }

    public static CommissionState forCode(String code) {
        for (CommissionState commissionState : CommissionState.values()) {
            if (commissionState.getValue().equals(code)) {
                return commissionState;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
