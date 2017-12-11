package com.bt.nextgen.core.repository;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class UserPreferenceKey implements Serializable {

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "PREFERENCE_ID")
    private String preferenceId;

    public UserPreferenceKey() {
    }

    public UserPreferenceKey(String userId, String preferenceId) {
        this.userId = userId;
        this.preferenceId = preferenceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPreferenceId(String preferenceId) {
        this.preferenceId = preferenceId;
    }

    public String getPreferenceId() {
        return preferenceId;
    }
}
