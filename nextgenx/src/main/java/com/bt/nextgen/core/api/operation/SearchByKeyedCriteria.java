package com.bt.nextgen.core.api.operation;

import java.util.List;

import com.bt.nextgen.core.api.dto.SearchByKeyedCriteriaDtoService;
import com.bt.nextgen.core.api.exception.ApiException;
import com.bt.nextgen.core.api.exception.ServiceException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.Dto;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.core.api.validation.ApiValidation;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;

/**
 * Delegates to the suplied dto service to execute the search by key and then search within that data set
 * use this operation when avaloq supply appropriate apis to matcht he request.
 * Will return appropriate 304 errro when the necissary inputs have not been supplied
*/
public class SearchByKeyedCriteria <K, T extends Dto> implements ControllerOperation
{
	private String version;
	private SearchByKeyedCriteriaDtoService <K, T> service;
	private List <ApiSearchCriteria> criteria;
	private K key;

	public SearchByKeyedCriteria(String version, SearchByKeyedCriteriaDtoService <K, T> service, K key, String queryString)
	{
		this.version = version;
		this.service = service;
		this.key = key;
		this.criteria = ApiSearchCriteria.parseQueryString(version, queryString);

	}

	public SearchByKeyedCriteria(String version, SearchByKeyedCriteriaDtoService <K, T> service, K key,
		List <ApiSearchCriteria> criteria)
	{
		this.version = version;
		this.service = service;
		this.key = key;
		this.criteria = criteria;

	}

	@Override
	public ApiResponse performOperation()
	{
		try
		{
			ApiValidation.preConditionCompleteKey(version, key);
			ServiceErrors serviceErrors = new FailFastErrorsImpl();
			List <T> resultList = service.search(key, criteria, serviceErrors);
			ApiValidation.postConditionNoServiceErrors(version, serviceErrors);
			ApiResponse result = new ApiResponse(version, new ResultListDto <T>(resultList));
			ApiValidation.postConditionDataNotNull(version, result);
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
