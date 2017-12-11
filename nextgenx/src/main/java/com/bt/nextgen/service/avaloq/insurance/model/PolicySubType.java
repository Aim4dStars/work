package com.bt.nextgen.service.avaloq.insurance.model;

import com.bt.nextgen.api.staticreference.model.StaticReference;

public enum PolicySubType implements StaticReference {
    ALL("All"),
    BUSINESS_OVERHEAD("BusinessOverhead", "Business overheads"),
    AGREED_VALUE("AgreedValue", "Agreed value"),
    INDEMNITY("Indemnity"),
    GUARANTEED("Guaranteed"),
    BILL_COVER("BillCover", "Bill cover"),
    ENDORSED_AGREED("EndorsedAgreed", "Endorsed agreed value"),
    OTHER("Other"),
    NOT_AVAILABLE("Not available");

    private String value;
    private String label;

    PolicySubType(String value) {
        this.value = value;
    }

    PolicySubType(String value, String label) {
        this(value);
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public static PolicySubType forValue(String value) {
        for (PolicySubType policySubType : PolicySubType.values()) {
            if (policySubType.getValue().equals(value)) {
                return policySubType;
            }
        }
        return NOT_AVAILABLE;
    }

    @Override
    public String toString() {
        return value;
    }


    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getLabel() {
        return label != null ? label : value;
    }
}
