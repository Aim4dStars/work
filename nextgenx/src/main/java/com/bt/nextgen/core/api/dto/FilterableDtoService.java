package com.bt.nextgen.core.api.dto;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.bt.nextgen.core.api.model.Dto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;

public interface FilterableDtoService <T extends Dto> extends DtoService <T>
{
	List <T> getFilteredValue(String queryString, List<ApiSearchCriteria> filterCriteria, ServiceErrors serviceErrors);
}
