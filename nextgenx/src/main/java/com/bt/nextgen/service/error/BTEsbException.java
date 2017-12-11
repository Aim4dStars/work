package com.bt.nextgen.service.error;

/**
 * 
 * @author L056616
 *
 */
public class BTEsbException extends IntegrationException
{
	public BTEsbException(String originatingSystem, String transactionId, Throwable cause)
	{
		super(originatingSystem, transactionId, "", "", cause);
	}

	public BTEsbException(String originatingSystem, String transactionId, String errorCode, String description, Throwable cause)
	{
		super(originatingSystem, transactionId, errorCode, description, cause);
	}

	public BTEsbException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public BTEsbException(Throwable cause)
	{
		super(cause);
	}

	public BTEsbException(String message)
	{
		super(message);
	}
}
