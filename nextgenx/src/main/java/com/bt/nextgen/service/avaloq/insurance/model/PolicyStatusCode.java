package com.bt.nextgen.service.avaloq.insurance.model;

import com.bt.nextgen.api.staticreference.model.StaticReference;

public enum PolicyStatusCode implements StaticReference {
    PROPOSAL("Proposal", "Proposal"),
    IN_SUSPENSE("InSuspense", "In suspense"),
    IN_FORCE("InForce", "In force"),
    CANCELLED("Cancelled", "Cancelled"),
    DECLINED("Declined", "Declined"),
    NOT_AVAILABLE("Not Available", "Not available");

    private String value;
    private String label;

    PolicyStatusCode(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public String getCode() {
        return name();
    }

    @Override
    public String getLabel() {
        return label;
    }

    public static PolicyStatusCode forStatus(String value) {
        for (PolicyStatusCode policyStatusCode : PolicyStatusCode.values()) {
            if (policyStatusCode.getValue().equals(value)) {
                return policyStatusCode;
            }
            else if ("Waiver".equalsIgnoreCase(value) || "Holiday".equalsIgnoreCase(value)) {
                return IN_FORCE;
            }
        }
        return NOT_AVAILABLE;
    }

    @Override
    public String toString() {
        return value;
    }
}