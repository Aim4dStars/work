package com.bt.nextgen.api.safi.model;

public enum Event {

    ADD_PAYEE,
    EDIT_PAYEE,
    PAYMENT,
    FORGOTTEN_PASSWORD,
    CHANGE_DAILY_LIMIT,
    USER_DETAILS;

    public String value() {
        return name();
    }

    public static Event fromValue(String v) {
        return valueOf(v);
    }

}
