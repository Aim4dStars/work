package com.bt.nextgen.core.api.exception;

import java.util.List;

import com.bt.nextgen.core.api.model.DomainApiErrorDto;

public final class ApiValidationException extends ApiException
{
	List <DomainApiErrorDto> errors;

	public ApiValidationException(String apiVersion, List <DomainApiErrorDto> errors)
	{
		super(apiVersion);
		this.errors = errors;
	}

	public ApiValidationException(String apiVersion, List <DomainApiErrorDto> errors, final String message, final Throwable cause)
	{
		super(apiVersion, message, cause);
		this.errors = errors;
	}

	public ApiValidationException(String apiVersion, List <DomainApiErrorDto> errors, final String message)
	{
		super(apiVersion, message);
		this.errors = errors;
	}

	public ApiValidationException(String apiVersion, List <DomainApiErrorDto> errors, final Throwable cause)
	{
		super(apiVersion, cause);
		this.errors = errors;
	}

	public List <DomainApiErrorDto> getErrors()
	{
		return errors;
	}
}
