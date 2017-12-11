package com.bt.nextgen.core.webservice.exception;

import org.springframework.core.NestedRuntimeException;

@SuppressWarnings("serial")
public class ServiceFailedException extends NestedRuntimeException
{
	public ServiceFailedException(String msg)
	{
		super(msg);
	}

	public ServiceFailedException(String msg, Throwable cause)
	{
		super(msg, cause);
	}
}
