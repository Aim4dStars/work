package com.bt.nextgen.api.global.controller;

import com.bt.nextgen.core.api.operation.FindOne;
import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.global.model.GlobalDetailsDto;
import com.bt.nextgen.api.global.service.GlobalDetailsDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.core.api.operation.FindAll;

/**
 * This is a global service which retrieves values which need to be updated regularly on the UI side.
 * e.g. notification count in the global header
 */

@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class GlobalDetailsApiController
{

	@Autowired
	private GlobalDetailsDtoService globalDetailsService;

	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.GLOBAL)
	@PreAuthorize("isAuthenticated()")
	public @ResponseBody
	ApiResponse getGlobalUpdateDetails()
	{
		return new FindOne<>(ApiVersion.CURRENT_VERSION, globalDetailsService).performOperation();
	}

}
