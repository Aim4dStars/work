package com.bt.nextgen.api.fundpayment.controller;

import com.bt.nextgen.api.fundpayment.model.FundPaymentNoticeSearchDtoKey;
import com.bt.nextgen.api.fundpayment.service.FundPaymentNoticeSearchDtoService;
import com.bt.nextgen.api.util.SearchUtil;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.PageFilter;
import com.bt.nextgen.core.api.operation.SearchByKeyedCriteria;
import com.bt.nextgen.core.api.operation.Sort;
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

import static com.bt.nextgen.api.util.ApiConstants.*;

/**
 * This API retrieves the list of fund payment notices for a user.
 *
 * Sample request: 
 * http://localhost:9080/ng/secure/api/v1_0/fundpayment?startDate=12 Mar 2014&endDate=14 Oct 2014&
 * fundManager=martin&sortby=distributionDate,desc;code,asc
 *
 */
@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class FundPaymentNoticeApiController
{
    @Autowired
    private FundPaymentNoticeSearchDtoService fundPaymentService;

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.FUND_PAYMENT)
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'Products_and_news')")
    public @ResponseBody ApiResponse getFundPaymentNoticeList(
        @RequestParam(required = false, value = QUERY, defaultValue = "") String searchKey,
        @RequestParam(required = false, value = FUND_NAME, defaultValue = "") String fundName,
        @RequestParam(required = false, value = FUND_MANAGER, defaultValue = "") String fundManager,
        @RequestParam(required = false, value = START_DATE) String startDate,
        @RequestParam(required = false, value = END_DATE) String endDate,
        @RequestParam(required = false, value = SORT_BY, defaultValue = "distributionDate,desc;code,asc") String sortBy,
        @RequestParam(required = false, value = PAGING) String paging) throws Exception
    {
        List<ApiSearchCriteria> criteriaList = getCriteriaList(searchKey, fundName, fundManager);
        FundPaymentNoticeSearchDtoKey key = getKey(startDate, endDate);
        if (paging != null)
        {
            return new PageFilter<>(ApiVersion.CURRENT_VERSION,
                (new Sort<>(new SearchByKeyedCriteria<>(ApiVersion.CURRENT_VERSION,
                    fundPaymentService, key, criteriaList), sortBy)), paging).performOperation();
        }
        else
        {
            return new Sort<>(new SearchByKeyedCriteria<>(ApiVersion.CURRENT_VERSION, fundPaymentService, key, criteriaList),
                sortBy).performOperation();
        }
    }

    private FundPaymentNoticeSearchDtoKey getKey(String startDate, String endDate)
    {
        FundPaymentNoticeSearchDtoKey key = new FundPaymentNoticeSearchDtoKey();
        key.setStartDate(SearchUtil.stringToDateTime(startDate, DATE_FORMAT));
        key.setEndDate(SearchUtil.stringToDateTime(endDate, DATE_FORMAT));
        return key;
    }

    private List<ApiSearchCriteria> getCriteriaList(String searchKey, String fundName, String fundManager)
    {
        List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        if (StringUtils.isNotBlank(searchKey))
        {
            criteriaList.add(new ApiSearchCriteria(CODE,
                ApiSearchCriteria.SearchOperation.EQUALS, searchKey, ApiSearchCriteria.OperationType.STRING));
        }
        else if (StringUtils.isNotBlank(fundName))
        {
            criteriaList.add(new ApiSearchCriteria(FUND_NAME,
                ApiSearchCriteria.SearchOperation.STARTS_WITH, fundName, ApiSearchCriteria.OperationType.STRING));
        }
        else if (StringUtils.isNotBlank(fundManager))
        {
            criteriaList.add(new ApiSearchCriteria(FUND_MANAGER,
                ApiSearchCriteria.SearchOperation.STARTS_WITH, fundManager, ApiSearchCriteria.OperationType.STRING));
        }
        return criteriaList;
    }
}
