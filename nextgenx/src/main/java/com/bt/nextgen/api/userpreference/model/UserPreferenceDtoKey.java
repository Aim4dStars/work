package com.bt.nextgen.api.userpreference.model;

public class UserPreferenceDtoKey {
    private String userType;
    private String preferenceId;

    public UserPreferenceDtoKey() {
    }

    public UserPreferenceDtoKey(String userType, String preferenceId) {
        this.userType = userType;
        this.preferenceId = preferenceId;
    }

    public String getPreferenceId() {
        return preferenceId;
    }

    public void setPreferenceId(String preferenceId) {
        this.preferenceId = preferenceId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
