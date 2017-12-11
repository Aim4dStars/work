package com.bt.nextgen.core.security.api.model;

import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.integration.broker.JobAuthorizationRole;

public enum FunctionalRoleGroupEnum {
    PARAPLANNER_READ_ONLY("$UR_PRPLNR_RO", JobRole.PARAPLANNER, JobAuthorizationGroupEnum.READ_ONLY),
    ADMIN_ASSISTANT_READ_ONLY("$UR_AVSRA_RO", JobRole.ASSISTANT, JobAuthorizationGroupEnum.READ_ONLY),
    PARAPLANNER_NO_CASH("$UR_PRPLNR_NO_CASH", JobRole.PARAPLANNER, JobAuthorizationGroupEnum.WITHOUT_CASH),
    ADMIN_ASSISTANT_NO_CASH("$UR_AVSRA_NO_CASH", JobRole.ASSISTANT, JobAuthorizationGroupEnum.WITHOUT_CASH),
    PARAPLANNER_CASH("$UR_PRPLNR_CASH", JobRole.PARAPLANNER, JobAuthorizationGroupEnum.WITH_CASH),
    ADMIN_ASSISTANT_CASH("$UR_AVSRA_CASH", JobRole.ASSISTANT, JobAuthorizationGroupEnum.WITH_CASH),
    INVESTMENT_MANAGER_READ_ONLY("$UR_INVST_MGR_RO", JobRole.INVESTMENT_MANAGER, JobAuthorizationGroupEnum.READ_ONLY),
    DEALER_GROUP_MANAGER_READ_ONLY("$UR_DGMGR_RO", JobRole.DEALER_GROUP_MANAGER, JobAuthorizationGroupEnum.READ_ONLY),
    PRACTICE_MANAGER_READ_ONLY("$UR_PRACTICE_RO", JobRole.PRACTICE_MANAGER, JobAuthorizationGroupEnum.READ_ONLY),
    PORTFOLIO_MANAGER_READ_ONLY("$UR_PORTF_MGR_BASIC", JobRole.PORTFOLIO_MANAGER, JobAuthorizationGroupEnum.READ_ONLY),
    Unknown_Role("unknown", JobRole.OTHER, null);

    String role;
    JobRole jobRole;
    JobAuthorizationGroupEnum rolePermission;

    FunctionalRoleGroupEnum(String role, JobRole jobRole, JobAuthorizationGroupEnum rolePermission) {
        this.role = role;
        this.jobRole = jobRole;
        this.rolePermission = rolePermission;
    }

    public String toString() {
        return role;
    }

    public static FunctionalRoleGroupEnum getFunctionalRole(JobRole jobRole, JobAuthorizationRole rolePermission) {
        for (FunctionalRoleGroupEnum role : FunctionalRoleGroupEnum.values()) {
            if (role.getJobRole().equals(jobRole)
                    && role.getRolePermission().equals(JobAuthorizationGroupEnum.getGroup(rolePermission))) {
                return role;
            }
        }
        // if no match is found, return RO role
        for (FunctionalRoleGroupEnum role : FunctionalRoleGroupEnum.values()) {
            if (role.getJobRole().equals(jobRole) && role.getRolePermission().equals(JobAuthorizationGroupEnum.READ_ONLY)) {
                return role;
            }
        }
        return null;
    }

    private JobRole getJobRole() {
        return jobRole;
    }

    public JobAuthorizationGroupEnum getRolePermission() {
        return rolePermission;
    }
}