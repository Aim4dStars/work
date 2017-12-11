/**
 * 
 */
package com.bt.nextgen.api.performance.controller;

import static org.junit.Assert.*;

import com.bt.nextgen.api.account.v1.model.DateRangeAccountKey;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import com.bt.nextgen.api.performance.model.BenchmarkPerformanceDto;
import com.bt.nextgen.api.performance.model.PerformanceDto;
import com.bt.nextgen.api.performance.model.PortfolioPerformanceDto;
import com.bt.nextgen.api.performance.service.BenchmarkPerformanceDtoService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;

/**
 * @author L072463
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PerformanceSummaryApiControllerTest
{

	@InjectMocks
	PerformanceSummaryApiController performanceSummaryApiController;

	@Mock
	private BenchmarkPerformanceDtoService benchmarkPerformanceDtoService;

	@Mock
	private static AnnotationMethodHandlerAdapter annotationMethodHandler;
	private MockHttpServletRequest mockHttpServletRequest;
	private MockHttpServletResponse mockHttpServletResponse;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{

		mockHttpServletRequest = new MockHttpServletRequest(RequestMethod.GET.name(), "/");
		mockHttpServletResponse = new MockHttpServletResponse();
		annotationMethodHandler = new AnnotationMethodHandlerAdapter();
		HttpMessageConverter[] messageConverters =
		{
			new MappingJackson2HttpMessageConverter()
		};
		annotationMethodHandler.setMessageConverters(messageConverters);

	}

	/**
	 * Test method for {@link com.bt.nextgen.api.performance.controller.PerformanceSummaryApiController#getBenchmarkPerformance(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testGetBenchmarkPerformance() throws Exception
	{
		List <BenchmarkPerformanceDto> benchmarkPerformanceDtos = new ArrayList <BenchmarkPerformanceDto>();
		BenchmarkPerformanceDto benchmarkPerformanceDto = new BenchmarkPerformanceDto("name", "id", new BigDecimal(100));
		benchmarkPerformanceDtos.add(benchmarkPerformanceDto);

		Mockito.when(benchmarkPerformanceDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class),
			Mockito.any(ServiceErrors.class))).thenReturn(benchmarkPerformanceDtos);

		mockHttpServletRequest.setParameter("id", "70643,,");
		mockHttpServletRequest.setParameter(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, "accountId");
		mockHttpServletRequest.setParameter(UriMappingConstants.START_DATE_PARAMETER_MAPPING, "2015-03-11");
		mockHttpServletRequest.setParameter(UriMappingConstants.END_DATE_PARAMETER_MAPPING, "2015-03-12");

		mockHttpServletRequest.setRequestURI("/secure/api/v1_0/accounts/C77D12EEFB4C2E219EB58C96951346085CCD746B57EA0B6C/benchmark-performance");
		mockHttpServletRequest.setMethod("GET");
		annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, performanceSummaryApiController);

	}
}
