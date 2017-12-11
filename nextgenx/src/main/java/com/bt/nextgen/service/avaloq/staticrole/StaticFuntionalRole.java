package com.bt.nextgen.service.avaloq.staticrole;

import java.util.List;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;

@ServiceBean(xpath="user")
public class StaticFuntionalRole
{
	@ServiceElement(xpath="user_head_list/user_head/ur_oracle_user/val")
	private String primaryRole;
	
	@ServiceElementList(xpath="user_head_list/user_head/funct_role_list/funct_role", type=RoleMapping.class)
	private List<RoleMapping> functionRoleList;
	
	public String getPrimaryRole()
	{
		return primaryRole;
	}
	public void setPrimaryRole(String primaryRole)
	{
		this.primaryRole = primaryRole;
	}
	public List <RoleMapping> getFunctionRoleList()
	{
		return functionRoleList;
	}
	public void setFunctionRoleList(List <RoleMapping> functionRoleList)
	{
		this.functionRoleList = functionRoleList;
	}
	
}
