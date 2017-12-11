package com.bt.nextgen.core.security.api.service;

import com.bt.nextgen.core.security.api.model.FunctionalRoleGroupEnum;
import com.bt.nextgen.core.security.api.model.PermissionsDto;
import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.broker.JobAuthorizationRole;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

public class PermissionServiceUtil {

    /**
     * This util creates the permission tree from a given list of functional roles
     *
     */
    public static void setFunctionalPermissions(PermissionsDto permissionsDto, Collection<FunctionalRole> functions,
            UserExperience userExperience, JobRole jobRole) {

        Set<FunctionalRole> assigned = EnumSet.noneOf(FunctionalRole.class);
        for (FunctionalRole function : functions) {
            // filter out permissions if the account is asim or direct and the user is an investor
            if (!JobRole.INVESTOR.equals(jobRole) || function.validForUserExperience(userExperience)) {
                final Collection<String> uiPermissionPaths = function.getUiRoles();
                if (!uiPermissionPaths.isEmpty()) {
                    for (String path : uiPermissionPaths) {
                        permissionsDto.setPermission(path, true);
                    }
                    assigned.add(function);
                }
            }
        }
    }

    public static FunctionalRoleGroupEnum getFunctionalRoleGroup(JobRole jobRole, JobAuthorizationRole authorization) {
        // If no match found, default to read only permission
        return authorization != null ? FunctionalRoleGroupEnum.getFunctionalRole(jobRole, authorization)
                : FunctionalRoleGroupEnum.getFunctionalRole(jobRole, JobAuthorizationRole.Support_ReadOnly);
    }
}
