package com.bt.nextgen.api.income.v1.controller;

import com.bt.nextgen.api.income.v1.model.IncomeDetailsKey;
import com.bt.nextgen.api.income.v1.model.IncomeDetailsType;
import com.bt.nextgen.api.income.v1.service.IncomeDetailsDtoService;
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
 * @deprecated use v2
 */
@Deprecated
@Controller("IncomeApiControllerV1")
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class IncomeApiController
{
	private static final String PARAM_START_DATE = "start-date";
	private static final String PARAM_END_DATE = "end-date";
	private static final String PARAM_INCOME_TYPE = "income-type";

	@Autowired
	private IncomeDetailsDtoService incomeDtoService;

	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.INCOME)
	@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
	public @ResponseBody
	KeyedApiResponse <IncomeDetailsKey> getIncome(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
		@RequestParam(value = PARAM_START_DATE, required = false) String startDateString,
		@RequestParam(value = PARAM_END_DATE, required = false) String endDateString,
            @RequestParam(value = PARAM_INCOME_TYPE, required = true) IncomeDetailsType incomeType) 
	{
		DateTime startDate = new DateTime(startDateString);
		DateTime endDate = new DateTime(endDateString);

		IncomeDetailsKey key = new IncomeDetailsKey(accountId, incomeType, startDate, endDate);

		return new FindByKey <>(ApiVersion.CURRENT_VERSION, incomeDtoService, key).performOperation();
	}
}
