package com.bt.nextgen.core.security;

public enum AvaloqUserRoles
{
    $UR_AVSR_BASIC(com.btfin.panorama.core.security.Roles.ROLE_ADVISER, UserRoles.USER_ROLE_ADVISER),
    $UR_AVSRA_BASIC(com.btfin.panorama.core.security.Roles.ROLE_ADVISER, UserRoles.USER_ROLE_ASSIST),
    $UR_PRPLNR_BASIC(com.btfin.panorama.core.security.Roles.ROLE_ADVISER, UserRoles.USER_ROLE_PARAPLANNER),
    $UR_OE_RESP_BASIC(com.btfin.panorama.core.security.Roles.ROLE_ADVISER, UserRoles.USER_ROLE_DEALER_GROUP),
    $UR_CUSTR(com.btfin.panorama.core.security.Roles.ROLE_INVESTOR, UserRoles.USER_ROLE_INVESTOR),
    $UR_EMULATOR_BASIC(com.btfin.panorama.core.security.Roles.ROLE_SERVICE_OP, UserRoles.USER_ROLE_SERVICE_OP),
    $UR_BOF_CSD_BASIC(com.btfin.panorama.core.security.Roles.ROLE_SERVICE_OP, UserRoles.USER_ROLE_SERVICE_OP_CSD),
    $UR_BOF_CRADMIN_BASIC(com.btfin.panorama.core.security.Roles.ROLE_SERVICE_OP, UserRoles.USER_ROLE_SERVICE_OP_CRADMIN),
    $UR_BOF_CROPS_BASIC(com.btfin.panorama.core.security.Roles.ROLE_SERVICE_OP, UserRoles.USER_ROLE_SERVICE_OP_CROPS),
    $UR_BOF_ECM_BASIC(com.btfin.panorama.core.security.Roles.ROLE_SERVICE_OP, UserRoles.USER_ROLE_SERVICE_OP_ECM),
    $UR_BOF_ACE_BASIC(com.btfin.panorama.core.security.Roles.ROLE_SERVICE_OP, UserRoles.USER_ROLE_SERVICE_OP_ACE),
    $UR_PROD_BASIC(com.btfin.panorama.core.security.Roles.ROLE_SERVICE_OP, UserRoles.USER_ROLE_SERVICE_OP_PROD),
    $UR_IT_SUPP_BASIC(com.btfin.panorama.core.security.Roles.ROLE_SERVICE_OP, UserRoles.USER_ROLE_SERVICE_OP_ITSUPP),
    $UR_BOF_CSDA_BASIC(com.btfin.panorama.core.security.Roles.ROLE_SERVICE_OP, UserRoles.USER_ROLE_SERVICE_OP_CSDA),
    $SU(com.btfin.panorama.core.security.Roles.ROLE_SUPER_USER, UserRoles.USER_ROLE_SUPER_USER),
    $UR_OPS_SUP(com.btfin.panorama.core.security.Roles.ROLE_SERVICE_OP, UserRoles.USER_ROLE_SERVICE_OP),
    $UR_TRUSTEE_BASIC(com.btfin.panorama.core.security.Roles.ROLE_TRUSTEE, UserRoles.USER_ROLE_TRUSTEE),
    $UR_TRUSTEE_RO(com.btfin.panorama.core.security.Roles.ROLE_TRUSTEE, UserRoles.USER_ROLE_TRUSTEE),
    $UR_PORTF_MGR_BASIC(com.btfin.panorama.core.security.Roles.ROLE_ADVISER, UserRoles.USER_ROLE_PORTFOLIO_MANAGER);

    public final com.btfin.panorama.core.security.Roles role;
    public final UserRoles userRole;

    private AvaloqUserRoles(com.btfin.panorama.core.security.Roles role, UserRoles userRole)
    {
        this.role = role;
        this.userRole = userRole;
    }
}