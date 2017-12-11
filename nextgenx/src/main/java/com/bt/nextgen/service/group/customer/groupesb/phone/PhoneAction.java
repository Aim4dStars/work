package com.bt.nextgen.service.group.customer.groupesb.phone;

/**
 * Created by F057654 on 3/09/2015.
 */
public enum PhoneAction {

    ADD("A"),
    MODIFY("M"),
    DELETE("D");

    private String code;
    PhoneAction(String code) {
        this.code = code;
    }
    public static PhoneAction fromString(String text) {
        for (PhoneAction t : PhoneAction.values()) {
            if (t.code.equalsIgnoreCase(text)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Invalid action type: " + text);
    }
}
