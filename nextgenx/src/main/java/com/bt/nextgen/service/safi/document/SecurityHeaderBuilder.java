package com.bt.nextgen.service.safi.document;

import com.bt.nextgen.core.util.Properties;
import com.rsa.csd.ws.AuthorizationMethod;
import com.rsa.csd.ws.SecurityHeader;

public class SecurityHeaderBuilder 
{
	private SecurityHeader securityHeader = new SecurityHeader();
	
	
	public SecurityHeaderBuilder setCallerCredential(String password)
	{
		securityHeader.setCallerCredential(password);
		return this;
	}
	
	public SecurityHeaderBuilder setCallerId(String callerId)
	{
		securityHeader.setCallerId(callerId);
		return this;
	}
	
	public SecurityHeaderBuilder setMethod(AuthorizationMethod authMethod)
	{
		securityHeader.setMethod(authMethod);
		return this;
	}
	
	
	public SecurityHeader build()
	{
		return securityHeader;
	}	
}
