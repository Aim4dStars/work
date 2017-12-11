/**
 * 
 */
package com.bt.nextgen.api.account.v1.controller;

import static org.junit.Assert.*;

import com.bt.nextgen.api.account.v1.service.ValuationMovementDtoService;

import com.bt.nextgen.api.account.v1.model.DateRangeAccountKey;
import com.bt.nextgen.api.account.v1.model.DatedAccountKey;
import com.bt.nextgen.api.account.v1.model.ValuationDto;
import com.bt.nextgen.api.account.v1.model.ValuationMovementDto;
import com.bt.nextgen.api.account.v1.model.ValuationSummaryDto;
import com.bt.nextgen.api.account.v1.controller.ValuationMovementApiController;

import java.math.BigDecimal;
import java.util.ArrayList;

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
import com.bt.nextgen.account.api.model.InvestmentValuationDto;
import com.bt.nextgen.api.performance.controller.PerformanceApiController;
import com.bt.nextgen.api.performance.model.PerformanceDto;
import com.bt.nextgen.api.performance.model.PortfolioPerformanceDto;
import com.bt.nextgen.api.performance.service.PerformanceDtoService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.service.ServiceErrors;

/**
 * @author L072463
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ValuationMovementApiControllerTest
{

	@InjectMocks
	ValuationMovementApiController valuationMovementApiController;

	@Mock
	private ValuationMovementDtoService valuationMovementDtoService;
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
	 * Test method for {@link com.bt.nextgen.api.account.v1.controller.ValuationMovementApiController#getValuationMovement(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testGetValuationMovement() throws Exception
	{
		ValuationMovementDto valuationMovementDto = new ValuationMovementDto();
		Mockito.when(valuationMovementDtoService.find(Mockito.any(DateRangeAccountKey.class), Mockito.any(ServiceErrors.class)))
			.thenReturn(valuationMovementDto);

		mockHttpServletRequest.setParameter(UriMappingConstants.START_DATE_PARAMETER_MAPPING, "2015-03-11");
		mockHttpServletRequest.setParameter(UriMappingConstants.END_DATE_PARAMETER_MAPPING, "2015-03-12");

		mockHttpServletRequest.setRequestURI("/secure/api/v1_0/accounts/C77D12EEFB4C2E219EB58C96951346085CCD746B57EA0B6C/movements");
		mockHttpServletRequest.setMethod("GET");
		annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, valuationMovementApiController);
	}

}
