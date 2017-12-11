package com.bt.nextgen.api.contributionhistory.controller;

import com.bt.nextgen.api.contributionhistory.service.ContributionHistoryDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.SearchOneByCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * API controller for contribution history.
 */
@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class ContributionHistoryApiController {
    /**
     * DTO service for contribution history.
     */
    @Autowired
    private ContributionHistoryDtoService contributionHistoryDtoService;

    /**
     * Get summary of contributions.
     *
     * @param accId                  Account id.
     * @param financialYearStartDate Start of the financial year.
     * @return Summary of contributions.
     */
    @SuppressWarnings("unchecked")
    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accId, 'account.super.contribution.view')")
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.SUPER_CONTRIBUTIONS_HISTORY)
    @ResponseBody
    public ApiResponse getContributionSummary(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) final String accId,
                                              @RequestParam(value = "date", required = true) final String financialYearStartDate,
                                              @RequestParam(value = "cache", defaultValue = "false") String useCache) {
        if (StringUtils.isEmpty(accId)) {
            throw new IllegalArgumentException("Account id is not valid");
        }

        if (StringUtils.isEmpty(financialYearStartDate)) {
            throw new IllegalArgumentException("Date is not valid");
        }

        final ApiSearchCriteria accountIdCriteria = new ApiSearchCriteria("accountId",
                ApiSearchCriteria.SearchOperation.EQUALS, EncodedString.toPlainText(accId),
                ApiSearchCriteria.OperationType.STRING);
        final ApiSearchCriteria dateCriteria = new ApiSearchCriteria("financialYearDate",
                ApiSearchCriteria.SearchOperation.EQUALS, financialYearStartDate,
                ApiSearchCriteria.OperationType.STRING);

        final List<ApiSearchCriteria> searchCriteriaList = new ArrayList<>();

        searchCriteriaList.add(accountIdCriteria);
        searchCriteriaList.add(dateCriteria);

        if (StringUtils.isNotEmpty(useCache) && "true".equalsIgnoreCase(useCache)) {
            ApiSearchCriteria useCacheCriteria = new ApiSearchCriteria("useCache", ApiSearchCriteria.SearchOperation.EQUALS, "true", ApiSearchCriteria.OperationType.STRING);
            searchCriteriaList.add(useCacheCriteria);
        }

        return new SearchOneByCriteria<>(ApiVersion.CURRENT_VERSION, contributionHistoryDtoService, searchCriteriaList)
                .performOperation();
    }
}
