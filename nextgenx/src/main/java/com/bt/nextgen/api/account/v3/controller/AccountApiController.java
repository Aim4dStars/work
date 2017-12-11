package com.bt.nextgen.api.account.v3.controller;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.account.v3.model.AccountSearchKey;
import com.bt.nextgen.api.account.v3.model.AccountSearchTypeEnum;
import com.bt.nextgen.api.account.v3.model.WrapAccountDetailDto;
import com.bt.nextgen.api.account.v3.service.AccountBalanceDtoService;
import com.bt.nextgen.api.account.v3.service.AccountDtoService;
import com.bt.nextgen.api.account.v3.service.AccountSearchByAccountDtoService;
import com.bt.nextgen.api.account.v3.service.AccountSearchByClientDtoService;
import com.bt.nextgen.api.account.v3.service.WrapAccountDetailDtoService;
import com.bt.nextgen.api.account.v3.validation.WrapAccountDetailsDtoErrorMapper;
import com.bt.nextgen.api.util.ApiConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.*;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller("AccountApiControllerV3")
@RequestMapping(produces = "application/json")
public class AccountApiController {

    public static final String V3_0 = "v3_0";
    @Autowired
    private WrapAccountDetailDtoService wrapAccountDetailDtoService;

    @Autowired
    private AccountDtoService accountDtoService;

    @Autowired
    private WrapAccountDetailsDtoErrorMapper wrapAccountDetailsDtoErrorMapper;

    @Autowired
    private AccountBalanceDtoService accountBalanceDtoService;

    @Autowired
    private UserProfileService profileService;

    @Autowired
    private AccountSearchByAccountDtoService accountSearchByAccountDtoService;

    @Autowired
    private AccountSearchByClientDtoService accountSearchbyClientDtoService;

    @RequestMapping(method = RequestMethod.GET, value = "${api.account.v3.uri.account}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public
    @ResponseBody
    ApiResponse getAccount(@PathVariable("account-id") String accountId,
                           @RequestParam(required = false, value = "cache") Boolean useCache,
                           @RequestParam(required = false, value = "type") String type) {

        final List<ApiSearchCriteria> criteria = new ArrayList<>();
        criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS, accountId,
                ApiSearchCriteria.OperationType.STRING));
        // If the value of cache is true, get the cacheType and send the value to factory by appending 'cache'
        // eg: accountOverview should be sent as 'cacheAccountOverview
        final StringBuilder cacheType = new StringBuilder();
        if (Boolean.TRUE.equals(useCache)) {
            cacheType.append("cache").append(type != null ? StringUtils.capitalize(type) : "");
        }
        criteria.add(new ApiSearchCriteria(Attribute.CACHE, ApiSearchCriteria.SearchOperation.EQUALS, cacheType.toString(),
                ApiSearchCriteria.OperationType.STRING));

