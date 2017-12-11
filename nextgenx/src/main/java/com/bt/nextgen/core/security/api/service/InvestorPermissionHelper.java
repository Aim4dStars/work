package com.bt.nextgen.core.security.api.service;

import com.bt.nextgen.core.security.api.model.PermissionsDto;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;

/**
 * Helper service to setup/update the investor permissions for an account
 */
interface InvestorPermissionHelper {

    /**
     * Updates the investor permissions
     *
     * @param permissionsDto - object of {@link PermissionsDto}
     * @param account        - object of {@link WrapAccountDetail}
     * @param serviceErrors  - object of{@link ServiceErrors}
     */
    void updateInvestorPermissions(PermissionsDto permissionsDto, WrapAccountDetail account, ServiceErrors serviceErrors);
}
