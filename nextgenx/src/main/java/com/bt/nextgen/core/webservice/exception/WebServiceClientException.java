package com.bt.nextgen.core.webservice.exception;

import org.springframework.core.NestedRuntimeException;

@SuppressWarnings("serial")
public class WebServiceClientException extends NestedRuntimeException
{
	public WebServiceClientException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public WebServiceClientException(String message)
	{
		super(message);
	}
}
