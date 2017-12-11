package com.bt.nextgen.core.api.exception;

public final class NotAllowedException extends ApiException
{

	public NotAllowedException(String apiVersion)
	{
		super(apiVersion);
	}

	public NotAllowedException(String apiVersion, final String message, final Throwable cause)
	{
		super(apiVersion, message, cause);
	}

	public NotAllowedException(String apiVersion, final String message)
	{
		super(apiVersion, message);
	}

	public NotAllowedException(String apiVersion, final Throwable cause)
	{
		super(apiVersion, cause);
	}

}
