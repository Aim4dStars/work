package com.bt.nextgen.core.api.operation;

import com.bt.nextgen.core.api.dto.SearchByKeyDtoService;
import com.bt.nextgen.core.api.exception.ApiException;
import com.bt.nextgen.core.api.exception.ServiceException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.core.api.validation.ApiValidation;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;

import java.util.List;

/**
 * Delegates to the suplied dto service to execute the search by key. Will return appropriate 404 error the service returns an
 * empty list Will return appropriate 304 errro when the necissary inputs have not been supplied
 */
public class SearchByKey <K, T extends KeyedDto <K>> implements ControllerOperation
{
	private K key;
	private String version;
	private SearchByKeyDtoService <K, T> service;

	public SearchByKey(String version, SearchByKeyDtoService <K, T> service, K key)
	{
		this.key = key;
		this.version = version;
		this.service = service;
	}

	@Override
	public ApiResponse performOperation()
	{
		try
		{
			ApiValidation.preConditionPartialKey(version, key);
			ServiceErrors serviceErrors = new FailFastErrorsImpl();
			List <T> resultList = service.search(key, serviceErrors);
			ApiValidation.postConditionNoServiceErrors(version, serviceErrors);
            ApiValidation.postConditionDataNotNull(version, resultList);
            return new ApiResponse(version, new ResultListDto <T>(resultList));
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
