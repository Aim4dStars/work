package com.bt.nextgen.core.api.dto;

import java.util.List;

import com.bt.nextgen.core.api.model.Dto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;

public interface SearchByKeyedCriteriaDtoService <K, T extends Dto> extends DtoService <T>
{
	List <T> search(K key, List <ApiSearchCriteria> criteria, ServiceErrors serviceErrors);
}
