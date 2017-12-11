package com.bt.nextgen.api.investmentfinder.v1.service;

import com.bt.nextgen.api.investmentfinder.v1.model.InvestmentFinderAssetDto;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;

import java.util.List;

public interface InvestmentFinderDtoService extends SearchByCriteriaDtoService<InvestmentFinderAssetDto> {

    /**
     * Presently search only supports queryName - the pre-configured query to execute.
     * 
     */
    @Override
    List<InvestmentFinderAssetDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors);

}