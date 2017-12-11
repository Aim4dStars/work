package com.bt.nextgen.api.account.v1.controller;

import com.bt.nextgen.api.account.v1.model.AccountKey;
import com.bt.nextgen.api.account.v1.model.DatedAccountKey;
import com.bt.nextgen.api.account.v1.model.ParameterisedDatedAccountKey;
import com.bt.nextgen.api.account.v1.model.WrapAccountDetailDto;
import com.bt.nextgen.api.account.v1.service.AccountBalanceDtoService;
import com.bt.nextgen.api.account.v1.service.AccountDtoService;
import com.bt.nextgen.api.account.v1.service.AccountPerformanceDtoService;
import com.bt.nextgen.api.account.v1.service.ValuationDtoService;
import com.bt.nextgen.api.account.v1.service.WrapAccountDetailDtoService;
import com.bt.nextgen.api.account.v1.validation.WrapAccountDetailsDtoErrorMapper;
import com.bt.nextgen.api.util.ApiConstants;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.FindAll;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.PageFilter;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.core.api.operation.ServiceFilter;
import com.bt.nextgen.core.api.operation.Sort;
import com.bt.nextgen.core.api.operation.Update;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.util.StringUtil;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @deprecated Use V2
 */
@Deprecated
@Controller("AccountApiControllerV1")
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class AccountApiController {
    @Autowired
    @Qualifier("ValuationDtoServiceV1")
    private ValuationDtoService valuationService;

    @Autowired
    @Qualifier("WrapAccountDetailDtoServiceV1")
    private WrapAccountDetailDtoService wrapAccountDetailDtoService;

    @Autowired
    @Qualifier("AccountDtoServiceV1")
    private AccountDtoService accountDtoService;

    @Autowired
    @Qualifier("AccountPerformanceDtoServiceV1")
    private AccountPerformanceDtoService performanceService;

    @Autowired
    @Qualifier("WrapAccountDetailsDtoErrorMapperV1")
    private WrapAccountDetailsDtoErrorMapper wrapAccountDetailsDtoErrorMapper;

    @Autowired
    @Qualifier("AccountBalanceDtoServiceV1")
    private AccountBalanceDtoService accountBalanceDtoService;

    @Autowired
    private UserProfileService profileService;

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.VALUATION)
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public @ResponseBody
    KeyedApiResponse<DatedAccountKey> getValuation(
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
            @RequestParam(value = UriMappingConstants.EFFECTIVE_DATE_PARAMETER_MAPPING, required = false) String effectiveDateString,
            @RequestParam(value = "cache", required = false) Boolean useCache) {

        DateTime effectiveDate = new DateTime(effectiveDateString);
        Map<String, String> parameters = new HashMap<>();

        if (Boolean.TRUE.equals(useCache))
        {
            parameters.put("serviceType", "cache");
        }

        DatedAccountKey key = new ParameterisedDatedAccountKey(accountId, effectiveDate, parameters);

        return new FindByKey<>(ApiVersion.CURRENT_VERSION, valuationService, key).performOperation();
    }

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ACCOUNT)
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public @ResponseBody
    ApiResponse getAccount(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId) {
        AccountKey key = new AccountKey(accountId);
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, wrapAccountDetailDtoService, key).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.ACCOUNT_UPDATE)
    @PreAuthorize("isAuthenticated() and @acctPermissionService.hasTransactionPermission(#accountId, 'account.details.update')")
    public @ResponseBody
    ApiResponse update(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
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

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ACCOUNT_PERFORMANCE)
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public @ResponseBody
    KeyedApiResponse<AccountKey> getPortfolioPerformance(
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId) {
        AccountKey key = new AccountKey(accountId);
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, performanceService, key).performOperation();
    }

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ACCOUNT_BALANCE)
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public @ResponseBody
    KeyedApiResponse<AccountKey> getAccountBalance(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId) {
        AccountKey key = new AccountKey(accountId);
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, accountBalanceDtoService, key).performOperation();
    }

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ACCOUNTS)
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_intermediary_client_reports')")
    public @ResponseBody
    ApiResponse getAccounts(@RequestParam(required = false, value = "filter") String filter,
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

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ACCOUNTS_SEARCH)
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_intermediary_client_reports')")
    public @ResponseBody
    ApiResponse searchAccount(@RequestParam(required = false, value = ApiConstants.QUERY) String query,
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

}
