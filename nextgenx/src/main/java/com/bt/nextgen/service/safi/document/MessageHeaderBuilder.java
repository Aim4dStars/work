package com.bt.nextgen.service.safi.document;

import com.rsa.csd.ws.APIType;
import com.rsa.csd.ws.MessageHeader;
import com.rsa.csd.ws.RequestType;

public class MessageHeaderBuilder 
{
	private MessageHeader messageHeader = new MessageHeader();
	private APIType apiType;
	
	
	public MessageHeaderBuilder setApiType(APIType apiType)
	{
		messageHeader.setApiType(apiType);		
		return this;
	}
	
	public MessageHeaderBuilder forRequestType(RequestType requestType)
	{
		messageHeader.setRequestType(requestType);		
		return this;
	}
	
	public MessageHeaderBuilder setVersion(String version)
	{
		messageHeader.setVersion(version);	
		return this;
	}
		
	public MessageHeader build()
	{
		if (messageHeader.getRequestType() == null ||
			messageHeader.getApiType() == null)
		{
			throw new IllegalArgumentException("One or more parameters have not been defined");
		}
			
		return messageHeader;
	}
}
