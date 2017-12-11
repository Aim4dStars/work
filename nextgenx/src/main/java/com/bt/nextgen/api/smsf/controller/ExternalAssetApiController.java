package com.bt.nextgen.api.smsf.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.smsf.model.ExternalAssetHoldingsValuationDto;
import com.bt.nextgen.api.smsf.model.ExternalAssetTrxnDto;
import com.bt.nextgen.api.smsf.service.SaveExternalAssetsService;
import com.bt.nextgen.api.smsf.service.ViewExternalHoldingsDtoService;
import com.bt.nextgen.api.smsf.validation.SaveExternalAssetsErrorMapper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.core.api.operation.Submit;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.btfin.panorama.core.security.profile.UserProfileService;

/**
 * Api controller for external assets save/update
 */
@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class ExternalAssetApiController
{
	private static final Logger logger = LoggerFactory.getLogger(ExternalAssetApiController.class);

	@Autowired
	private UserProfileService profileService;

	@Autowired
	private SaveExternalAssetsService saveExternalAssetsService;

	@Autowired
	private ViewExternalHoldingsDtoService viewExternalHoldingsDtoService;

	@Autowired
	private SaveExternalAssetsErrorMapper saveExternalAssetsErrorMapper;

	@RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.EXTERNAL_ASSETS)
	@PreAuthorize("  @acctPermissionService.canTransact(#accId, 'account.portfolio.externalassets.update')")
	public @ResponseBody
	KeyedApiResponse <AccountKey> saveExternalAssets(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accId,
		@RequestBody ExternalAssetTrxnDto externalAssetTrxnDto)
	{
		if (!profileService.isEmulating())
		{
			AccountKey key = new AccountKey(accId);
			externalAssetTrxnDto.setKey(key);
			return new Submit <AccountKey, ExternalAssetTrxnDto>(ApiVersion.CURRENT_VERSION,
				saveExternalAssetsService,
				saveExternalAssetsErrorMapper,
				externalAssetTrxnDto).performOperation();
		}
		else
		{
			throw new AccessDeniedException("Access Denied");
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.EXTERNAL_ASSETS)
	@PreAuthorize("@acctPermissionService.canTransact(#accId, 'account.portfolio.externalassets.view')")
	public @ResponseBody
	ApiResponse retrieveExternalAssets(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accId)
	{
		if (StringUtils.isEmpty(accId))
		{
			logger.info("Account id is not valid");
			throw new IllegalArgumentException("Account id is not valid");
		}

		ApiSearchCriteria accountKey = new ApiSearchCriteria("account_id",
			ApiSearchCriteria.SearchOperation.EQUALS,
			accId,
			ApiSearchCriteria.OperationType.STRING);
		List <ApiSearchCriteria> criteriaList = new ArrayList <>();
		criteriaList.add(accountKey);

		ApiResponse response = new SearchByCriteria <ExternalAssetHoldingsValuationDto>(ApiVersion.CURRENT_VERSION,
			viewExternalHoldingsDtoService,
			criteriaList).performOperation();
		return response;
	}



}