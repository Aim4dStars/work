package com.bt.nextgen.service.avaloq.insurance.model;

/**
 * Benefit option status for InForce, Proposal, InSuspense and Other to return true
 */
public enum BenefitOptionStatus {
    InForce("InForce", true),
    Cancelled("Cancelled", false),
    Proposal("Proposal", true),
    Allowed("Allowed", false),
    NotAllowed("NotAllowed", false),
    InSuspense("InSuspense", true),
    Other("Other", true);

    private String value;
    private boolean status;

    BenefitOptionStatus(String value, boolean status) {
        this.value = value;
        this.status = status;
    }

    public static BenefitOptionStatus forValue(String value) {
        for (BenefitOptionStatus benefitOptionStatus : BenefitOptionStatus.values()) {
            if (benefitOptionStatus.getValue().equals(value)) {
                return benefitOptionStatus;
            }
        }
        return null;
    }


    public String getValue() {
        return value;
    }

    public boolean isStatus() {
        return status;
    }

    @Override
    public String toString() {
        return value;
    }
}
