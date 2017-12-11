package com.bt.nextgen.core.api.operation;

import static com.bt.nextgen.core.api.validation.ApiValidation.postConditionDataNotNull;
import static com.bt.nextgen.core.api.validation.ApiValidation.postConditionNoServiceErrors;

import com.bt.nextgen.core.api.dto.FindOneDtoService;
import com.bt.nextgen.core.api.exception.ApiException;
import com.bt.nextgen.core.api.exception.ServiceException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.Dto;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;

/**
 * Delegates to the supplied dto service to execute find one.
 * This is used where a search, which takes no parameters such as a key or criteria,
 * will always return no more than one result e.g. a user only has one active profile
 * Will return appropriate 404 error
 */
public class FindOne <T extends Dto> implements ControllerOperation
{
	private final String version;
	private final FindOneDtoService <T> service;

	public FindOne(String version, FindOneDtoService <T> service)
	{
		this.version = version;
		this.service = service;
	}

	public ApiResponse performOperation()
	{
		return new ApiResponse(version, findOne());
	}

	public T findOne() {
		return findOne(service, version);
	}

	public <D extends Dto> T findOne(FindOneDtoService <D> service, String version)
	{
		try
		{
			final ServiceErrors serviceErrors = new FailFastErrorsImpl();
			final T result = (T)service.findOne(serviceErrors);
			postConditionDataNotNull(version, result);
			postConditionNoServiceErrors(version, serviceErrors);
			return result;
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
