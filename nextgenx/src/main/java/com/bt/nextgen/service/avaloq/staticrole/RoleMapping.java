package com.bt.nextgen.service.avaloq.staticrole;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;

@ServiceBean(xpath="funct_role")
public class RoleMapping
{
	private static final String CODE_CATEGORY_USER_ROLE = "USER_ROLE_KEY";
	
	@ServiceElement(xpath="fr_oracle_user/val")
	private FunctionalRole roleName;

	public FunctionalRole getRoleName()
	{
		return roleName;
	}

	public void setRoleName(FunctionalRole roleName)
	{
		this.roleName = roleName;
	}
}
