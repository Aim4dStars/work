package com.bt.nextgen.api.userpreference.model;

/**
 * This enum contains the list of user types that a preference can be saved for
 */

public enum UserTypeEnum {
    JOB("job"),
    USER("user"),
    UNKNOWN("unknown");

    private String userType;

    UserTypeEnum(String userType) {
        this.userType = userType;
    }

    public static UserTypeEnum fromString(String userType) {
        for (UserTypeEnum userTypeEnum : UserTypeEnum.values()) {
            if (userTypeEnum.getUserType().equalsIgnoreCase(userType)) {
                return userTypeEnum;
            }
        }
        return UserTypeEnum.UNKNOWN;
    }

    public String getUserType() {
        return userType;
    }
}