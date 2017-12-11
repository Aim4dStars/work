package com.bt.nextgen.api.profile.model;

import java.util.List;

public class ProfileRoles {
    private String role;
    private String tncStatus;
    private String profileId;
    private String userExperience;
    private boolean active;
    private int count;
    private List<String> names;

    public ProfileRoles() {
    }

    public ProfileRoles(String role, String profileId, boolean active)
    {
        this.role = role;
        this.profileId = profileId;
        this.active = active;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    public String getTncStatus() {
        return tncStatus;
    }

    public void setTncStatus(String tncStatus) {
        this.tncStatus = tncStatus;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public String getUserExperience() {
        return userExperience;
    }

    public void setUserExperience(String userExperience) {
        this.userExperience = userExperience;
    }
}
