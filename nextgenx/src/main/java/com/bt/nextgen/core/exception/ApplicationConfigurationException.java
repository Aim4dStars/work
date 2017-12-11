package com.bt.nextgen.core.exception;

public class ApplicationConfigurationException extends RuntimeException
{
	public ApplicationConfigurationException(String message)
	{
		super(message);
	}

	public ApplicationConfigurationException(String message, Throwable throwable)
	{
		super(message, throwable);
	}
}
