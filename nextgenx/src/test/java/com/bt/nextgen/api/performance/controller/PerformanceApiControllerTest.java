/**
 *
 */
package com.bt.nextgen.api.performance.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Assert;
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

import com.bt.nextgen.api.account.v2.model.DateRangeAccountKey;
import com.bt.nextgen.api.performance.model.AccountPerformanceChartDto;
import com.bt.nextgen.api.performance.model.AccountPerformanceKey;
import com.bt.nextgen.api.performance.model.PerformanceDto;
import com.bt.nextgen.api.performance.model.PortfolioPerformanceDto;
import com.bt.nextgen.api.performance.model.ReportDataPointDto;
import com.bt.nextgen.api.performance.service.AccountPerformanceChartDtoService;
import com.bt.nextgen.api.performance.service.PerformanceDtoService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.performance.PerformancePeriodType;

import static org.mockito.Mockito.when;

/**
 * @author L072463
 */
@RunWith(MockitoJUnitRunner.class)
public class PerformanceApiControllerTest {

    @InjectMocks
    PerformanceApiController performanceApiController;

    @Mock
    private PerformanceDtoService performanceDtoService;

    @Mock
    private AccountPerformanceChartDtoService performanceChartDtoService;

    @Mock
    private static AnnotationMethodHandlerAdapter annotationMethodHandler;
    private MockHttpServletRequest mockHttpServletRequest;
    private MockHttpServletResponse mockHttpServletResponse;
    AccountPerformanceChartDto accountPerformanceChartDto;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        mockHttpServletRequest = new MockHttpServletRequest(RequestMethod.GET.name(), "/");
        mockHttpServletResponse = new MockHttpServletResponse();
        annotationMethodHandler = new AnnotationMethodHandlerAdapter();
        HttpMessageConverter[] messageConverters = {new MappingJackson2HttpMessageConverter()};
        annotationMethodHandler.setMessageConverters(messageConverters);

    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.performance.controller.PerformanceApiController#getPorfolioPerformance(java.lang.String, java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public final void testGetPorfolioPerformance() throws Exception {
        PortfolioPerformanceDto portfolioPerformanceDto = new PortfolioPerformanceDto(new DateRangeAccountKey("accountId",
                new DateTime(), new DateTime()), new BigDecimal(100), new BigDecimal(100), new BigDecimal(100), new BigDecimal(
                100), new BigDecimal(100), new BigDecimal(100), new BigDecimal(100), new ArrayList<PerformanceDto>());

        when(performanceDtoService.find(Mockito.any(DateRangeAccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(portfolioPerformanceDto);

        mockHttpServletRequest.setParameter(UriMappingConstants.START_DATE_PARAMETER_MAPPING, "2015-03-11");
        mockHttpServletRequest.setParameter(UriMappingConstants.END_DATE_PARAMETER_MAPPING, "2015-03-12");

        mockHttpServletRequest
                .setRequestURI("/secure/api/v1_0/accounts/C77D12EEFB4C2E219EB58C96951346085CCD746B57EA0B6C/account-performance");
        mockHttpServletRequest.setMethod("GET");
        annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, performanceApiController);
    }


   @Test
    public final void testGetPorfolioPerformanceChartData() throws Exception {
        List<ReportDataPointDto> activeReturnChartData = new ArrayList<>(),
                totalPerfChartData = new ArrayList<>(),
                benchmarkChartData = new ArrayList<>();

        accountPerformanceChartDto = new AccountPerformanceChartDto(new AccountPerformanceKey("accountId",
                new DateTime(), new DateTime(), "benchmark"), activeReturnChartData, totalPerfChartData, benchmarkChartData,
                PerformancePeriodType.DAILY, PerformancePeriodType.MONTHLY, new ArrayList<String>());

        when(performanceChartDtoService.find(Mockito.any(AccountPerformanceKey.class), Mockito.any(ServiceErrors.class))).thenReturn(accountPerformanceChartDto);

        KeyedApiResponse<AccountPerformanceKey> response = performanceApiController.getAccountPerformanceChartData("account", "2015-04-01", "2015-08-01", "-1");
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getData());
    }

    @Test
    public final void testGetPorfolioPerformanceChartDataUriSuccess() throws Exception {
        List<ReportDataPointDto> activeReturnChartData = new ArrayList<>(),
                totalPerfChartData = new ArrayList<>(),
                benchmarkChartData = new ArrayList<>();

        accountPerformanceChartDto = new AccountPerformanceChartDto(new AccountPerformanceKey("accountId",
                new DateTime(), new DateTime(), "benchmark"), activeReturnChartData, totalPerfChartData, benchmarkChartData,
                PerformancePeriodType.DAILY, PerformancePeriodType.MONTHLY, new ArrayList<String>());

        when(performanceChartDtoService.find(Mockito.any(AccountPerformanceKey.class), Mockito.any(ServiceErrors.class))).thenReturn(accountPerformanceChartDto);

        mockHttpServletRequest.setParameter(UriMappingConstants.START_DATE_PARAMETER_MAPPING, "2015-03-11");
        mockHttpServletRequest.setParameter(UriMappingConstants.END_DATE_PARAMETER_MAPPING, "2015-03-12");
        mockHttpServletRequest.setParameter("benchmark", "-1");

        mockHttpServletRequest
                .setRequestURI("/secure/api/v1_0/accounts/C77D12EEFB4C2E219EB58C96951346085CCD746B57EA0B6C/performance-chart");
        mockHttpServletRequest.setMethod("GET");
        annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, performanceApiController);
    }

    @Test
    public final void testGetPorfolioPerformanceChartDataUriFailure() throws Exception {
        List<ReportDataPointDto> activeReturnChartData = new ArrayList<>(),
                totalPerfChartData = new ArrayList<>(),
                benchmarkChartData = new ArrayList<>();

        accountPerformanceChartDto = new AccountPerformanceChartDto(new AccountPerformanceKey("accountId",
                new DateTime(), new DateTime(), "benchmark"), activeReturnChartData, totalPerfChartData, benchmarkChartData,
                PerformancePeriodType.DAILY, PerformancePeriodType.MONTHLY, new ArrayList<String>());

        when(performanceChartDtoService.find(Mockito.any(AccountPerformanceKey.class), Mockito.any(ServiceErrors.class))).thenReturn(accountPerformanceChartDto);

        mockHttpServletRequest.setParameter(UriMappingConstants.START_DATE_PARAMETER_MAPPING, "2015-03-11");
        mockHttpServletRequest.setParameter(UriMappingConstants.END_DATE_PARAMETER_MAPPING, "2015-03-12");

        mockHttpServletRequest
                .setRequestURI("/secure/api/v1_0/accounts/C77D12EEFB4C2E219EB58C96951346085CCD746B57EA0B6C/performance-chart");
        mockHttpServletRequest.setMethod("GET");
        try {
            annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, performanceApiController);
        } catch (BadRequestException  e) {
            Assert.assertNotNull(e);
            Assert.assertEquals(e.getMessage(),"Input parameter benchmarkId must be provided");
        }
    }

}
