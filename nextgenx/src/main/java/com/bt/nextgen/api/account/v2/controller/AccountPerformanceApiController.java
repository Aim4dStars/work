package com.bt.nextgen.api.account.v2.controller;

import com.bt.nextgen.api.account.v2.model.DateRangeAccountKey;
import com.bt.nextgen.api.account.v2.model.DatedAccountKey;
import com.bt.nextgen.api.account.v2.model.performance.AccountBenchmarkPerformanceKey;
import com.bt.nextgen.api.account.v2.service.performance.AccountBenchmarkPerformanceChartDtoService;
import com.bt.nextgen.api.account.v2.service.performance.AccountPerformanceChartDtoService;
import com.bt.nextgen.api.account.v2.service.performance.AccountPerformanceInceptionDtoService;
import com.bt.nextgen.api.account.v2.service.performance.AccountPerformanceOverallDtoService;
import com.bt.nextgen.api.account.v2.service.performance.AccountPerformanceTotalDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Deprecated
@Controller
@RequestMapping(produces = "application/json")
public class AccountPerformanceApiController {
    @Autowired
    private AccountPerformanceChartDtoService accountPerformanceChartDtoService;
    
    @Autowired
    private AccountPerformanceInceptionDtoService accountPerformanceInceptionDtoService;

    @Autowired
    private AccountPerformanceTotalDtoService accountPerformanceTotalDtoService;

    @Autowired
    private AccountPerformanceOverallDtoService accountPerformanceOverallDtoService;

    @Autowired
    private AccountBenchmarkPerformanceChartDtoService accountBenchmarkPerformanceDtoService;

    @RequestMapping(method = RequestMethod.GET, value = "${api.account.v2.uri.performanceChart}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public @ResponseBody
    KeyedApiResponse<DateRangeAccountKey> getAccountPerformanceChartData(
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
            @RequestParam(value = UriMappingConstants.START_DATE_PARAMETER_MAPPING, required = true) String startDateString,
            @RequestParam(value = UriMappingConstants.END_DATE_PARAMETER_MAPPING, required = true) String endDateString) {

        DateTime startDate = null;
        DateTime endDate = null;

        if (StringUtils.isNotBlank(startDateString) && StringUtils.isNotBlank(endDateString)) {
            startDate = new DateTime(startDateString);
            endDate = new DateTime(endDateString);
        }

        DateRangeAccountKey key = new DateRangeAccountKey(accountId, startDate, endDate);

        return new FindByKey<>(ApiVersion.CURRENT_VERSION, accountPerformanceChartDtoService, key).performOperation();
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "${api.account.v2.uri.performanceInception}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public @ResponseBody
    KeyedApiResponse<DatedAccountKey> getAccountPerformanceInceptionData(
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
            @RequestParam(value = "effective-date", required = true) String effectiveDateStr) {

        DateTime effectiveDate = DateTime.parse(effectiveDateStr);
        DatedAccountKey key = new DatedAccountKey(accountId, effectiveDate == null ? new DateTime() : effectiveDate);

        return new FindByKey<>(ApiVersion.CURRENT_VERSION, accountPerformanceInceptionDtoService, key).performOperation();
    }

    @RequestMapping(method = RequestMethod.GET, value = "${api.account.v2.uri.performanceTotal}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public @ResponseBody
    KeyedApiResponse<DateRangeAccountKey> getAccountPerformanceTotalData(
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
            @RequestParam(value = UriMappingConstants.START_DATE_PARAMETER_MAPPING, required = true) String startDateString,
            @RequestParam(value = UriMappingConstants.END_DATE_PARAMETER_MAPPING, required = true) String endDateString) {

        DateTime startDate = null;
        DateTime endDate = null;

        if (StringUtils.isNotBlank(startDateString) && StringUtils.isNotBlank(endDateString)) {
            startDate = new DateTime(startDateString);
            endDate = new DateTime(endDateString);
        }

        DateRangeAccountKey key = new DateRangeAccountKey(accountId, startDate, endDate);
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, accountPerformanceTotalDtoService, key).performOperation();
    }

    @RequestMapping(method = RequestMethod.GET, value = "${api.account.v2.uri.performanceOverall}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public @ResponseBody
    KeyedApiResponse<DateRangeAccountKey> getAccountPerformanceOverallData(
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
            @RequestParam(value = UriMappingConstants.START_DATE_PARAMETER_MAPPING, required = true) String startDateString,
            @RequestParam(value = UriMappingConstants.END_DATE_PARAMETER_MAPPING, required = true) String endDateString) {

        DateTime startDate = null;
        DateTime endDate = null;

        if (StringUtils.isNotBlank(startDateString) && StringUtils.isNotBlank(endDateString)) {
            startDate = new DateTime(startDateString);
            endDate = new DateTime(endDateString);
        }

        DateRangeAccountKey key = new DateRangeAccountKey(accountId, startDate, endDate);
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, accountPerformanceOverallDtoService, key).performOperation();
    }

    @RequestMapping(method = RequestMethod.GET, value = "${api.account.v2.uri.benchmarkPerformance}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public @ResponseBody
    KeyedApiResponse<AccountBenchmarkPerformanceKey> getAccountBenchmarkPerformanceChartData(
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
            @RequestParam(value = UriMappingConstants.START_DATE_PARAMETER_MAPPING, required = true) String startDateString,
            @RequestParam(value = UriMappingConstants.END_DATE_PARAMETER_MAPPING, required = true) String endDateString,
            @RequestParam(value = UriMappingConstants.BENCHMARK_ID, required = true) String benchmarkId) {

        DateTime startDate = null;
        DateTime endDate = null;

        if (StringUtils.isNotBlank(startDateString) && StringUtils.isNotBlank(endDateString)) {
            startDate = new DateTime(startDateString);
            endDate = new DateTime(endDateString);
        }

        AccountBenchmarkPerformanceKey key = new AccountBenchmarkPerformanceKey(accountId, startDate, endDate, benchmarkId);

        return new FindByKey<>(ApiVersion.CURRENT_VERSION, accountBenchmarkPerformanceDtoService, key).performOperation();
    }
}
