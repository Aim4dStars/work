package com.bt.nextgen.api.termdeposit.controller;

import com.bt.nextgen.api.account.v2.model.TermDepositAccountDto;
import com.bt.nextgen.api.account.v2.service.TermDepositAccountDtoService;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.account.v3.validation.WrapAccountDetailsDtoErrorMapper;
import com.bt.nextgen.api.termdeposit.model.TermDepositDetailDto;
import com.bt.nextgen.api.termdeposit.service.TermDepositDtoService;
import com.bt.nextgen.api.termdeposit.service.TermDepositsDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.FindAll;
import com.bt.nextgen.core.api.operation.Sort;
import com.bt.nextgen.core.api.operation.Submit;
import com.bt.nextgen.core.api.operation.Update;
import com.bt.nextgen.core.api.operation.Validate;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.security.profile.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class TermDepositsApiController {
    private static final String TD_ACCOUNT_ID = "tdAccountId";

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private WrapAccountDetailsDtoErrorMapper errorMapper;

    @Autowired
    private TermDepositsDtoService termDepositsDtoService;

    @Autowired
    private TermDepositDtoService termDepositDtoService;

    @Autowired
    private TermDepositAccountDtoService termDepositAccountDtoService;

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ADVISER_TERM_DEPOSIT, produces =
            "application/json")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_adviser_dashboard_screen_Act_Panel')")
    @ResponseBody
    public ApiResponse getAdviserTermDeposits(
            @RequestParam(value = Sort.SORT_PARAMETER, required = false) String orderBy) throws Exception {
        return new Sort<>(new FindAll<>(ApiVersion.CURRENT_VERSION, termDepositsDtoService), orderBy)
                .performOperation();
    }

    @PreAuthorize("isAuthenticated() and hasPermission(#adviserId, 'isValidAdviser')")
    @RequestMapping(value = UriMappingConstants.UPDATE_TERM_DEPOSIT, method = RequestMethod.POST)
    @ResponseBody
    public KeyedApiResponse<com.bt.nextgen.api.account.v2.model.AccountKey> updateTermDepositForAccount(
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
            @RequestParam(TD_ACCOUNT_ID) String tdAccountId, @RequestParam(Attribute.RENEW_MODE_ID) String renewModeId)
            throws Exception {
        if (!userProfileService.isEmulating()) {
            TermDepositAccountDto termDepositAccountDto = new TermDepositAccountDto(
                    new com.bt.nextgen.api.account.v2.model.AccountKey(accountId), tdAccountId, renewModeId);
            return new Update<>(ApiVersion.CURRENT_VERSION, termDepositAccountDtoService, errorMapper,
                    termDepositAccountDto).performOperation();
        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }

    @PreAuthorize("isAuthenticated() and hasPermission(#adviserId, 'isValidAdviser')")
    @RequestMapping(value = UriMappingConstants.VALIDATE_TERM_DEPOSIT_BREAK, method = RequestMethod.GET)
    @ResponseBody
    public KeyedApiResponse<AccountKey> validateTDBreak(
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
            @RequestParam(TD_ACCOUNT_ID) String tdAccountId) {
        TermDepositDetailDto termDepositDto = new TermDepositDetailDto();
        AccountKey key = new AccountKey(accountId);
        termDepositDto.setTdAccountId(tdAccountId);
        termDepositDto.setKey(key);
        return new Validate<>(ApiVersion.CURRENT_VERSION, termDepositDtoService, errorMapper, termDepositDto)
                .performOperation();
    }

    @PreAuthorize("isAuthenticated() and hasPermission(#adviserId, 'isValidAdviser')")
    @RequestMapping(value = UriMappingConstants.SUBMIT_TERM_DEPOSIT_BREAK, method = RequestMethod.GET)
    public @ResponseBody
    KeyedApiResponse<AccountKey> submitTDBreak(
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
            @RequestParam(TD_ACCOUNT_ID) String tdAccountId) {
        TermDepositDetailDto termDepositDto = new TermDepositDetailDto();
        AccountKey key = new AccountKey(accountId);
        termDepositDto.setTdAccountId(tdAccountId);
        termDepositDto.setKey(key);
        return new Submit<>(ApiVersion.CURRENT_VERSION, termDepositDtoService, errorMapper, termDepositDto)
                .performOperation();
    }
}
