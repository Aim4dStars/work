package com.bt.nextgen.api.account.v1.controller.drawdown;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.account.v1.model.DrawdownDto;
import com.bt.nextgen.api.account.v1.service.drawdown.DrawdownDtoService;
import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.Update;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.btfin.panorama.core.security.profile.UserProfileService;

/**
 * @deprecated Use V2
 */
@Deprecated
@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class DrawdownApiController {

    @Autowired
    private DrawdownDtoService drawdownDtoService;

    @Autowired
    private UserProfileService profileService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.DRAWDOWN)
    public @ResponseBody
    KeyedApiResponse<AccountKey> getDrawdown(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId)

    {
        AccountKey key = new AccountKey(accountId);
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, drawdownDtoService, key).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.UPDATE_DRAWDOWN)
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'BP_requests')")
    public @ResponseBody
    KeyedApiResponse<AccountKey> updateDrawdown(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
            @RequestParam(value = UriMappingConstants.DRAWDOWN_URI_MAPPING, required = true) String drawdown) {
        if (!profileService.isEmulating()) {
            AccountKey key = new AccountKey(accountId);
            DrawdownDto drawdownDto = new DrawdownDto(key, drawdown);
            return new Update<>(ApiVersion.CURRENT_VERSION, drawdownDtoService, null, drawdownDto).performOperation();
        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }

}
