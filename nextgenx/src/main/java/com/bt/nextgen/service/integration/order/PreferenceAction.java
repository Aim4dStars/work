package com.bt.nextgen.service.integration.order;

public enum PreferenceAction {
    SET("set"),
    REMV("remv");

    private String intlId;
    
    private PreferenceAction(String intlId) {
        this.intlId = intlId;
    }

    public String toString() {
        return intlId;
    }

    public static PreferenceAction forIntlId(String intlId) {
        for (PreferenceAction action : values()) {
            if (action.intlId.equals(intlId)) {
                return action;
            }
        }
        return null;
    }
}

