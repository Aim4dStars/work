package com.bt.nextgen.api.profile.v1.model;

import java.util.List;

public class AggregatedRoleDto {

    private String role;
    private String userExperienceDisplay;
    private List<UnderlyingRoleDto> underlyingRoles;

    public AggregatedRoleDto(String role, String userExperienceDisplay, List<UnderlyingRoleDto> underlyingRoles) {
        this.role = role;
        this.userExperienceDisplay = userExperienceDisplay;
        this.underlyingRoles = underlyingRoles;
    }

    public String getRole() {
        return role;
    }

    public String getUserExperienceDisplay() {
        return userExperienceDisplay;
    }

    public List<UnderlyingRoleDto> getUnderlyingRoles() {
        return underlyingRoles;
    }

    public boolean isActive() {
        for (UnderlyingRoleDto underlyingRole : underlyingRoles) {
            if (underlyingRole.isActive()) {
                return true;
            }
        }
        return false;
    }
}
