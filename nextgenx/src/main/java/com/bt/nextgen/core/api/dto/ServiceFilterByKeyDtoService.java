package com.bt.nextgen.core.api.dto;

import com.bt.nextgen.core.api.model.Dto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;

import java.util.List;

/**
 * Created by L075208 on 12/08/2015.
 */
public interface ServiceFilterByKeyDtoService<K,T extends Dto> extends DtoService<T> {

    List<T>getFilteredValue(K key, List <ApiSearchCriteria> criteria,String queryString,ServiceErrors serviceErrors);
}
