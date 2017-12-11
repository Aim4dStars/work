package com.bt.nextgen.core.webservice.exception;

@SuppressWarnings("serial")
public class UnsuccessfulResponseException extends ServiceFailedException
{
	public UnsuccessfulResponseException(String message)
	{
		super(message);
	}
}
