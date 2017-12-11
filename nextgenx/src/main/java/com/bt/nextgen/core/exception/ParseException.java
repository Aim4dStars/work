package com.bt.nextgen.core.exception;

/**
 * The ParseException is thrown if it fails in parsing response data.
 */
public class ParseException extends ServiceException
{
	public ParseException() 
	{
		super();
	}
	  
	public ParseException(String message) 
	{
		super(message);
	}
	
	public ParseException(String message, Throwable cause) 
	{
		super(message, cause);
	}
	
	public ParseException(Throwable cause)
	{
		super(cause.getMessage(), cause);
	}
}
