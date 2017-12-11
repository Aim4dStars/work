package com.bt.nextgen.service.avaloq;

import java.util.List;

public interface PersonRequest
{
	
	public String getPersonId(); 
	public void setPersonId(String personId); 
	public List<String> getClientIdList();
	public void setClientIdList(List <String> clientIdList);	
	public String getAccRegistrationStatus();
	public void setAccRegistrationStatus(String accRegistrationStatus);
	

}
