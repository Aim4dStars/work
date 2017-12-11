package com.bt.nextgen.api.financialyear.controller;


import com.bt.nextgen.api.financialyear.service.FinancialYearDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("squid:UnusedProtectedMethod")
@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class FinancialYearApiController
{
	@Autowired
	private FinancialYearDtoService financialYearDtoService;


	/**
	 * Return a list of the last seven financial years for reporting purposes.<p>
	 * Does not take into consideration account inception date, or whether any data is actually available.
	 * @param accId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.FINANCIAL_YEAR)
	@PreAuthorize("isAuthenticated()")
	public @ResponseBody
	ApiResponse getAvailableFinancialYears(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accId)
	{
		List<ApiSearchCriteria> criteria = new ArrayList<ApiSearchCriteria>();
		criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS, accId, ApiSearchCriteria.OperationType.STRING));

		return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, financialYearDtoService, criteria).performOperation();
	}
}
