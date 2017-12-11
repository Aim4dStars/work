package com.bt.nextgen.api.supermatch.v1.model;

public enum UpdateType {

    CONSENT("consent"),
    ROLLOVER("rollover"),
    ACKNOWLEDGEMENT("acknowledge"),
    CREATE_MEMBER("create"),
    INVALID_INPUT("");

    private String value;

    UpdateType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return name();
    }

    public String getValue() {
        return value;
    }

    public static UpdateType forValue(String value) {
        for (UpdateType updateType : UpdateType.values()) {
            if (updateType.getValue().equalsIgnoreCase(value)) {
                return updateType;
            }
        }
        return INVALID_INPUT;
    }
}
