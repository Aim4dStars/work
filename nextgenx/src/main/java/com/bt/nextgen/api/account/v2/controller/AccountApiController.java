package com.bt.nextgen.api.account.v2.controller;

import com.bt.nextgen.api.account.v2.model.*;
import com.bt.nextgen.api.account.v2.service.*;
import com.bt.nextgen.api.account.v2.service.valuation.ValuationDtoService;
import com.bt.nextgen.api.account.v2.validation.WrapAccountDetailsDtoErrorMapper;
import com.bt.nextgen.api.util.ApiConstants;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.*;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Deprecated
@Controller("AccountApiControllerV2")
@RequestMapping(produces = "application/json")
public class AccountApiController {
    @Autowired
    private ValuationDtoService valuationService;

    @Autowired
    private WrapAccountDetailDtoService wrapAccountDetailDtoService;

    @Autowired
    private AccountDtoService accountDtoService;

    @Autowired
    private AccountPerformanceDtoService performanceService;

    @Autowired
    private AccountPeriodPerformanceDtoService periodPerformanceDtoService;

    @Autowired
    private WrapAccountDetailsDtoErrorMapper wrapAccountDetailsDtoErrorMapper;

    @Autowired
    private AccountBalanceDtoService accountBalanceDtoService;

    @Autowired
    private UserProfileService profileService;

    @RequestMapping(method = RequestMethod.GET, value = "${api.account.v2.uri.valuation}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public @ResponseBody KeyedApiResponse<DatedValuationKey> getValuation(@PathVariable("account-id") String accountId,
            @RequestParam(value = "effective-date", required = false) String dateString,
            @RequestParam(value = "include-external", required = false) Boolean includeExternal,
            @RequestParam(value = "use-cache", required = false) Boolean useCache,
            @RequestParam(value = "clear-cache", required = false) Boolean clearCache) {
        DateTime effectiveDate;
        if (dateString != null) {
            effectiveDate = new DateTime(dateString);
        } else {
            effectiveDate = DateMidnight.now().toDateTime();
        }

        DatedValuationKey key = new DatedValuationKey(accountId, effectiveDate, includeExternal);

        if (Boolean.TRUE.equals(useCache)) {
            return new CacheableFindByKey<>(ApiVersion.CURRENT_VERSION, valuationService, key, Boolean.TRUE.equals(clearCache))
                    .performOperation();
        } else {
            return new FindByKey<>(ApiVersion.CURRENT_VERSION, valuationService, key).performOperation();
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "${api.account.v2.uri.account}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public @ResponseBody ApiResponse getAccount(@PathVariable("account-id") String accountId) {
        AccountKey key = new AccountKey(accountId);
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, wrapAccountDetailDtoService, key).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = "${api.account.v2.uri.account}")
    @PreAuthorize("isAuthenticated() and @acctPermissionService.hasTransactionPermission(#accountId, 'account.details.update')")
    public @ResponseBody ApiResponse update(@PathVariable("account-id") String accountId,
            @ModelAttribute WrapAccountDetailDto wrapAccountDetailDto) {
        if (!profileService.isEmulating()) {
            AccountKey key = new AccountKey(accountId);
            wrapAccountDetailDto.setKey(key);
            return new Update(ApiVersion.CURRENT_VERSION, wrapAccountDetailDtoService, wrapAccountDetailsDtoErrorMapper,
                    wrapAccountDetailDto).performOperation();
        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "${api.account.v2.uri.performance}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public @ResponseBody KeyedApiResponse<AccountKey> getPortfolioPerformance(@PathVariable("account-id") String accountId) {
        AccountKey key = new AccountKey(accountId);
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, performanceService, key).performOperation();
    }

