package com.bt.nextgen.core.security.api.service;


import com.bt.nextgen.core.security.api.model.PermissionsDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;

interface SuperPermissionDtoService {

    /**
     * Updates the Super account permissions
     *
     * @param permissionsDto - object of {@link PermissionsDto}
     * @param account        - object of {@link WrapAccountDetail}
     * @param jobRole        - Role of the user {@link JobRole}
     * @param serviceErrors  - object of{@link ServiceErrors}
     */
    void setSuperPermissions(PermissionsDto permissionsDto, WrapAccountDetail account, JobRole jobRole, ServiceErrors serviceErrors);
}
