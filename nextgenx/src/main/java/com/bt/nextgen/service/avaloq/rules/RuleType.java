package com.bt.nextgen.service.avaloq.rules;

/**
 * Created by M041926 on 27/09/2016.
 */
public enum RuleType {

    ACC_ACTIV("ACC_ACTIV"), LINK_ACC("LINK_ACC");

    private String userId;

    RuleType(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public static RuleType fromIntlCode(String id) {

        for(RuleType type : values()) {
            if (type.getUserId().equalsIgnoreCase(id)) {
                return type;
            }
        }

        return null;
    }
}
