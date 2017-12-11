/**
 * 
 */
package com.bt.nextgen.api.performance.controller;

import java.util.ArrayList;
import java.util.List;

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

import com.bt.nextgen.api.performance.model.BenchmarkDto;
import com.bt.nextgen.api.performance.service.BenchmarkDtoService;
import com.bt.nextgen.service.ServiceErrors;

/**
 * @author L072463

 *
 */
@RunWith(MockitoJUnitRunner.class)
public class BenchmarkApiControllerTest
{
	@InjectMocks
	BenchmarkApiController benchmarkApiController;

	@Mock
	private BenchmarkDtoService benchmarkService;

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
	 * Test method for {@link com.bt.nextgen.api.performance.controller.BenchmarkApiController#getBenchmarks()}.
	 */
	@Test
	public final void testGetBenchmarks() throws Exception
	{
		List <BenchmarkDto> benchmarkDtos = new ArrayList <BenchmarkDto>();
		Mockito.when(benchmarkService.findAll(Mockito.any(ServiceErrors.class))).thenReturn(benchmarkDtos);

		mockHttpServletRequest.setRequestURI("/secure/api/v1_0/benchmarks");
		mockHttpServletRequest.setMethod("GET");
		annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, benchmarkApiController);
	}

}
