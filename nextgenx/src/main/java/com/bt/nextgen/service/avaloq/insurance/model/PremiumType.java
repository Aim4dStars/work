package com.bt.nextgen.service.avaloq.insurance.model;

import com.bt.nextgen.api.staticreference.model.StaticReference;

/**
 * BenefitType premium type enum
 */
public enum PremiumType implements StaticReference {
    STEPPED("Stepped", "Stepped"),
    LEVEL55("Level55", "Level55", "Level 55"),
    LEVEL65("Level65", "Level65", "Level 65"),
    OTHER("Other", "Stepped"),
    NOT_AVAILABLE("Not Available", "Not available");

    private String key;
    private String value;
    private String label;

    PremiumType(String key, String value, String label) {
        this.key = key;
        this.value = value;
        this.label = label;
    }

    PremiumType(String key, String value) {
        this(key, value, value);
    }

    public static PremiumType forValue(String value) {
        for (PremiumType premiumType : PremiumType.values()) {
            if (premiumType.getKey().equals(value)) {
                return premiumType;
            }
        }
        return NOT_AVAILABLE;
    }


    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key;
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
