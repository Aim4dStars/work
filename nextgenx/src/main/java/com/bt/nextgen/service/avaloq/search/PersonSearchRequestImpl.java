package com.bt.nextgen.service.avaloq.search;

import com.bt.nextgen.service.integration.search.PersonSearchRequest;


public class PersonSearchRequestImpl implements PersonSearchRequest
{
	private String searchToken;
	private String roleType;
	private String personTypeId;
	
	public PersonSearchRequestImpl()
	{
		
	}
			
	public PersonSearchRequestImpl(String searchToken, String roleType, String personTypeId)
	{
		this.searchToken = searchToken;
		this.roleType = roleType;
		this.personTypeId = personTypeId;
	}

	
	@Override
	public String getSearchToken()
	{
		return searchToken;
	}

	@Override
	public void setSearchToken(String searchToken)
	{
		this.searchToken = searchToken;
		
	}

	@Override
	public String getRoleType()
	{
		return roleType;
	}

	@Override
	public void setRoleType(String roleType)
	{
		this.roleType = roleType;		
	}

	@Override
	public String getPersonTypeId()
	{
		return personTypeId;
	}

	@Override
	public void setPersonTypeId(String personTypeId)
	{
		this.personTypeId = personTypeId;		
	}
	
}
