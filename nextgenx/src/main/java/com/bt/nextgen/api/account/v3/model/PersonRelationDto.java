package com.bt.nextgen.api.account.v3.model;

import com.bt.nextgen.api.client.model.ClientKey;
import com.bt.nextgen.service.integration.domain.InvestorRole;

import java.util.Set;

public class PersonRelationDto
{

	private ClientKey clientKey;
	private String name;
	private Set <InvestorRole> personRoles;
	private String permissions;
	private boolean approver;
	private boolean adviser;
	private boolean primaryContactPerson;

	public boolean isPrimaryContactPerson()
	{
		return primaryContactPerson;
	}

	public void setPrimaryContactPerson(boolean primaryContactPerson)
	{
		this.primaryContactPerson = primaryContactPerson;
	}

	public boolean isAdviser()
	{
		return adviser;
	}

	public void setAdviser(boolean adviser)
	{
		this.adviser = adviser;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public ClientKey getClientKey()
	{
		return clientKey;
	}

	public void setClientKey(ClientKey clientKey)
	{
		this.clientKey = clientKey;
	}

	public Set <InvestorRole> getPersonRoles()
	{
		return personRoles;
	}

	public void setPersonRoles(Set <InvestorRole> personRoles)
	{
		this.personRoles = personRoles;
	}

	public String getPermissions()
	{
		return permissions;
	}

	public void setPermissions(String permission)
	{
		this.permissions = permission;
	}

	public boolean isApprover()
	{
		return approver;
	}

	public void setApprover(boolean approver)
	{
		this.approver = approver;
	}
}
