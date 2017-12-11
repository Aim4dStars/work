package com.bt.nextgen.core.api.operation;

import java.util.List;

import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;
import com.bt.nextgen.core.api.exception.ApiException;
import com.bt.nextgen.core.api.exception.ServiceException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.Dto;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.core.api.validation.ApiValidation;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;

import static java.util.Arrays.asList;

/**
 * Delegates to the suplied dto service to execute the search criteria. Use this operation
 * when avaloq support the required criteria searching.
 *
 * Will return appropriate 304 errro when the necissary inputs have not been supplied
*/
public class SearchByCriteria <T extends Dto> implements ControllerOperation
{
	public static final String SEARCH_CRITERIA_PARAMETER = "filters";
	private final String version;
	private final SearchByCriteriaDtoService <T> service;
	private final List<ApiSearchCriteria> criteria;

	public SearchByCriteria(String version, SearchByCriteriaDtoService<T> service, String queryString)
	{
		this(version, service, ApiSearchCriteria.parseQueryString(version, queryString));
	}

	public SearchByCriteria(String version, SearchByCriteriaDtoService<T> service, List<ApiSearchCriteria> criteria)
	{
		this.version = version;
		this.service = service;
		this.criteria = criteria;
	}

	public SearchByCriteria(String version, SearchByCriteriaDtoService<T> service, ApiSearchCriteria... criteria) {
		this(version, service, asList(criteria));
	}

	public ApiResponse performOperation()
	{
		try
		{
			ServiceErrors serviceErrors = new FailFastErrorsImpl();
			List<T> resultList = service.search(criteria, serviceErrors);
			ApiResponse result = new ApiResponse(version, new ResultListDto<T>(resultList));
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
