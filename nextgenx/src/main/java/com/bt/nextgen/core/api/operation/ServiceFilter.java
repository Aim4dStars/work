package com.bt.nextgen.core.api.operation;

import java.util.List;

import com.bt.nextgen.core.api.dto.FilterableDtoService;
import com.bt.nextgen.core.api.exception.ApiException;
import com.bt.nextgen.core.api.exception.ServiceException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.Dto;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.core.api.validation.ApiValidation;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;

public class ServiceFilter <T extends Dto> implements ControllerOperation
{
	private String version;
	private FilterableDtoService <T> service;
	private String queryString;
	private List <ApiSearchCriteria> filterCriteria;

	public ServiceFilter(String version, FilterableDtoService <T> service)
	{
		super();
		this.version = version;
		this.service = service;
		this.queryString = queryString;
		//this.filterCriteria = ApiSearchCriteria.parseQueryString(version, filterCriteria);
	}

	public ServiceFilter(String version, FilterableDtoService <T> service, String queryString)
	{
		this.version = version;
		this.service = service;
		this.queryString = queryString;
		//this.filterCriteria = filterCriteria;
	}

	public ServiceFilter(String version, FilterableDtoService <T> service, String queryString,
		List <ApiSearchCriteria> filterCriteria)
	{
		this.version = version;
		this.service = service;
		this.queryString = queryString;
		this.filterCriteria = filterCriteria;
	}

	@Override
	public ApiResponse performOperation()
	{
		try
		{
			ServiceErrors serviceErrors = new FailFastErrorsImpl();
			List <T> resultList = service.getFilteredValue(queryString, filterCriteria, serviceErrors);
			ApiResponse result = new ApiResponse(version, new ResultListDto <T>(resultList));
			ApiValidation.postConditionDataNotNull(version, result);
			ApiValidation.postConditionNoServiceErrors(version, serviceErrors);
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
