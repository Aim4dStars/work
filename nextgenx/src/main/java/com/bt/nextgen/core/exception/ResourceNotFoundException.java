package com.bt.nextgen.core.exception;

/**
 * The ResourceNotFoundException is thrown if the requested service/document/data cannot be found.
 */
public class ResourceNotFoundException extends ServiceException
{
	public ResourceNotFoundException() 
	{
		super();
	}
	  
	public ResourceNotFoundException(String message) 
	{
		super(message);
	}
	
	public ResourceNotFoundException(String message, Throwable cause) 
	{
		super(message, cause);
	}
	
	public ResourceNotFoundException(Throwable cause)
	{
		super(cause.getMessage(), cause);
	}
}
