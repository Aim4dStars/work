package com.bt.nextgen.core.security.api.service;

import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.JobRoleType;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Asim permission implementation.
 */
@Component
public class AsimPermissionImpl implements AsimPermission {
    /**
     * Service to access user profile.
     */
    @Autowired
    private UserProfileService profileService;


    @Override
    public boolean overrideValue(boolean value, boolean valueOverride, boolean includeRoleTypes,
                                 JobRoleType... jobRoleTypes) {
        final UserProfile userProfile = profileService.getActiveProfile();
        final JobRole jobRole = userProfile.getJobRole();

        if (userProfile.getUserExperience() != UserExperience.ASIM || jobRole == null || jobRoleTypes == null) {
            return value;
        }

        final boolean hasRoles = Arrays.asList(jobRoleTypes).contains(jobRole.getJobRoleType());

        if (!(includeRoleTypes ^ hasRoles)) {
            return valueOverride;
        }

        return value;
    }
}
