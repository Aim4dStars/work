package com.bt.nextgen.api.account.v1.controller;

import com.bt.nextgen.api.account.v1.model.AccountKey;
import com.bt.nextgen.api.account.v1.service.AvailableCashDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @deprecated Use V2
 */
@Deprecated
@Controller("AvailableCashApiControllerV1")
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class AvailableCashApiController
{
	@Autowired
	private AvailableCashDtoService availableCashDtoService;

	@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.AVAILABLE_CASH)
	public @ResponseBody
    ApiResponse getAvailableCash(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId)
	{
		AccountKey key = new AccountKey(accountId);
		return new FindByKey <>(ApiVersion.CURRENT_VERSION, availableCashDtoService, key).performOperation();
	}
}