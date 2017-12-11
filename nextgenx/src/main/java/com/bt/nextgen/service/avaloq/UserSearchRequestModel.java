package com.bt.nextgen.service.avaloq;

public class UserSearchRequestModel implements UserSearchRequest
{
	String searchToken;
	String roleType;
	String codeId;
	
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
	public String getCodeId()
	{
		return codeId;
	}
	@Override
	public void setCodeId(String codeId)
	{
		this.codeId = codeId;
	}
	
	

}
