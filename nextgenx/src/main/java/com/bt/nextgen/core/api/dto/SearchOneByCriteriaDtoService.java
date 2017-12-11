package com.bt.nextgen.core.api.dto;

import com.bt.nextgen.core.api.model.Dto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;

import java.util.List;

/**
 * Interface for search by criteria that is expected to only return one object.
 *
 * @param <T> Type of the search result.
 */
public interface SearchOneByCriteriaDtoService<T extends Dto> extends DtoService<T> {
    /**
     * Search using a list of criteria.
     *
     * @param criteriaList  List of criteria.
     * @param serviceErrors Object to store errors.
     *
     * @return Single result of the search.
     */
    T search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors);
}
