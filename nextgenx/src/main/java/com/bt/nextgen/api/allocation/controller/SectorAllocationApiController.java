package com.bt.nextgen.api.allocation.controller;

import com.bt.nextgen.api.account.v1.model.DatedAccountKey;
import com.bt.nextgen.api.allocation.service.AllocationDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @deprecated use account.v2.controller.allocation
 */
@Deprecated
@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class SectorAllocationApiController
{
	private static final String EFFECTIVE_DATE_PARAMETER_MAPPING = "effective-date";

	@Autowired
	private AllocationDtoService allocationService;

	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ALLOCATION)
	@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
	public @ResponseBody
	KeyedApiResponse <DatedAccountKey> getAssetAllocationDetails(
		@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
		@RequestParam(value = EFFECTIVE_DATE_PARAMETER_MAPPING, required = false) String effectiveDateString) throws Exception
	{
		DatedAccountKey key;
		DateTime effectiveDate = new DateTime(effectiveDateString);
		key = new DatedAccountKey(accountId, effectiveDate);

		return new FindByKey <>(ApiVersion.CURRENT_VERSION, allocationService, key).performOperation();
	}

}
