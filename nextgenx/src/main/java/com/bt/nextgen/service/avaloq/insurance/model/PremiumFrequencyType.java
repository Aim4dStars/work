package com.bt.nextgen.service.avaloq.insurance.model;


import com.bt.nextgen.api.staticreference.model.StaticReference;

public enum PremiumFrequencyType implements StaticReference {

    YEARLY("Yearly", 1),
    HALF_YEARLY("HalfYearly", "Half yearly", 2),
    QUARTERLY("Quarterly", 4),
    MONTHLY("Monthly", 12),
    SEMI_MONTHLY("SemiMonthly", "Semi monthly", 24),
    WEEKLY("Weekly", 52),
    TWO_WEEKLY("TwoWeekly", "Two weekly", 21),
    FOUR_WEEKLY("FourWeekly", "Four weekly", 13),
    NO_FREQUENCY("NoFrequency", "No frequency", 1),
    OTHER("Other", 1),
    NOT_AVAILABLE("Not Available", "Not available", 1),
    PREMIUM("Premium", 1);

    private String value;
    private String label;
    private int annualFrequency;

    PremiumFrequencyType(String value, int frequency) {
        this.value = value;
        this.annualFrequency = frequency;
    }

    PremiumFrequencyType(String value, String label, int frequency) {
        this.value = value;
        this.label = label;
        this.annualFrequency = frequency;
    }

    public String getCode() {
        return name();
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getLabel() {
        return label != null ? label : value;
    }

    public int getAnnualFrequency() {
        return annualFrequency;
    }

    public static PremiumFrequencyType forCode(String code) {
        for (PremiumFrequencyType frequencyType : PremiumFrequencyType.values()) {
            if (frequencyType.getCode().equals(code)) {
                return frequencyType;
            }
        }
        return PREMIUM;
    }

    public static PremiumFrequencyType forValue(String value) {
        for (PremiumFrequencyType frequencyType : PremiumFrequencyType.values()) {
            if (frequencyType.getValue().equals(value)) {
                return frequencyType;
            }
        }
        return PREMIUM;
    }

    @Override
    public String toString() {
        return value;
    }
}
