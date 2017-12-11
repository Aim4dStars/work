package com.bt.nextgen.core.web.interceptor;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.mock.MockHttpSession;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class CrossSiteReqForgeryFilterTest
{
	private static final Logger logger = LoggerFactory.getLogger(CrossSiteReqForgeryFilterTest.class);
	@Test
	public void test_TokenMissingInRequest_Forbidden() throws Exception
	{
		logger.info("test_TokenMissingInRequest_Forbidden");
		MockHttpServletRequest request = mock(MockHttpServletRequest.class);
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpSession session = mock(MockHttpSession.class);
		FilterChain filterChain = mock(FilterChain.class);

		String token = UUID.randomUUID().toString();

		when(request.getMethod()).thenReturn("POST");
		when(request.getSession()).thenReturn(session);
		when(session.getAttribute(CrossSiteReqForgeryFilter.CSRF_TOKEN)).thenReturn(token);

		CrossSiteReqForgeryFilter filter = new CrossSiteReqForgeryFilter();
		filter.doFilter(request,response,filterChain);

		verify(request,times(2)).getSession();
		verify(session,times(2)).getAttribute(CrossSiteReqForgeryFilter.CSRF_TOKEN);

		assertThat(HttpServletResponse.SC_FORBIDDEN,equalTo(response.getStatus()));
	}

	@Test
	public void test_sameTokenInRequestAndInSessionReturnOk() throws Exception
	{
		logger.info("test_sameTokenInRequestAndInSession");
		MockHttpServletRequest request = mock(MockHttpServletRequest.class);
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpSession session = mock(MockHttpSession.class);
		FilterChain filterChain = mock(FilterChain.class);

		String token = UUID.randomUUID().toString();

		when(request.getParameter(CrossSiteReqForgeryFilter.CSRF_TOKEN)).thenReturn(token);
		when(request.getMethod()).thenReturn("POST");
		when(request.getSession()).thenReturn(session);
		when(session.getAttribute(CrossSiteReqForgeryFilter.CSRF_TOKEN)).thenReturn(token);

		CrossSiteReqForgeryFilter filter = new CrossSiteReqForgeryFilter();
		filter.doFilter(request, response, filterChain);

		verify(request,times(1)).getMethod();
		verify(request,times(2)).getSession();
		verify(request,times(1)).getParameter(CrossSiteReqForgeryFilter.CSRF_TOKEN);
		verify(session,times(2)).getAttribute(CrossSiteReqForgeryFilter.CSRF_TOKEN);

		assertThat(HttpServletResponse.SC_OK,equalTo(response.getStatus()));
	}

	@Test
	public void test_TokenIsAddedInRequest() throws Exception
	{
		logger.debug("test_TokenIsAddedInRequest");
		MockHttpServletRequest request = mock(MockHttpServletRequest.class);
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpSession session = mock(MockHttpSession.class);
		FilterChain filterChain = mock(FilterChain.class);

		String token = UUID.randomUUID().toString();
		when(request.getMethod()).thenReturn("GET");
		when(request.getSession()).thenReturn(session);
		when(session.getAttribute(CrossSiteReqForgeryFilter.CSRF_TOKEN)).thenReturn(token);
		doNothing().when(request).setAttribute(CrossSiteReqForgeryFilter.CSRF_TOKEN,token);

		CrossSiteReqForgeryFilter filter = new CrossSiteReqForgeryFilter();
		filter.doFilter(request, response, filterChain);

		verify(request,times(1)).getMethod();
		verify(request,times(1)).getSession();
		verify(request,times(1)).setAttribute(CrossSiteReqForgeryFilter.CSRF_TOKEN,token);
		verify(session,times(1)).getAttribute(CrossSiteReqForgeryFilter.CSRF_TOKEN);

		assertThat(HttpServletResponse.SC_OK,equalTo(response.getStatus()));
	}
}
