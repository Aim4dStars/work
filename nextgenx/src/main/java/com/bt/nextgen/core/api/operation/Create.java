package com.bt.nextgen.core.api.operation;

import com.bt.nextgen.core.api.dto.CreateDtoService;
import com.bt.nextgen.core.api.exception.ApiException;
import com.bt.nextgen.core.api.exception.ServiceException;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.core.api.validation.ApiValidation;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;

/**
 * Delegates to the supplied dto service to execute the creation of an object.
 * Will return 201 if the the service creates the object successfully
 * Will return appropriate 404 error if the service returns null
 * Will return appropriate 304 error when the necessary inputs have not been supplied
*/
public class Create <K, T extends KeyedDto <K>> implements ControllerOperation
{
	private String version;
	private CreateDtoService <K, T> service;
	private T createdObject;

	public Create(String version, CreateDtoService <K, T> service, T createdObject)
	{
		this.version = version;
		this.service = service;
		this.createdObject = createdObject;
	}

	public KeyedApiResponse <K> performOperation()
	{
		try
		{
			ServiceErrors serviceErrors = new FailFastErrorsImpl();
			T result = service.create(createdObject, serviceErrors);
			ApiValidation.postConditionDataNotNull(version, result);
			ApiValidation.postConditionCompleteKey(version, result.getKey());
			ApiValidation.postConditionNoServiceErrors(version, serviceErrors);
			KeyedApiResponse <K> response = new KeyedApiResponse <K>(version, result.getKey(), result);
			return response;
		}
		catch (com.bt.nextgen.core.exception.ServiceException e)
		{
			throw new ServiceException(version, e.getServiceErrors(), e);
		}
		catch (RuntimeException e)
		{
			if (!(e instanceof ApiException))
			{
				throw new ApiException(version, e.getMessage(), e);
			}
			throw e;
		}
	}
}
