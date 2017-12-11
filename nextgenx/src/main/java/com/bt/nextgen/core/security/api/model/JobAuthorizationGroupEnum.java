package com.bt.nextgen.core.security.api.model;

import com.bt.nextgen.service.integration.broker.JobAuthorizationRole;

import java.util.Arrays;
import java.util.List;

/*
 * The following job authorization roles are intentionally ordered from the most restrictive to least restrictive.
 * New enums must be added in the correct order.
 */

public enum JobAuthorizationGroupEnum
{
    READ_ONLY(JobAuthorizationRole.Support_ReadOnly, JobAuthorizationRole.Supervisor_ReadOnly),
    WITHOUT_CASH(JobAuthorizationRole.Support_Without_Cash, JobAuthorizationRole.Supervisor_Without_Cash),
    WITH_CASH(JobAuthorizationRole.Support_With_Cash, JobAuthorizationRole.Supervisor_With_Cash),
    UPDATE(JobAuthorizationRole.Supervisor_Update),
    TRANSACT(JobAuthorizationRole.Supervisor_Transact);

    List<JobAuthorizationRole> permissionList;

    JobAuthorizationGroupEnum(JobAuthorizationRole... rolePermission)
    {
        this.permissionList = Arrays.asList(rolePermission);
    }

    public static JobAuthorizationGroupEnum getGroup(JobAuthorizationRole rolePermission)
    {
        for (JobAuthorizationGroupEnum auth : JobAuthorizationGroupEnum.values())
        {
            if (auth.getPermissionList().contains(rolePermission))
            {
                return auth;
            }
        }
        return JobAuthorizationGroupEnum.READ_ONLY;
    }

    private List<JobAuthorizationRole> getPermissionList()
    {
        return permissionList;
    }
}