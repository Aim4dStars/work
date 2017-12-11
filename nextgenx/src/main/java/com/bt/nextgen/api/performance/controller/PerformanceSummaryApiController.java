package com.bt.nextgen.api.performance.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.performance.model.BenchmarkPerformanceDto;
import com.bt.nextgen.api.performance.service.BenchmarkPerformanceDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.btfin.panorama.core.security.avaloq.Constants;

@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class PerformanceSummaryApiController
{
	private static final String SEARCH_CRITERIA = "id";
	private static final String COMMA = ",";

	@Autowired
	private BenchmarkPerformanceDtoService benchmarkPerformanceDtoService;

	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.BENCHMARK_PERFORMANCE)
	@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
	public @ResponseBody
	ApiResponse getBenchmarkPerformance(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
		@RequestParam(value = UriMappingConstants.START_DATE_PARAMETER_MAPPING, required = false) String startDateString,
		@RequestParam(value = UriMappingConstants.END_DATE_PARAMETER_MAPPING, required = false) String endDateString,
		@RequestParam(value = SEARCH_CRITERIA, required = false) String searchCriteria) throws Exception
	{
		List <ApiSearchCriteria> criterias = new ArrayList <>();
		criterias.add(new ApiSearchCriteria("accountId", SearchOperation.EQUALS, accountId, OperationType.STRING));
		criterias.add(new ApiSearchCriteria("startDate", SearchOperation.EQUALS, startDateString, OperationType.STRING));
		criterias.add(new ApiSearchCriteria("endDate", SearchOperation.EQUALS, endDateString, OperationType.STRING));

		StringTokenizer st = new StringTokenizer(searchCriteria, COMMA);
		while (st.hasMoreTokens())
		{
			String token = st.nextToken();
			if (!token.equals(Constants.EMPTY_STRING))
			{
				criterias.add(new ApiSearchCriteria(SEARCH_CRITERIA, SearchOperation.EQUALS, token, OperationType.STRING));
			}
		}

		return new SearchByCriteria <BenchmarkPerformanceDto>(ApiVersion.CURRENT_VERSION,
			benchmarkPerformanceDtoService,
			criterias).performOperation();
	}
}
