package com.bt.nextgen.service.avaloq;

public interface UserSearchRequest
{

	public String getSearchToken();
	public String getRoleType();
	public void setRoleType(String roleType);
	public String getCodeId();
	public void setCodeId(String codeId);
	void setSearchToken(String searchToken);
	
}
