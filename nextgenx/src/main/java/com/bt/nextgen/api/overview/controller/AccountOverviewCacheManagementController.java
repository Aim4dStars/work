package com.bt.nextgen.api.overview.controller;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.overview.service.AccountOverviewCacheManagementDtoService;
import com.bt.nextgen.api.overview.service.AccountOverviewDetailsDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.FindOne;
import com.bt.nextgen.service.avaloq.UserCacheService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
public class AccountOverviewCacheManagementController
{
	private Logger logger = LoggerFactory.getLogger(AccountOverviewCacheManagementController.class);

	@Autowired
	AccountOverviewCacheManagementDtoService accountOverviewCacheManagementDtoService;

	@Autowired
	UserCacheService userCacheService;

	@Autowired
	AccountOverviewDetailsDtoService accountOverviewDetailsDtoService;

	@RequestMapping(method = RequestMethod.GET, value="/secure/api/v1_0/accounts/{account-id}/delete_overview")
	//@PreAuthorize("@acctPermissionService.canTransact(#accId, 'account.portfolio.externalassets.view')")
	public @ResponseBody ApiResponse clearOverviewCache(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId)
	{
		if (StringUtils.isEmpty(accountId))
		{
			throw new IllegalArgumentException("Account id is not valid");
		}

		AccountKey key = new AccountKey(accountId);

		ApiResponse response = new FindByKey<>(ApiVersion.CURRENT_VERSION, accountOverviewCacheManagementDtoService, key).performOperation();

		return response;
	}


	@RequestMapping(method = RequestMethod.GET, value="/secure/api/v1_0/accounts/{account-id}/overview")
	//@PreAuthorize("@acctPermissionService.canTransact(#accId, 'account.portfolio.externalassets.view')")
	public @ResponseBody ApiResponse getCacheDetails(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId)
	{
		if (StringUtils.isEmpty(accountId))
		{
			throw new IllegalArgumentException("Account id is not valid");
		}

		AccountKey key = new AccountKey(accountId);

		ApiResponse response = new FindByKey<>(ApiVersion.CURRENT_VERSION, accountOverviewDetailsDtoService, key).performOperation();

		return response;
	}

}