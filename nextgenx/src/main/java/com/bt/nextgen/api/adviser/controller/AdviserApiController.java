package com.bt.nextgen.api.adviser.controller;

import com.bt.nextgen.api.adviser.model.AdviserSearchDto;
import com.bt.nextgen.api.adviser.model.AdviserSearchDtoKey;
import com.bt.nextgen.api.adviser.model.SingleAdviserForUserDto;
import com.bt.nextgen.api.adviser.service.AdviserSearchDtoService;
import com.bt.nextgen.api.adviser.service.SingleAdviserForUserDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.*;

import com.bt.nextgen.api.adviser.service.AdviserDetailDtoService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import static com.bt.nextgen.api.util.ApiConstants.PAGING;
import static com.bt.nextgen.api.util.ApiConstants.QUERY;

/**
 * This API retrieves a list of advisers which matches the search string
 * 
 * Sample request:
 * http://localhost:9080/ng/secure/api/v1_0/adviser?q=martin
 */
@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class AdviserApiController
{
	private static final String RESULT_SORTING_ORDER = "lastName,asc;firstName,asc";

	@Autowired
	private AdviserSearchDtoService adviserSearchDtoService;
	@Autowired
	private AdviserDetailDtoService adviserDetailDtoService;

	@Autowired
	private SingleAdviserForUserDtoService singleAdviserForUserDtoService;

	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ADVISER_SEARCH)
	@PreAuthorize("isAuthenticated()")
	public @ResponseBody
	ApiResponse getAdvisers(@RequestParam(required = true, value = QUERY) String searchKey,
		@RequestParam(required = false, value = PAGING) String paging) throws Exception
	{
		if (paging != null)
		{
			return new PageFilter <>(ApiVersion.CURRENT_VERSION, (new Sort <>(new SearchByKey <>(ApiVersion.CURRENT_VERSION,
				adviserSearchDtoService,
				new AdviserSearchDtoKey(searchKey)), RESULT_SORTING_ORDER)), paging).performOperation();
		}
		else
		{
			return new Sort <>(new SearchByKey <>(ApiVersion.CURRENT_VERSION,
				adviserSearchDtoService,
				new AdviserSearchDtoKey(searchKey)), RESULT_SORTING_ORDER).performOperation();
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ADVISER_BY_ID)
	@PreAuthorize("isAuthenticated()")
	public @ResponseBody
	ApiResponse getAdviserByPositionId(@PathVariable(UriMappingConstants.ADVISER_ID_URI_MAPPING) String positionId)
		throws Exception
	{
		return new FindByKey <AdviserSearchDtoKey, AdviserSearchDto>(ApiVersion.CURRENT_VERSION,
			adviserSearchDtoService,
			new AdviserSearchDtoKey(positionId)).performOperation();
	}

	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ADVISER_SINGLE_SEARCH)
	@PreAuthorize("isAuthenticated()")
	public @ResponseBody
	ApiResponse getSingleAllowedAdviser() throws Exception
	{
		return new FindOne <SingleAdviserForUserDto>(ApiVersion.CURRENT_VERSION, singleAdviserForUserDtoService).performOperation();
	}

	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ADVISER_DETAIL_URL)
	@PreAuthorize("isAuthenticated()")
	public @ResponseBody
	ApiResponse getAdviserDetails(@PathVariable(UriMappingConstants.CLIENT_ID_URI_MAPPING) String clientKey) throws Exception
	{
		ClientKey personKey = ClientKey.valueOf(EncodedString.toPlainText(clientKey));
		return new FindByKey <>(ApiVersion.CURRENT_VERSION, adviserDetailDtoService, personKey).performOperation();
	}

	/**
	 * consistent request parameter included for consistent encoded of brokerid/positionid of an adviser
	 * @param consistent
	 * @return
	 * @throws Exception
     */
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.USER_ADVISER_SEARCH)
	@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_intermediary_reports')")
	public @ResponseBody
	ApiResponse getAdviserForUser(@RequestParam(value = "consistent", required = false) Boolean consistent) throws Exception
	{
		List<ApiSearchCriteria> apiSearchCriterias = new ArrayList<>();
		if (Boolean.TRUE.equals(consistent)) {
			apiSearchCriterias.add(new ApiSearchCriteria(Attribute.CONSISTENT_ID_FLAG, consistent.toString()));
		}
		return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, adviserSearchDtoService, apiSearchCriterias).performOperation();

	}
}
