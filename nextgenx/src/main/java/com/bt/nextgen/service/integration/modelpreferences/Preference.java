package com.bt.nextgen.service.integration.modelpreferences;

public enum Preference {
    CASH("cash"),
    PRORATA("do_not_hold");

    private String intlId;

    private Preference(String intlId) {
        this.intlId = intlId;
    }

    public String toString() {
        return intlId;
    }

    public static Preference forIntlId(String intlId) {
        for (Preference pref : values()) {
            if (pref.intlId.equals(intlId)) {
                return pref;
            }
        }
        return null;
    }
}
