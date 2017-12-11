package com.bt.nextgen.core.api.dto;

import java.util.List;

import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.ServiceErrors;

public abstract class BaseSearchByKeyDtoService <K, T extends KeyedDto <K>> implements SearchByKeyDtoService <K, T>,
	FindByKeyDtoService <K, T>
{

	private static final String RESULT_SIZE_MORE_THAN_ONE = "Result size more than 1";

	@Override
	public T find(K key, ServiceErrors serviceErrors)
	{
		List <T> result = search(key, serviceErrors);
		if (result != null)
		{
			if (result.size() > 1)
				throw new BadRequestException(ApiVersion.CURRENT_VERSION, RESULT_SIZE_MORE_THAN_ONE);
			else
				return result.get(0);
		}
		else
			return null;
	}
}