        return new SearchOneByCriteria<>(V3_0, wrapAccountDetailDtoService, criteria).performOperation();
    }

    @InitBinder("updateAccountDetail")
    public void updateInitBinder(WebDataBinder binder) {
        binder.setAllowedFields("cGTLMethodId", "primaryContact", "modificationSeq", "statementPref", "cmaStatementPref");
    }

    @RequestMapping(method = RequestMethod.POST, value = "${api.account.v3.uri.account}")
    @PreAuthorize("isAuthenticated() and @acctPermissionService.hasTransactionPermission(#accountId, 'account.details.update')")
    public
    @ResponseBody
    ApiResponse update(@PathVariable("account-id") String accountId,
                       @ModelAttribute ("updateAccountDetail") WrapAccountDetailDto wrapAccountDetailDto, BindingResult bindingResult) {
        if (!profileService.isEmulating()) {
            final AccountKey key = new AccountKey(accountId);
            wrapAccountDetailDto.setKey(key);
            return new Update(V3_0, wrapAccountDetailDtoService, wrapAccountDetailsDtoErrorMapper,
                    wrapAccountDetailDto).performOperation();
        }
        else {
            throw new AccessDeniedException("Access Denied");
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "${api.account.v3.uri.balance}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public
    @ResponseBody
    KeyedApiResponse<AccountKey> getAccountBalance(@PathVariable("account-id") String accountId) {
        final AccountKey key = new AccountKey(accountId);
        return new FindByKey<>(V3_0, accountBalanceDtoService, key).performOperation();
    }

    //This API does not return Account balances
    @RequestMapping(method = RequestMethod.GET, value = {"${api.account.v3.uri.accounts}", "${api.account.v3.uri.search.all.number}", "${api.account.v3.uri.search.all.name}"})
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_intermediary_client_reports')")
    public
    @ResponseBody
    ApiResponse getAccounts(@RequestParam(required = false, value = "filter") String filter,
                            @RequestParam(required = false, value = "sortby") String orderby,
                            @RequestParam(required = false, value = "paging") String paging) {
        ApiResponse response = null;
        if (filter != null && orderby != null) {
            response = new PageFilter<>(V3_0, new Sort<>(new SearchByCriteria<>(V3_0,
                    accountDtoService, filter), orderby), paging).performOperation();
        }
        if (orderby != null && response == null) {
            response = new PageFilter<>(V3_0, new Sort<>(new FindAll<>(V3_0,
                    accountDtoService), orderby), paging).performOperation();
        }
        if (filter != null && response == null) {
            response = new PageFilter<>(V3_0, new SearchByCriteria<>(V3_0,
                    accountDtoService, filter), paging).performOperation();
        }
        if (paging != null && response == null) {
            response = new PageFilter<>(V3_0, new FindAll<>(V3_0, accountDtoService),
                    paging).performOperation();
        }
        else {
            if (response == null) {
                response = new FindAll<>(V3_0, accountDtoService).performOperation();
            }
        }
        return response;
    }

    //This API does not return Account balances
    @RequestMapping(method = RequestMethod.GET, value = "${api.account.v3.uri.search}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_intermediary_client_reports')")
    public
    @ResponseBody
    ApiResponse searchAccount(@RequestParam(required = false, value = ApiConstants.QUERY) String query,
                              @RequestParam(required = false, value = ApiConstants.ACCOUNT_STATUS) String status) {
        final List<ApiSearchCriteria> filterCriteria = new ArrayList<ApiSearchCriteria>();

        if (StringUtil.isNotNullorEmpty(status) && status.contains(",")) {
            final List<String> statusList = Arrays.asList(status.split(","));
            for (String state : statusList) {
                filterCriteria.add(new ApiSearchCriteria("accountStatus", ApiSearchCriteria.SearchOperation.EQUALS, state,
                        ApiSearchCriteria.OperationType.STRING));
            }
        }
        else if (StringUtil.isNotNullorEmpty(status)) {
            filterCriteria.add(new ApiSearchCriteria("accountStatus", ApiSearchCriteria.SearchOperation.EQUALS, status,
                    ApiSearchCriteria.OperationType.STRING));
        }

        return new Sort<>(new ServiceFilter<>(V3_0, accountDtoService, query, filterCriteria),
                "accountName").performOperation();
    }

    @RequestMapping(method = RequestMethod.GET, value = "${api.account.v3.uri.balance.list}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public
    @ResponseBody
    ApiResponse getAccountBalances(@PathVariable("account-id-list") List<String> accountIdList) {
        final List<ApiSearchCriteria> filterCriteria = new ArrayList<ApiSearchCriteria>();
        for (String account : accountIdList) {
            filterCriteria.add(new ApiSearchCriteria(account, account));
        }
        final AccountBalanceDtoService accountBalanceDtoService = this.accountBalanceDtoService;
        return new SearchByCriteria<>(V3_0, accountBalanceDtoService, filterCriteria)
                .performOperation();

    }

    //This API allows you to search by account id
    @RequestMapping(method = RequestMethod.GET, value = "${api.account.v3.uri.search.by.name}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_intermediary_reports')")
    public
    @ResponseBody
    ApiResponse searchAccountsByAccountName(@PathVariable("search-text") String searchText,
                                            @RequestParam(required = false, value = "filter") String filter,
                                            @RequestParam(required = false, value = "sortby") String orderby,
                                            @RequestParam(required = false, value = "paging") String paging) {
        //Combined key to add in search type, so quick reuse of AccountDto can be done for April
        String key = AccountSearchTypeEnum.ACCOUNT_NAME.toString() + "," + searchText;
        return getAccountSearchResponse(key, filter, orderby, paging);
    }

    //This API allows you to search by account name
    @RequestMapping(method = RequestMethod.GET, value = "${api.account.v3.uri.search.by.number}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_intermediary_reports')")
    public
    @ResponseBody
    ApiResponse searchAccountsByAccountNumber(@PathVariable("search-text") String searchText,
                                              @RequestParam(required = false, value = "filter") String filter,
                                              @RequestParam(required = false, value = "sortby") String orderby,
                                              @RequestParam(required = false, value = "paging") String paging) {
        //Combined key to add in search type, so quick reuse of AccountDto can be done for April
        String key = AccountSearchTypeEnum.ACCOUNT_ID.toString() + "," + searchText;
        return getAccountSearchResponse(key, filter, orderby, paging);
    }

    @SuppressWarnings("squid:S1142")
    private ApiResponse getAccountSearchResponse(String key, String filter, String orderby, String paging) {
        if (filter != null && orderby != null) {
            return new PageFilter<>(V3_0, new Sort<>(new SearchByKeyedCriteria<>(V3_0, accountSearchByAccountDtoService,
                new AccountKey(key), filter), orderby), paging).performOperation();
        }
        if (orderby != null) {
            return new PageFilter<>(V3_0, new Sort<>(new SearchByKey<>(V3_0, accountSearchByAccountDtoService, new AccountKey(key)),
                orderby), paging).performOperation();
        }
        if (filter != null) {
            return new PageFilter<>(V3_0, new SearchByKeyedCriteria<>(V3_0, accountSearchByAccountDtoService, new AccountKey(key),
                filter), paging).performOperation();
        }
        if (paging != null) {
            return new PageFilter<>(V3_0, new SearchByKey<>(V3_0, accountSearchByAccountDtoService, new AccountKey(key)),
                paging).performOperation();
        } else {
            return new SearchByKey<>(V3_0, accountSearchByAccountDtoService, new AccountKey(key)).performOperation();
        }
    }

    //This API allows you to search by client name and return their account details
    @RequestMapping(method = RequestMethod.GET, value = "${api.account.v3.uri.search.all.client}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_intermediary_reports')")
    @ResponseBody
    public ApiResponse searchAccountsByClientNameWithNoSearchText(@RequestParam(required = false, value = "filter") String filter,
                                                            @RequestParam(required = false, value = "sortby") String orderby,
                                                            @RequestParam(required = false, value = "paging") String paging) {

        return searchAccountsByClientName("", filter, orderby, paging);
    }

    @RequestMapping(method = RequestMethod.GET, value = "${api.account.v3.uri.search.by.client}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_intermediary_reports')")
    @ResponseBody
    public ApiResponse searchAccountsByClientNameWithSearch(@PathVariable("search-text") String searchText,
                                                            @RequestParam(required = false, value = "filter") String filter,
                                                            @RequestParam(required = false, value = "sortby") String orderby,
                                                            @RequestParam(required = false, value = "paging") String paging) {

        return searchAccountsByClientName(searchText, filter, orderby, paging);
    }

    private ApiResponse searchAccountsByClientName(String searchText, String filter, String orderby, String paging) {
        AccountSearchKey key = new AccountSearchKey(searchText, AccountSearchTypeEnum.CLIENT_NAME);
        ApiResponse response = null;
        if (filter != null && orderby != null) {
            response = new PageFilter<>(V3_0, new Sort<>(new SearchByKeyedCriteria<>(V3_0, accountSearchbyClientDtoService, key, filter), orderby),
                    paging).performOperation();
        }
        if (orderby != null && response == null) {
            response = new PageFilter<>(V3_0, new Sort<>(new SearchByKey<>(V3_0, accountSearchbyClientDtoService, key), orderby),
                    paging).performOperation();
        }
        if (filter != null && response == null) {
            response = new PageFilter<>(V3_0, new SearchByKeyedCriteria<>(V3_0, accountSearchbyClientDtoService, key, filter),
                    paging).performOperation();
        }
        if (paging != null && response == null) {
            response = new PageFilter<>(V3_0, new SearchByKey<>(V3_0, accountSearchbyClientDtoService, key), paging).performOperation();
        }
        else {
            if (response == null) {
                response = new SearchByKey<>(V3_0, accountSearchbyClientDtoService, key).performOperation();
            }
        }
        return response;
    }
}
