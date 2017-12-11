package com.bt.nextgen.core.api.operation;

import com.bt.nextgen.core.api.dto.SearchByPartialKeyCriteriaDtoService;
import com.bt.nextgen.core.api.exception.ServiceException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.Dto;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.core.api.validation.ApiValidation;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;

import java.util.List;

public class SearchByPartialKeyCriteria<K, T extends Dto> implements ControllerOperation
{
    private String version;
    private SearchByPartialKeyCriteriaDtoService<K, T> service;
    private List<ApiSearchCriteria> criteria;
    private K key;

    public SearchByPartialKeyCriteria(String version, SearchByPartialKeyCriteriaDtoService<K, T> service, K key, String queryString)
    {
        this.version = version;
        this.service = service;
        this.key = key;
        this.criteria = ApiSearchCriteria.parseQueryString(version, queryString);
    }

    public SearchByPartialKeyCriteria(String version, SearchByPartialKeyCriteriaDtoService<K, T> service, K key,
                                      List<ApiSearchCriteria> criteria)
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
            ApiValidation.preConditionPartialKey(version, key);
            ServiceErrors serviceErrors = new FailFastErrorsImpl();
            List <T> resultList = service.search(key, criteria, serviceErrors);
            ApiValidation.postConditionNoServiceErrors(version, serviceErrors);
            ApiResponse result = new ApiResponse(version, new ResultListDto<T>(resultList));
            ApiValidation.postConditionDataNotNull(version, result);
            return result;
        }
        catch (com.bt.nextgen.core.exception.ServiceException e)
        {
            throw new ServiceException(version, e.getServiceErrors(), e);
        }
    }
}