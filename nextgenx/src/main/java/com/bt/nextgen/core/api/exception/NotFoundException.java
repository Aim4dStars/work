package com.bt.nextgen.core.api.exception;

public final class NotFoundException extends ApiException
{

	public NotFoundException(String apiVersion)
	{
		super(apiVersion);
	}

	public NotFoundException(String apiVersion, final String message, final Throwable cause)
	{
		super(apiVersion, message, cause);
	}

	public NotFoundException(String apiVersion, final String message)
	{
		super(apiVersion, message);
	}

	public NotFoundException(String apiVersion, final Throwable cause)
	{
		super(apiVersion, cause);
	}

}
