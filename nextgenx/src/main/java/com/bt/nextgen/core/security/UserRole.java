package com.bt.nextgen.core.security;

@SuppressWarnings("squid:UnusedPrivateMethod")
public enum UserRole
{
	ROLE_ADVISER("$UR_AVSR_BASIC"),
    ROLE_ACCOUNTANT("ROLE_ACCOUNTANT"),//todo
    ROLE_ACCOUNTANT_SUPPORT_STAFF("ROLE_ACCOUNTANT_SUPPORT_STAFF"),
	USER_ROLE_ASSIST("$UR_AVSRA_BASIC"),
	USER_ROLE_PARAPLANNER("$UR_PRPLNR_BASIC"),
	USER_ROLE_DEALER_GROUP("$UR_OE_RESP_BASIC"),
	USER_ROLE_INVESTOR("$UR_CUSTR"),
	SERVICEOPS_EMULATOR_BASIC("$UR_EMULATOR_BASIC"),
    SERVICEOPS_IT_SUPPORT_BASIC("$UR_IT_SUPP_BASIC"),
    SERVICEOPS_PRODUCT_SUPPORT_BASIC("$UR_IT_SEC_BASIC"),
	SERVICEOPS_DECEASED_ESTATE_BASIC("$UR_DEC_EST_BAS"),
	SERVICEOPS_ADMINISTRATOR_BASIC("$UR_SERVICE_UI"),
	SERVICEOPS_ADMINISTRATOR_DG_RESTRICTED("$UR_SVC_UI_DG_RESTR"),
	TRUSTEE_BASIC("$UR_TRUSTEE_BASIC"),
	TRUSTEE_READ_ONLY("$UR_TRUSTEE_RO"),
	IRG_BASIC("$UR_IRG_BASIC"),
	IRG_READ_ONLY("$UR_IRG_RO"),
	OTHER("OTHER");
	
	private String role;


	UserRole(String userRole)
    {
        this.role = userRole;
    }


    public String getRole()
    {
        return role;
    }
    
	public static UserRole forAvaloqRole(String avaloqRole)
	{
		for (UserRole userRole : UserRole.values())
		{
			if (userRole.role.equalsIgnoreCase(avaloqRole))
			{
				return userRole;
			}
		}
		return OTHER;
	}
}
