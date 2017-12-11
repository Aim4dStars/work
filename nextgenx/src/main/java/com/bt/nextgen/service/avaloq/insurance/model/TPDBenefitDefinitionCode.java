package com.bt.nextgen.service.avaloq.insurance.model;

import com.bt.nextgen.api.staticreference.model.StaticReference;

public enum TPDBenefitDefinitionCode implements StaticReference {

    OWN_OCCUPATION("OwnOccupation", "Own occupation"),
    ANY_OCCUPATION("AnyOccupation", "Any occupation"),
    GENERAL("General", "General cover"),
    HOME_DUTIES("HomeDuties", "Home duties"),
    OTHER("Other", "Other"),
    NOT_AVAILABLE("Not Available", "Not Available");

    private String value;
    private String label;

    TPDBenefitDefinitionCode(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public static TPDBenefitDefinitionCode forValue(String value) {
        for (TPDBenefitDefinitionCode tpdBenefitDefinitionCode : TPDBenefitDefinitionCode.values()) {
            if (tpdBenefitDefinitionCode.getValue().equals(value)) {
                return tpdBenefitDefinitionCode;
            }
        }
        return NOT_AVAILABLE;
    }

    public String getValue() {
        return value;
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
        return label;
    }
}
