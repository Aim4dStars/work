package com.bt.nextgen.api.contributioncaps.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.contributioncaps.service.ContributionCapDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.web.controller.cash.util.Attribute;

@SuppressWarnings("squid:UnusedProtectedMethod")
@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class ContributionCapApiController
{

	@Autowired
	private ContributionCapDtoService contributionCapDtoService;

	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.CONTRIBUTION_CAPS)
	@PreAuthorize("isAuthenticated()")
	public @ResponseBody
	ApiResponse getMemberContributionCaps(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accId,
		@RequestParam(value = "date", required = false) String date)
	{

		List <ApiSearchCriteria> criteria = new ArrayList <ApiSearchCriteria>();
		criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, accId, OperationType.STRING));
		criteria.add(new ApiSearchCriteria("date", SearchOperation.EQUALS, date, OperationType.STRING));

		return new SearchByCriteria <>(ApiVersion.CURRENT_VERSION, contributionCapDtoService, criteria).performOperation();

	}
}
