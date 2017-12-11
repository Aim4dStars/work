package com.bt.nextgen.api.tdmaturities.controller;

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

import com.bt.nextgen.api.tdmaturities.service.TDMaturitiesDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.api.operation.BeanFilter;
import com.bt.nextgen.core.api.operation.ControllerOperation;
import com.bt.nextgen.core.api.operation.PageFilter;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.web.controller.cash.util.Attribute;

@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class TDMaturitiesApiController
{
	private static final String STATUS = "status";
	@Autowired
	private TDMaturitiesDtoService tdmaturitiesDtoService;

	@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports') and hasPermission(null, 'View_intermediary_reports')")
	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ADVISER_TD_MATURITIES, produces = "application/json")
	public @ResponseBody
	ApiResponse getTDMaturities(@RequestParam(value = "search-criteria", required = false) String searchCriteria,
		@RequestParam(value = "paging", required = false) String paging,
		@PathVariable(UriMappingConstants.ADVISER_ID_URI_MAPPING) String adviserId) throws Exception
	{
		List <ApiSearchCriteria> criteriaForTDTypes = new ArrayList <ApiSearchCriteria>();
		List <ApiSearchCriteria> searchCriterias = ApiSearchCriteria.parseQueryString(ApiVersion.CURRENT_VERSION, searchCriteria);
		if (null != searchCriterias)
		{
			for (ApiSearchCriteria apiSearchCriteria : searchCriterias)
			{
				if (apiSearchCriteria.getProperty().equalsIgnoreCase(STATUS))
				{
					criteriaForTDTypes.add(apiSearchCriteria);
				}
			}
		}
		searchCriterias.add(new ApiSearchCriteria(Attribute.ADVISER_ID, SearchOperation.EQUALS, adviserId, OperationType.STRING));

		ControllerOperation controllerOperation = new BeanFilter(ApiVersion.CURRENT_VERSION,
			new SearchByCriteria <>(ApiVersion.CURRENT_VERSION, tdmaturitiesDtoService, searchCriterias),
			criteriaForTDTypes);
		return new PageFilter <>(ApiVersion.CURRENT_VERSION, controllerOperation, paging).performOperation();
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ACCOUNT_TD_MATURITIES, produces = "application/json")
	public @ResponseBody
	ApiResponse getAccountTDMaturities(@RequestParam(value = "search-criteria", required = false) String searchCriteria,
		@RequestParam(value = "paging", required = false) String paging,
		@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId) throws Exception
	{
		List <ApiSearchCriteria> criteria = ApiSearchCriteria.parseQueryString(ApiVersion.CURRENT_VERSION, searchCriteria);
		criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, accountId, OperationType.STRING));

		return new PageFilter <>(ApiVersion.CURRENT_VERSION, new SearchByCriteria <>(ApiVersion.CURRENT_VERSION,
			tdmaturitiesDtoService,
			criteria), paging).performOperation();
	}
}