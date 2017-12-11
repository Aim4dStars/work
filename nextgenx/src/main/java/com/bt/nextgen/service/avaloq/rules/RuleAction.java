package com.bt.nextgen.service.avaloq.rules;

/**
 * Created by M041926 on 27/09/2016.
 */
public enum RuleAction {

    CHK("CHK"), CHK_UPD("CHK_UPD");

    private String userId;

    RuleAction(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public static RuleAction fromIntlCode(String id) {

        for(RuleAction action : values()) {
            if (action.getUserId().equalsIgnoreCase(id)) {
                return action;
            }
        }

        return null;
    }
}
