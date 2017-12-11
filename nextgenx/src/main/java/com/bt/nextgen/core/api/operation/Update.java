package com.bt.nextgen.core.api.operation;

import com.bt.nextgen.core.api.dto.UpdateDtoService;
import com.bt.nextgen.core.api.exception.ApiException;
import com.bt.nextgen.core.api.exception.ApiValidationException;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.core.api.validation.ApiValidation;
import com.bt.nextgen.core.api.validation.ErrorMapper;
import com.bt.nextgen.core.exception.ValidationException;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;

/**
 * Delegates to the supplied dto service to execute the update (save) of an object.
 * Will return appropriate 404 error if the service returns null or an incomplete key
 * Will return appropriate 304 error when the necessary inputs have not been supplied
*/
public class Update <K, T extends KeyedDto <K>> implements ControllerOperation
{
	private String version;
	private UpdateDtoService <K, T> service;
	private T updateObject;
	private ErrorMapper mapper;

	public Update(String version, UpdateDtoService <K, T> service, ErrorMapper mapper, T updateObject)
	{
		this.version = version;
		this.service = service;
		this.updateObject = updateObject;

		this.mapper = mapper;
	}

	public KeyedApiResponse <K> performOperation()
	{
		try
		{
			ApiValidation.preconditionNotNull(version, updateObject);
			ApiValidation.preConditionCompleteKey(version, updateObject.getKey());
			ServiceErrors serviceErrors = new FailFastErrorsImpl();
			KeyedApiResponse <K> result = new KeyedApiResponse <K>(version, updateObject.getKey(), service.update(updateObject,
				serviceErrors));
			ApiValidation.postConditionDataNotNull(version, result);
			ApiValidation.postConditionNoServiceErrors(version, serviceErrors);
			return result;
		}
		catch (ValidationException ex)
		{
			throw new ApiValidationException(version, mapper.map(ex.getErrors()), ex);
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
