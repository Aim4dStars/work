package com.bt.nextgen.api.morningstar.controller;

import com.bt.nextgen.api.morningstar.model.MorningstarUrlKey;
import com.bt.nextgen.api.morningstar.service.MorningstarUrlDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class MorningstarUrlApiController {

	@Autowired
	private MorningstarUrlDtoService encryptionService;

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.MORNINGSTAR_FUND_PROFILE_URL)
	@ResponseBody
	public ApiResponse getFundProfileUrl(@PathVariable(UriMappingConstants.ASSET_ID) String assetId) {
		if (StringUtils.isEmpty(assetId) || !StringUtils.isNumeric(assetId)) {
			throw new BadRequestException("Invalid asset ID");
		}

		return new FindByKey<>(ApiVersion.CURRENT_VERSION, encryptionService, new MorningstarUrlKey(assetId)).performOperation();
	}
}
