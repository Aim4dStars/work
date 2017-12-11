package com.bt.nextgen.userauthority.web;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.bt.nextgen.core.security.AvaloqUserRoles;
import com.bt.nextgen.core.security.UserRoles;

public class PermissionsModel implements Serializable
{

	private List <Role> roles;
	private UserRoles avaloqPrimaryRole;
	private Map <String, List <Authority>> authorities;

	public List <Role> getRoles()
	{
		return roles;
	}

	public void setRoles(List <Role> roles)
	{
		this.roles = roles;
	}

	public Map <String, List <Authority>> getAuthorities()
	{
		return authorities;
	}

	public void setAuthorities(Map <String, List <Authority>> authorities)
	{
		this.authorities = authorities;
	}

	public UserRoles getAvaloqPrimaryRole()
	{
		return avaloqPrimaryRole;
	}

	public void setAvaloqPrimaryRole(Role avaloqPrimaryRole)
	{
		this.avaloqPrimaryRole = AvaloqUserRoles.valueOf(avaloqPrimaryRole.getId()).userRole;
	}
}
