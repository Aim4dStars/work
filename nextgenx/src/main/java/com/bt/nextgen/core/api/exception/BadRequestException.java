package com.bt.nextgen.core.api.exception;

public final class BadRequestException extends ApiException
{
	public BadRequestException(String apiVersion)
	{
		super(apiVersion);
	}

	public BadRequestException(String apiVersion, final String message, final Throwable cause)
	{
		super(apiVersion, message, cause);
	}

	public BadRequestException(String apiVersion, final String message)
	{
		super(apiVersion, message);
	}

	public BadRequestException(String apiVersion, final Throwable cause)
	{
		super(apiVersion, cause);
	}

}
