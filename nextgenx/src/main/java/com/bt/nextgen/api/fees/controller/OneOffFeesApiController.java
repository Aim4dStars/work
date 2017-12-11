package com.bt.nextgen.api.fees.controller;

import com.bt.nextgen.api.fees.model.OneOffFeesDto;
import com.bt.nextgen.api.fees.service.OneOffFeesDtoService;
import com.bt.nextgen.api.fees.validation.OneOffFeesDtoErrorMapper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.Create;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.Validate;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.integration.account.AccountKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API)
public class OneOffFeesApiController {

    @Autowired
    private OneOffFeesDtoService adviceFeesDtoService;

    @Autowired
    private OneOffFeesDtoErrorMapper oneOffAdviceFeesDtoErrorMapper;

    @Autowired
    private UserProfileService profileService;

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ONE_OFF_ADVICE_FEES, produces = "application/json")
    @PreAuthorize("isAuthenticated() and @acctPermissionService.hasTransactionPermission(#accountId, 'account.fee.advice.update')")
    public
    @ResponseBody
    KeyedApiResponse<AccountKey> getAdviseFees(
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId) {
        AccountKey key = AccountKey.valueOf(accountId);
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, adviceFeesDtoService, key).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.ONE_OFF_ADVICE_FEES, produces = "application/json")
    @PreAuthorize("isAuthenticated() and @acctPermissionService.hasTransactionPermission(#accId, 'account.fee.advice.update')")
    public
    @ResponseBody
    KeyedApiResponse<AccountKey> submitAdviserFees(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accId,
                                                   @ModelAttribute("oneOffAdviceFees") OneOffFeesDto oneOffFeesDto) {
        if (!profileService.isEmulating()) {
            AccountKey key = AccountKey.valueOf(accId);
            oneOffFeesDto.setKey(key);
            return new Create<>(ApiVersion.CURRENT_VERSION, adviceFeesDtoService, oneOffFeesDto).performOperation();
        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.VALIDATE_ADVICE_FEES)
    @PreAuthorize("isAuthenticated() and @acctPermissionService.hasTransactionPermission(#accId, 'account.fee.advice.update')")
    public
    @ResponseBody
    KeyedApiResponse<AccountKey> validateAdviserFees(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accId,
                                                     @ModelAttribute("oneOffAdviceFees") OneOffFeesDto oneOffFeesDto) {
        AccountKey key = AccountKey.valueOf(accId);
        oneOffFeesDto.setKey(key);
        return new Validate<>(ApiVersion.CURRENT_VERSION, adviceFeesDtoService, oneOffAdviceFeesDtoErrorMapper,
                oneOffFeesDto).performOperation();
    }
}
