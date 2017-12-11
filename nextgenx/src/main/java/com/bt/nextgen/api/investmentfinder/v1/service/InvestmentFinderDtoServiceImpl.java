package com.bt.nextgen.api.investmentfinder.v1.service;

import com.bt.nextgen.api.investmentfinder.v1.model.InvestmentFinderAssetDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.integration.investmentfinder.model.InvestmentFinderAssetQuery;
import com.btfin.panorama.service.integration.investmentfinder.service.InvestmentFinderAssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.core.IsEqual.equalTo;

@Service
public class InvestmentFinderDtoServiceImpl implements InvestmentFinderDtoService {

    @Autowired
    private InvestmentFinderAssetService investmentFinderAssetService;

    public static final String QUERY_NAME_CRITERIA = "queryName";

    /**
     * Presently search only supports queryName - the pre-configured query to execute.
     * 
     */
    @Override
    public List<InvestmentFinderAssetDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        ApiSearchCriteria queryNameCriteria = selectFirst(criteriaList,
                having(on(ApiSearchCriteria.class).getProperty(), equalTo(QUERY_NAME_CRITERIA)));
        if (queryNameCriteria == null) {
            serviceErrors.addError(new ServiceErrorImpl("Criteria: " + QUERY_NAME_CRITERIA + " not provided in criteriaList."));
            return Collections.<InvestmentFinderAssetDto> emptyList();
        }

        InvestmentFinderAssetQuery query = InvestmentFinderAssetQuery.queryFromSimpleName(queryNameCriteria.getValue());
        if (query == null) {
            serviceErrors.addError(new ServiceErrorImpl("Query name: " + queryNameCriteria.getValue()
                    + " is not a valid query name, expected one of: " + InvestmentFinderAssetQuery.simpleQueryNames()));
            return Collections.<InvestmentFinderAssetDto> emptyList();
        }
        return InvestmentFinderAssetDtoConverter
                .toInvestmentFinderAssetDto(investmentFinderAssetService.findInvestmentFinderAssetsByQuery(query));
    }

}
