/**
 * 
 */
package com.bt.nextgen.api.account.v2.controller;

import com.bt.nextgen.api.account.v2.model.BglDataDto;
import com.bt.nextgen.api.account.v2.model.DateRangeAccountKey;
import com.bt.nextgen.api.account.v2.service.BglDtoService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.service.ServiceErrors;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
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

/**
 * @author L072463
 *
 */
@RunWith(MockitoJUnitRunner.class)
@Ignore("dodgy mock uri method handler doesn't have full spring capabilities")
public class BglApiControllerTest
{

	@InjectMocks
	private BglApiController bglApiController;

	@Mock
	private BglDtoService bglDtoService;

	private MockHttpServletRequest mockHttpServletRequest;
	private MockHttpServletResponse mockHttpServletResponse;

	@Mock
	private static AnnotationMethodHandlerAdapter annotationMethodHandler;

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
	 * Test method for {@link com.bt.nextgen.api.account.v2.controller.BglApiController#getBGLData(java.lang.String, java.lang.String, java.lang.String, javax.servlet.http.HttpServletResponse)}.
	 */
	@Test
	public final void testGetBGLData() throws Exception
	{
		BglDataDto bglDataDto = new BglDataDto(new DateRangeAccountKey("accountId", new DateTime(), new DateTime()), new byte[10]);
		Mockito.when(bglDtoService.find(Mockito.any(DateRangeAccountKey.class), Mockito.any(ServiceErrors.class)))
			.thenReturn(bglDataDto);

		mockHttpServletRequest.setParameter("start-date", "2015-03-11");
		mockHttpServletRequest.setParameter("end-date", "2015-03-12");
		mockHttpServletRequest.setParameter(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, "accountId");

        mockHttpServletRequest.setRequestURI("/secure/api/accounts/v2_0/C77D12EEFB4C2E219EB58C96951346085CCD746B57EA0B6C/bgl");
		mockHttpServletRequest.setMethod("GET");
		annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, bglApiController);
	}
}
