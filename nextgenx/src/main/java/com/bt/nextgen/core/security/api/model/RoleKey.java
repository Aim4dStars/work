package com.bt.nextgen.core.security.api.model;

import com.btfin.panorama.core.security.Roles;

public class RoleKey
{
	private String roleId;

	public RoleKey(Roles role)
	{
		this(role.name());
	}

	public RoleKey(String roleId)
	{
		this.roleId = roleId;
	}

	public RoleKey()
	{
	}

	public String getRoleId()
	{
		return roleId;
	}
}
