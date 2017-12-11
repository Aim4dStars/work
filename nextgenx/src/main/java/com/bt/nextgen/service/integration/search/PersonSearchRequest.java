package com.bt.nextgen.service.integration.search;


public interface PersonSearchRequest
{
	public String getSearchToken();
	void setSearchToken(String searchToken);
	
	public String getRoleType();
	public void setRoleType(String roleType);
	
	public String getPersonTypeId();
	public void setPersonTypeId(String personTypeId);
	
	
}
