package com.bt.nextgen.service.avaloq.insurance.model;

/**
 * Occupation Class enum for insurance policy service
 */
public enum OccupationClass {

    A("A", "A"),
    AA("AA", "AA"),
    B("B", "B"),
    BB("BB", "BB"),
    C("C", "C"),
    E("E", "E"),
    H("H", "Other"),
    P("P", "P"),
    S("S", "S"),
    Z("Z", "Other"),
    Other("Other", "Other");

    private String key;
    private String value;

    OccupationClass(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static OccupationClass forValue(String value) {
        for (OccupationClass occupationClass : OccupationClass.values()) {
            if (occupationClass.getKey().equals(value)) {
                return occupationClass;
            }
        }
        return null;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
