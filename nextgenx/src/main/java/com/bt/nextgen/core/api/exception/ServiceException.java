package com.bt.nextgen.core.api.exception;

import com.bt.nextgen.service.ServiceErrors;

public final class ServiceException extends ApiException
{
	private ServiceErrors errors;

	public ServiceException(String apiVersion, ServiceErrors errors)
	{
		super(apiVersion);
		this.errors = errors;
	}

	public ServiceException(String apiVersion, ServiceErrors errors, Exception cause)
	{
		super(apiVersion, cause);
		this.errors = errors;
	}

	public ServiceErrors getErrors()
	{
		return errors;
	}

}
