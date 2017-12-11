package com.bt.nextgen.core.api.operation;

import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.FindByPartialKeyDtoService;
import com.bt.nextgen.core.api.exception.ServiceException;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.core.api.validation.ApiValidation;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;

/**
  * Delegates to the suplied dto service to execute the find by key.
  * Will return appropriate 404 error the service returns null
  * Will return appropriate 304 errro when the necissary inputs have not been supplied
 */
public class FindByPartialKey <K, T extends KeyedDto <K>> implements ControllerOperation
{
	private K key;
	private String version;
	private FindByPartialKeyDtoService <K, T> service;

	public FindByPartialKey(String version, FindByPartialKeyDtoService <K, T> service, K key)
	{
		this.key = key;
		this.version = version;
		this.service = service;
	}

	public KeyedApiResponse <K> performOperation()
	{
		try
		{
			ApiValidation.preConditionPartialKey(version, key);
			ServiceErrors serviceErrors = new FailFastErrorsImpl();
			KeyedApiResponse <K> result = new KeyedApiResponse <K>(version, key, service.find(key, serviceErrors));
			ApiValidation.postConditionDataNotNull(version, result);
			ApiValidation.postConditionNoServiceErrors(version, serviceErrors);
			return result;
		}
		catch (com.bt.nextgen.core.exception.ServiceException e)
		{
			throw new ServiceException(version, e.getServiceErrors(), e);
		}
	}
}
