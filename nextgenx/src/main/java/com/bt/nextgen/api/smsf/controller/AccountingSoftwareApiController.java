package com.bt.nextgen.api.smsf.controller;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.smsf.model.AccountingSoftwareDto;
import com.bt.nextgen.api.smsf.service.AccountingSoftwareDtoService;
import com.bt.nextgen.api.smsf.validation.AccountingSoftwareErrorMapper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.Update;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.btfin.panorama.core.security.profile.UserProfileService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Accounting Software Api Controller, exposes rest services to update accounting software for account's container (used
 * in external asset data feed).
 */
@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class AccountingSoftwareApiController {

    @Autowired
    private AccountingSoftwareDtoService accountingSoftwareDtoService;

    @Autowired
    private UserProfileService profileService;

    @Autowired
    private AccountingSoftwareErrorMapper errorMapper;

    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.ACCOUNTING_SOFTWARE, produces = "application/json")
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    KeyedApiResponse<AccountKey> updateDataFeedStatus(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
                                                 @ModelAttribute AccountingSoftwareDto software, BindingResult result)
	{
		AccountingSoftwareValidator validator = new AccountingSoftwareValidator();

		validator.validate(software, result);

		if (result.hasErrors())
		{
			throw new IllegalArgumentException("One or more input parameters not supported");
		}

        if (!profileService.isEmulating()) {
            AccountKey key = new AccountKey(accountId);
            software.setKey(key);
            return new Update<>(ApiVersion.CURRENT_VERSION, accountingSoftwareDtoService, errorMapper, software).performOperation();
        }
		else
		{
            throw new AccessDeniedException("Access Denied");
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ACCOUNTING_SOFTWARE)
    @PreAuthorize("@acctPermissionService.canTransact(#accId, 'account.portfolio.externalassets.view')")
    public @ResponseBody
    ApiResponse retrieveDataFeedStatus(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accId)
    {
        if (StringUtils.isEmpty(accId))
        {
            throw new IllegalArgumentException("Account id is not valid");
        }
        AccountKey key = new AccountKey(accId);
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, accountingSoftwareDtoService, key).performOperation();
    }
}