    /**
     * Generates performance report for an account based on a date range
     * @param accountId - Account to evaluate performance data for
     * @param startDateString - start date
     * @param endDateString   - end date
     * @return KeyedApiResponse<DateRangeAccountKey>
     */
    @RequestMapping(method = RequestMethod.GET, value = "${api.account.v2.uri.periodPerformance}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public @ResponseBody KeyedApiResponse<DateRangeAccountKey> getPortfolioPerformance(
           @PathVariable("account-id") String accountId,
           @RequestParam("start-date") String startDateString,
           @RequestParam("end-date") String endDateString) {

        DateTime startDate = null;
        DateTime endDate = null;

        if (StringUtils.isNotBlank(startDateString) && StringUtils.isNotBlank(endDateString)) {
            startDate = new DateTime(startDateString);
            endDate = new DateTime(endDateString);
        }

        DateRangeAccountKey key = new DateRangeAccountKey(accountId, startDate, endDate);
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, periodPerformanceDtoService, key).performOperation();
    }

    @RequestMapping(method = RequestMethod.GET, value = "${api.account.v2.uri.balance}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public @ResponseBody KeyedApiResponse<AccountKey> getAccountBalance(@PathVariable("account-id") String accountId) {
        AccountKey key = new AccountKey(accountId);
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, accountBalanceDtoService, key).performOperation();
    }

    @RequestMapping(method = RequestMethod.GET, value = "${api.account.v2.uri.accounts}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_intermediary_client_reports')")
    public @ResponseBody ApiResponse getAccounts(@RequestParam(required = false, value = "filter") String filter,
            @RequestParam(required = false, value = "sortby") String orderby,
            @RequestParam(required = false, value = "paging") String paging) {
        ApiResponse response = null;
        if (filter != null && orderby != null) {
            response = new PageFilter<>(ApiVersion.CURRENT_VERSION, new Sort<>(new SearchByCriteria<>(ApiVersion.CURRENT_VERSION,
                    accountDtoService, filter), orderby), paging).performOperation();
        }
        if (orderby != null && response == null) {
            response = new PageFilter<>(ApiVersion.CURRENT_VERSION, new Sort<>(new FindAll<>(ApiVersion.CURRENT_VERSION,
                    accountDtoService), orderby), paging).performOperation();
        }
        if (filter != null && response == null) {
            response = new PageFilter<>(ApiVersion.CURRENT_VERSION, new SearchByCriteria<>(ApiVersion.CURRENT_VERSION,
                    accountDtoService, filter), paging).performOperation();
        }
        if (paging != null && response == null) {
            response = new PageFilter<>(ApiVersion.CURRENT_VERSION, new FindAll<>(ApiVersion.CURRENT_VERSION, accountDtoService),
                    paging).performOperation();
        } else {
            if (response == null) {
                response = new FindAll<>(ApiVersion.CURRENT_VERSION, accountDtoService).performOperation();
            }
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.GET, value = "${api.account.v2.uri.search}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_intermediary_client_reports')")
    public @ResponseBody ApiResponse searchAccount(@RequestParam(required = false, value = ApiConstants.QUERY) String query,
            @RequestParam(required = false, value = ApiConstants.ACCOUNT_STATUS) String status) {
        List<ApiSearchCriteria> filterCriteria = new ArrayList<ApiSearchCriteria>();

        if (StringUtil.isNotNullorEmpty(status) && status.contains(",")) {
            List<String> statusList = Arrays.asList(status.split(","));
            for (String state : statusList) {
                filterCriteria.add(new ApiSearchCriteria("accountStatus", ApiSearchCriteria.SearchOperation.EQUALS, state,
                        ApiSearchCriteria.OperationType.STRING));
            }
        } else if (StringUtil.isNotNullorEmpty(status)) {
            filterCriteria.add(new ApiSearchCriteria("accountStatus", ApiSearchCriteria.SearchOperation.EQUALS, status,
                    ApiSearchCriteria.OperationType.STRING));
        }

        return new Sort<>(new ServiceFilter<>(ApiVersion.CURRENT_VERSION, accountDtoService, query, filterCriteria),
                "accountName").performOperation();
    }

    // Method to get account balances for list of account ids
    @RequestMapping(method = RequestMethod.GET, value = "${api.account.v2.uri.balance.list}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public @ResponseBody ApiResponse getAccountBalances(@PathVariable("account-id-list") List<String> accountIdList) {
        final List<ApiSearchCriteria> filterCriteria = new ArrayList<ApiSearchCriteria>();
        for (String account : accountIdList) {
            filterCriteria.add(new ApiSearchCriteria(account, account));
        }
        final AccountBalanceDtoService accountBalanceDtoService = this.accountBalanceDtoService;
        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, accountBalanceDtoService, filterCriteria)
                .performOperation();
    }
}
