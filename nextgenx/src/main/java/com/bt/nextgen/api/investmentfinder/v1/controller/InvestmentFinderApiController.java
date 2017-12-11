package com.bt.nextgen.api.investmentfinder.v1.controller;

import com.bt.nextgen.api.investmentfinder.v1.model.InvestmentFinderAssetDto;
import com.bt.nextgen.api.investmentfinder.v1.service.InvestmentFinderDtoService;
import com.bt.nextgen.api.investmentfinder.v1.service.InvestmentFinderDtoServiceImpl;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.btfin.panorama.service.integration.investmentfinder.model.InvestmentFinderAssetQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Provides services to support the Investment Finder.
 */
@Controller("InvestmentFinderApiControllerV1")
@RequestMapping(method = RequestMethod.GET, produces = "application/json")
@Api(value = "Provides services to support the Investment Finder.")
public class InvestmentFinderApiController {

    @Autowired
    private InvestmentFinderDtoService investmentFinderDtoService;

    /**
     * Search for investment finder assets by giving the name of a pre-defined query to execute, valid query names are:
     * 
     * <pre>
     * findEtfOrderByName
     * findAsx50OrderByMarketCap
     * findAsx300MfMpOrderBy1yrPerformance
     * findAsx200MfOrderBySustainability
     * findMfMpOrderByFee
     * findPropertyOrderByName
     * findInternationalOrderByFee
     * findShareOrderByDividend
     * findShareOrderBy1yrPerformance
     * findMfOrderBy
     * findMpOrderBy
     * findTdOrderByRate
     * findByAssetCode
     * </pre>
     * 
     * @see InvestmentFinderAssetQuery InvestmentFinderAssetQuery for queryNames
     * 
     * @param queryName
     *            the name of the query to execute.
     * @return the api response containing the list of investment assets that satisfy the query.
     */
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Search for investment finder assets by giving the name of a pre-defined query to execute.", response = InvestmentFinderAssetDto.class)
    @RequestMapping(value = "${api.investmentfinder.v1.uri.query}")
    public @ResponseBody ApiResponse searchByQueryName(
            @PathVariable("queryName") @ApiParam(value = "The name of the pre-defined query to execute", allowableValues = "findEtfOrderByName, "
                    + "findAsx50OrderByMarketCap, findAsx300MfMpOrderBy1yrPerformance, findAsx200MfOrderBySustainability, findMfMpOrderByFee, "
                    + "findPropertyOrderByName, findInternationalOrderByFee, findShareOrderByDividend, findShareOrderBy1yrPerformance, findMfOrderBy, "
                    + "findMpOrderBy, findTdOrderByRate,findByAssetCode", required = true) String queryName) {
        return new SearchByCriteria<InvestmentFinderAssetDto>(ApiVersion.CURRENT_VERSION, investmentFinderDtoService,
                new ApiSearchCriteria(InvestmentFinderDtoServiceImpl.QUERY_NAME_CRITERIA, queryName)).performOperation();
    }

}
