package com.bt.nextgen.core.api.dto;

import com.bt.nextgen.core.api.model.Dto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;

import java.util.List;

public interface SearchByPartialKeyCriteriaDtoService<K, T extends Dto> extends DtoService<T> {
    List<T> search(K key, List <ApiSearchCriteria> criteria, ServiceErrors serviceErrors);
}