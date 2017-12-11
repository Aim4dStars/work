package com.bt.nextgen.service.safi.document;

import org.apache.commons.lang3.StringUtils;

import com.rsa.csd.ws.IdentificationData;
import com.rsa.csd.ws.UserStatus;
import com.rsa.csd.ws.WSUserType;

public class IdentificationDataBuilder 
{
	private String orgName;
	private WSUserType userType;
	private UserStatus userStatus;
	private String userName;
	private String clientSessionId;
	private String clientTransactionId;
	private String sessionId;
	private String transactionId;
		
	
	public IdentificationDataBuilder setSessionId(String sessionId)
	{		
		this.sessionId = sessionId;				
		return this;
	}
	
	public IdentificationDataBuilder setTransactionId(String transactionId)
	{		
		this.transactionId = transactionId;				
		return this;
	}
	
	public IdentificationDataBuilder setClientSessionIdIdentificationData(String clientSessionId)
	{		
		this.clientSessionId = clientSessionId;				
		return this;
	}
	
	public IdentificationDataBuilder setClientTransactionIdIdentificationData(String clientTransactionId)
	{		
		this.clientTransactionId = clientTransactionId;		
		return this;
	}	
	
	public IdentificationDataBuilder setOrgName(String orgName) {
		this.orgName = orgName;
		return this;
	}

	public IdentificationDataBuilder setUserType(WSUserType userType) {
		this.userType = userType;
		return this;
	}

	public IdentificationDataBuilder setUserStatus(UserStatus userStatus) {
		this.userStatus = userStatus;
		return this;
	}
	
	public IdentificationDataBuilder setUserName(String userName) {
		this.userName = userName;
		return this;
	}

	public IdentificationData build()
	{
		IdentificationData identificationData = new IdentificationData();
		
		identificationData.setOrgName(orgName);
		identificationData.setUserType(userType);
		identificationData.setUserStatus(userStatus);
		
		if (!StringUtils.isEmpty(userName))
			identificationData.setUserName(userName);
		
		if (!StringUtils.isEmpty(clientTransactionId))
			identificationData.setClientTransactionId(clientTransactionId);
		
		if (!StringUtils.isEmpty(clientSessionId))
			identificationData.setClientSessionId(clientSessionId);
		
		if (!StringUtils.isEmpty(sessionId))
			identificationData.setSessionId(sessionId);
		
		if (!StringUtils.isEmpty(transactionId))
			identificationData.setTransactionId(transactionId);
		
		return identificationData;
	}
}
