package com.bt.nextgen.api.dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.dashboard.model.PeriodKey;
import com.bt.nextgen.api.dashboard.service.AdviserDashboardService;
import com.bt.nextgen.api.dashboard.service.AdviserFUAByPortfolioBandDtoService;
import com.bt.nextgen.api.dashboard.service.TopAccountsByValueService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.FindOne;

@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class AdviserDashboardApiController
{

	@Autowired
	private AdviserDashboardService adviserDashboardService;

	@Autowired
	private AdviserFUAByPortfolioBandDtoService adviserFUAByPortfolioBandService;

	@Autowired
	private TopAccountsByValueService topAccountsService;

	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.DASHBOARD_PERFORMANCE)
	@PreAuthorize("isAuthenticated()")
	public @ResponseBody
	ApiResponse getDashboardPerformance(
		@RequestParam(value = UriMappingConstants.PERIOD_TYPE_MAPPING, required = true) String periodType)
	{
		PeriodKey periodKey = PeriodKey.valueOf(periodType);
		return new FindByKey <>(ApiVersion.CURRENT_VERSION, adviserDashboardService, periodKey).performOperation();

	}

	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.DASHBOARD_FUA_BY_PORTFOLIO_BAND)
	public @ResponseBody
	ApiResponse getPortfolioFUAByBand()
	{
		return new FindOne <>(ApiVersion.CURRENT_VERSION, adviserFUAByPortfolioBandService).performOperation();
	}

	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.DASHBOARD_TOP_ACCOUNTS)
	@PreAuthorize("isAuthenticated()")
	public @ResponseBody
	ApiResponse getDashboardTopAccounts()
	{
		return new FindOne <>(ApiVersion.CURRENT_VERSION, topAccountsService).performOperation();
	}

	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.DASHBOARD_SUMMARY)
	@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_adviser_dashboard_screen_Act_Panel')")
	public @ResponseBody
	ApiResponse getDashboardSummary()
	{
		return new FindOne <>(ApiVersion.CURRENT_VERSION, adviserDashboardService).performOperation();
	}

}
