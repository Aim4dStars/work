package com.bt.nextgen.service.avaloq;

import java.util.List;

public class PersonRequestModel implements PersonRequest
{
	String personId;
	List<String> clientIdList;
	//Account Registration Status
	private String accRegistrationStatus;
	
	
	public String getPersonId()
	{
		
		return personId;
	}

	
	public void setPersonId(String personId)
	{
		this.personId = personId;
		
	}


	@Override
	public List <String> getClientIdList()
	{
		
		return clientIdList;
	}


	@Override
	public void setClientIdList(List <String> clientIdList)
	{
		
		this.clientIdList = clientIdList;
	}

	public String getAccRegistrationStatus() {
		return accRegistrationStatus;
	}

	public void setAccRegistrationStatus(String accRegistrationStatus) {
		this.accRegistrationStatus = accRegistrationStatus;
	}	
	
	

}
