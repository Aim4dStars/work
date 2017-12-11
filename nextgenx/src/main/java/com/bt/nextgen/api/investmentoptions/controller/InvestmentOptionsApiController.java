package com.bt.nextgen.api.investmentoptions.controller;

import com.bt.nextgen.api.investmentoptions.model.InvestmentOptionsDto;
import com.bt.nextgen.api.investmentoptions.service.InvestmentOptionsSearchDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.PageFilter;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.core.api.operation.Sort;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import static com.bt.nextgen.api.util.ApiConstants.CODE;
import static com.bt.nextgen.api.util.ApiConstants.NAME;
import static com.bt.nextgen.api.util.ApiConstants.PAGING;
import static com.bt.nextgen.api.util.ApiConstants.SORT_BY;

/**
 * This API retrieves the list of investment options for a user.
 * <p>
 * Sample request: localhost:9080/ng/secure/api/v1_0/investmentoptions?sortby=name,asc;minAmount,asc&paging={"startIndex":0,"maxResults":"50"}
 */
@Controller
@Api(value = "Retrieves the list of investment options for a user.")
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class InvestmentOptionsApiController {
    @Autowired
    private InvestmentOptionsSearchDtoService ipsService;

    public static final String PRODUCT_ID = "product-id";

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.INVESTMENT_OPTIONS)
    @ApiOperation(value = "Returns the list of investment(MP) options available.", response = InvestmentOptionsDto.class)
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'Products_and_news')")
    public
    @ResponseBody
    ApiResponse getInvestmentOptions(
            @ApiParam(value = "The selected White label product-id (encoded).", name = PRODUCT_ID)
            @RequestParam(required = false, value = PRODUCT_ID) String product,
            @ApiParam(value = "The APIR code to be searched.", name = CODE)
            @RequestParam(required = false, value = CODE) String code,
            @ApiParam(value = "The investment option name(partial allowed) to be searched.", name = NAME)
            @RequestParam(required = false, value = NAME) String name,
            @ApiParam(value = "The sort order parameter.", name = SORT_BY, defaultValue = "name,asc;minAmount,asc")
            @RequestParam(required = false, value = SORT_BY, defaultValue = "name,asc;minAmount,asc") String sortBy,
            @ApiParam(value = "The paging parameter, to restrict the number od records returned.", name = PAGING)
            @RequestParam(required = false, value = PAGING) String paging) {

        final List<ApiSearchCriteria> criteriaList = getCriteriaList(product, code, name);
        if (paging != null) {
            return new PageFilter<>(ApiVersion.CURRENT_VERSION, (new Sort<>(new SearchByCriteria<>(ApiVersion.CURRENT_VERSION,
                    ipsService, criteriaList), sortBy)), paging).performOperation();
        } else {
            return new Sort<>(new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, ipsService, criteriaList), sortBy).performOperation();
        }
    }

    private List<ApiSearchCriteria> getCriteriaList(String product, String code, String name) {
        List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        if (StringUtils.isNotBlank(product)) {
            criteriaList.add(new ApiSearchCriteria(PRODUCT_ID,
                    ApiSearchCriteria.SearchOperation.EQUALS, product, ApiSearchCriteria.OperationType.STRING));
        }
        if (StringUtils.isNotBlank(code)) {
            criteriaList.add(new ApiSearchCriteria(CODE,
                    ApiSearchCriteria.SearchOperation.EQUALS, code, ApiSearchCriteria.OperationType.STRING));
        }
        if (StringUtils.isNotBlank(name)) {
            criteriaList.add(new ApiSearchCriteria(NAME,
                    ApiSearchCriteria.SearchOperation.STARTS_WITH, name, ApiSearchCriteria.OperationType.STRING));
        }
        return criteriaList;
    }
}
