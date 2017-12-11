package com.bt.nextgen.core.api.operation;

import static com.bt.nextgen.core.api.validation.ApiValidation.postConditionNoServiceErrors;

import java.util.List;

import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.core.api.exception.ApiException;
import com.bt.nextgen.core.api.exception.ServiceException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.Dto;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;

/**
 * Delegates to the suplied dto service to execute the find all. 
 */
public class FindAll <T extends Dto> implements ControllerOperation
{
	private final String version;
	private final FindAllDtoService <T> service;

	public FindAll(String version, FindAllDtoService <T> service)
	{
		this.version = version;
		this.service = service;
	}

	public ApiResponse performOperation()
	{
		List <T> resultList = findAll(service, version);
		return new ApiResponse(version, new ResultListDto <>(resultList));
	}

	public static <D extends Dto> List <D> findAll(FindAllDtoService <D> service, String version)
	{
		try
		{
			final ServiceErrors serviceErrors = new FailFastErrorsImpl();
			final List <D> resultList = service.findAll(serviceErrors);
			postConditionNoServiceErrors(version, serviceErrors);
			return resultList;
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
