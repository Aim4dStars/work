package com.bt.nextgen.service.avaloq.insurance.model;

public enum CommissionStructureType {

    UP_FRONT("UpFront"),
    LEVEL("Level"),
    HYBRID_OPTION_1("HybridOption1"),
    HYBRID_OPTION_2("HybridOption2"),
    FEE_BASED("FeeBased"),
    OTHER("Other"),
    UPFRONT_INITIAL_ONLY("Upfront_InitialOnly"),
    UPFRONT_ALL("Upfront_All"),
    UPFRONT_RESERVE("Upfront_Reserve");

    private String value;

    CommissionStructureType(String value) {
        this.value = value;
    }

    public static CommissionStructureType forCode(String code) {
        for (CommissionStructureType commissionStructureType : CommissionStructureType.values()) {
            if (commissionStructureType.getValue().equals(code)) {
                return commissionStructureType;
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
