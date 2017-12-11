package com.bt.nextgen.core.api.operation;

import com.bt.nextgen.core.api.dto.DeleteDtoService;
import com.bt.nextgen.core.api.exception.ApiException;
import com.bt.nextgen.core.api.exception.ServiceException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.Dto;
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
public class Delete <K, T extends KeyedDto <K>> implements ControllerOperation
{
	private String version;
	private DeleteDtoService <K, T> service;
	private K key;

	public Delete(String version, DeleteDtoService <K, T> service, K key)
	{
		this.version = version;
		this.service = service;
		this.key = key;
	}

	public ApiResponse performOperation()
	{
		try
		{
			ApiValidation.preConditionCompleteKey(version, key);
			ServiceErrors serviceErrors = new FailFastErrorsImpl();
			service.delete(key, serviceErrors);
			ApiResponse response = new ApiResponse(version, (Dto)null);
			ApiValidation.postConditionNoServiceErrors(version, serviceErrors);
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
