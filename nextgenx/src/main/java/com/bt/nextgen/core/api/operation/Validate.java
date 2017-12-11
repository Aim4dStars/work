package com.bt.nextgen.core.api.operation;

import com.bt.nextgen.core.api.dto.ValidateDtoService;
import com.bt.nextgen.core.api.exception.ApiException;
import com.bt.nextgen.core.api.exception.ApiValidationException;
import com.bt.nextgen.core.api.exception.ServiceException;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.core.api.validation.ApiValidation;
import com.bt.nextgen.core.api.validation.ErrorMapper;
import com.bt.nextgen.core.exception.ValidationException;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;

/**
 * Delegates to the supplied dto service to execute the validation of an object.
*/
public class Validate <K, T extends KeyedDto <K>> implements ControllerOperation
{
	private String version;
	private ValidateDtoService <K, T> service;
	private ErrorMapper mapper;
	private T validateObject;

	public Validate(String version, ValidateDtoService <K, T> service, ErrorMapper mapper, T validateObject)
	{
		this.version = version;
		this.service = service;
		this.mapper = mapper;
		this.validateObject = validateObject;
	}

	public KeyedApiResponse <K> performOperation()
	{
		try
		{
			ServiceErrors serviceErrors = new FailFastErrorsImpl();
			KeyedApiResponse <K> result = new KeyedApiResponse <K>(version,
				validateObject.getKey(),
				service.validate(validateObject, serviceErrors));
			ApiValidation.postConditionDataNotNull(version, result);
			ApiValidation.postConditionNoServiceErrors(version, serviceErrors);

			return result;
		}
		catch (ValidationException ex)
		{
			throw new ApiValidationException(version, mapper.map(ex.getErrors()), ex);
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
