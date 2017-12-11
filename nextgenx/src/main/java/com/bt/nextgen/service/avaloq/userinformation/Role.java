package com.bt.nextgen.service.avaloq.userinformation;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;

@ServiceBean(xpath = "role")
public class Role {

	@ServiceElement(xpath = "val")
	private String roleId;

	public String getRoleId()
	{
		return roleId;
	}

	public void setRoleId(String roleId)
	{
		this.roleId = roleId;
	}

}
