package com.bt.nextgen.core.api.exception;

public class ApiException extends RuntimeException
{
	private String apiVersion;

	public ApiException(String apiVersion)
	{
		super();
		this.apiVersion = apiVersion;
	}

	public ApiException(String apiVersion, final String message, final Throwable cause)
	{
		super(message, cause);
		this.apiVersion = apiVersion;
	}

	public ApiException(String apiVersion, final String message)
	{
		super(message);
		this.apiVersion = apiVersion;
	}

	public ApiException(String apiVersion, final Throwable cause)
	{
		super(cause);
		this.apiVersion = apiVersion;
	}

	public String getApiVersion()
	{
		return apiVersion;
	}
}
