package com.bt.nextgen.api.cgt.controller;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.cgt.model.CgtKey;
import com.bt.nextgen.api.cgt.service.RealisedCgtDtoService;
import com.bt.nextgen.api.cgt.service.UnrealisedCgtDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.Group;

/**
 * CgtApiController - controller for accepting cgt requests
 * @author L061041
 *
 */
@Controller
@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class CgtApiController
{
	private static final String GROUP_BY_ASSET_TYPE = "ASSET_TYPE";
	private static final String GROUP_BY_SECURITY = "SECURITY";

	@Autowired
	private RealisedCgtDtoService realisedCgtDtoService;

	@Autowired
	private UnrealisedCgtDtoService unrealisedCgtDtoService;

	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.UNREALISED_CGT, produces = "application/json")
	@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
	public @ResponseBody
	KeyedApiResponse <CgtKey> getUnrealisedCgtAssetTypes(
		@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
		@RequestParam(value = "effective-date", required = false) String effectiveDateString,
		@RequestParam(value = Group.GROUP_PARAMETER, required = false) String groupBy) throws Exception
	{
		if (StringUtils.isBlank(effectiveDateString))
		{
			effectiveDateString = null;
		}

		DateTime effectiveDate = new DateTime(effectiveDateString);
		CgtKey key = new CgtKey(accountId, effectiveDate, effectiveDate, GROUP_BY_ASSET_TYPE);

		return new FindByKey <>(ApiVersion.CURRENT_VERSION, unrealisedCgtDtoService, key).performOperation();
	}

	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.UNREALISED_CGT_BY_SECURITY, produces = "application/json")
	@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
	public @ResponseBody
	ApiResponse getUnrealisedCgtSecurities(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
		@RequestParam(value = "effective-date", required = false) String effectiveDateString,
		@RequestParam(value = Group.GROUP_PARAMETER, required = false) String groupBy) throws Exception
	{
		if (StringUtils.isBlank(effectiveDateString))
		{
			effectiveDateString = null;
		}

		DateTime effectiveDate = new DateTime(effectiveDateString);
		CgtKey key = new CgtKey(accountId, effectiveDate, effectiveDate, GROUP_BY_SECURITY);

		return new FindByKey <>(ApiVersion.CURRENT_VERSION, unrealisedCgtDtoService, key).performOperation();
	}

	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.REALISED_CGT, produces = "application/json")
	@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
	public @ResponseBody
	ApiResponse getRealisedCgtAssetTypes(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
		@RequestParam(value = "start-date", required = false) String startDateStr,
		@RequestParam(value = "end-date", required = false) String endDateStr,
		@RequestParam(value = Group.GROUP_PARAMETER, required = false) String groupBy) throws Exception
	{
		if (StringUtils.isBlank(startDateStr))
		{
			startDateStr = null;
		}

		DateTime startDate = new DateTime(startDateStr);
		DateTime endDate = new DateTime(endDateStr);
		CgtKey key = new CgtKey(accountId, startDate, endDate, GROUP_BY_ASSET_TYPE);

		return new FindByKey <>(ApiVersion.CURRENT_VERSION, realisedCgtDtoService, key).performOperation();
	}

	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.REALISED_CGT_BY_SECURITY, produces = "application/json")
	@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
	public @ResponseBody
	ApiResponse getRealisedCgtSecurities(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
		@RequestParam(value = "start-date", required = false) String startDateStr,
		@RequestParam(value = "end-date", required = false) String endDateStr,
		@RequestParam(value = Group.GROUP_PARAMETER, required = false) String groupBy) throws Exception
	{
		if (StringUtils.isBlank(startDateStr))
		{
			startDateStr = null;
		}

		DateTime startDate = new DateTime(startDateStr);
		DateTime endDate = new DateTime(endDateStr);
		CgtKey key = new CgtKey(accountId, startDate, endDate, GROUP_BY_SECURITY);

		return new FindByKey <>(ApiVersion.CURRENT_VERSION, realisedCgtDtoService, key).performOperation();
	}
}
