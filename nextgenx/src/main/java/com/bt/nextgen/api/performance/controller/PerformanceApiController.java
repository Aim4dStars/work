package com.bt.nextgen.api.performance.controller;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.account.v2.model.DateRangeAccountKey;
import com.bt.nextgen.api.performance.model.AccountPerformanceKey;
import com.bt.nextgen.api.performance.service.AccountPerformanceChartDtoService;
import com.bt.nextgen.api.performance.service.PerformanceDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;

@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class PerformanceApiController {

    private static final String START_DATE_PARAMETER_MAPPING = "start-date";
    private static final String END_DATE_PARAMETER_MAPPING = "end-date";
    private static final String BENCHMARK = "benchmark";

    @Autowired
    private PerformanceDtoService performanceDtoService;

    @Autowired
    private AccountPerformanceChartDtoService performanceChartDtoService;

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.PERFORMANCE)
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public
    @ResponseBody
    KeyedApiResponse<DateRangeAccountKey> getPorfolioPerformance(
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
            @RequestParam(value = START_DATE_PARAMETER_MAPPING, required = false) String startDateString,
            @RequestParam(value = END_DATE_PARAMETER_MAPPING, required = false) String endDateString) throws Exception {
        DateRangeAccountKey key;
        if (StringUtils.isBlank(startDateString)) {
            startDateString = null;
        }
        if (StringUtils.isBlank(endDateString)) {
            endDateString = null;
        }
        DateTime startDate = new DateTime(startDateString);
        DateTime endDate = new DateTime(endDateString);
        key = new DateRangeAccountKey(accountId, startDate, endDate);

        return new FindByKey<>(ApiVersion.CURRENT_VERSION, performanceDtoService, key).performOperation();
    }

    /**
     * Generates performance report based on the start & end date for an account (for performance charts)
     *
     * @param accountId       - Account to evaluate performance data for
     * @param startDateString - start date
     * @param endDateString   - end date
     * @param benchmark       - benchmark id for calculation of performance data
     * @return
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ACCOUNT_PERFORMANCE_CHART)
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public
    @ResponseBody
    KeyedApiResponse<AccountPerformanceKey> getAccountPerformanceChartData(
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
            @RequestParam(value = START_DATE_PARAMETER_MAPPING, required = true) String startDateString,
            @RequestParam(value = END_DATE_PARAMETER_MAPPING, required = true) String endDateString,
            @RequestParam(value = BENCHMARK, required = false) String benchmark) {

        DateTime startDate = null;
        DateTime endDate = null;
        String benchmarkId = StringUtils.isNotBlank(benchmark) ? benchmark : "-1";

        if (StringUtils.isNotBlank(startDateString) && StringUtils.isNotBlank(endDateString)) {
            startDate = new DateTime(startDateString);
            endDate = new DateTime(endDateString);
        }

        AccountPerformanceKey key = new AccountPerformanceKey(accountId, startDate, endDate, benchmarkId);

        return new FindByKey<>(ApiVersion.CURRENT_VERSION, performanceChartDtoService, key).performOperation();
    }
}
