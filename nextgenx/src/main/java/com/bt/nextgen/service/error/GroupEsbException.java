package com.bt.nextgen.service.error;

public class GroupEsbException extends IntegrationException
{

	public GroupEsbException(String originatingSystem, String transactionId, Throwable cause)
	{
		super(originatingSystem, transactionId, "", "", cause);
	}
	
	public GroupEsbException(String originatingSystem, String transactionId,
 String errorCode, String description, Throwable cause)
	{
		super(originatingSystem, transactionId, errorCode, description, cause);
	}

}
