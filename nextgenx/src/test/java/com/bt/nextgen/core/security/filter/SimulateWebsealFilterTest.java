package com.bt.nextgen.core.security.filter;

import static com.bt.nextgen.core.util.SETTINGS.SECURITY_HEADER_USERNAME;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_HEADER_XFORWARDHOST;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.anyString;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bt.nextgen.core.security.HttpHeadersRequestWrapper;

public class SimulateWebsealFilterTest
{
	SimulateWebsealFilter simulateWebsealFilter = new SimulateWebsealFilter();

	@Before
	public void setup() {
		SecurityContextHolder.getContext().setAuthentication(null);
	}

	@Test
	public void testDoFilter_samlMissing() throws Exception
	{
		FilterChain mockChain = mock(FilterChain.class);
		HttpServletRequest mockReq = mock(HttpServletRequest.class);
		ServletResponse mockResp = mock(ServletResponse.class);
		ArgumentCaptor<HttpHeadersRequestWrapper> chainRequest = ArgumentCaptor.forClass(
			HttpHeadersRequestWrapper.class);
		ArgumentCaptor<ServletResponse> chainResponse = ArgumentCaptor.forClass(ServletResponse.class);

		simulateWebsealFilter.doFilter(mockReq, mockResp, mockChain);

		//todo have to do more testing here

		verify(mockChain).doFilter(chainRequest.capture(), chainResponse.capture());
		assertThat(chainRequest.getValue(), Is.isA(HttpHeadersRequestWrapper.class));

	}

	@Test
	public void testDoFilter_esi2SamlPresent() throws Exception
	{
		FilterChain mockChain = mock(FilterChain.class);
		HttpServletRequest mockReq = mock(HttpServletRequest.class);
		ServletResponse mockResp = mock(ServletResponse.class);

		when(mockReq.getHeader("esi2token")).thenReturn("dummy");
		when(mockReq.getHeader(SECURITY_HEADER_USERNAME.value())).thenReturn("username");
        when(mockReq.getHeader(SECURITY_HEADER_XFORWARDHOST.value())).thenReturn("dummy");

		simulateWebsealFilter.doFilter(mockReq, mockResp, mockChain);

		// this verifies that no 'wrapping' has occured
		verify(mockChain).doFilter(mockReq, mockResp);

	}

	@Test
	public void testDoFilter_headersPresent_noMocks() throws Exception
	{
		FilterChain mockChain = mock(FilterChain.class);
		HttpServletRequest mockReq = mock(HttpServletRequest.class);
		ServletResponse mockResp = mock(ServletResponse.class);

		when(mockReq.getHeader("wbctoken")).thenReturn("dummy");
		when(mockReq.getHeader(SECURITY_HEADER_USERNAME.value())).thenReturn("username");
        when(mockReq.getHeader(SECURITY_HEADER_XFORWARDHOST.value())).thenReturn("dummy");

		simulateWebsealFilter.doFilter(mockReq, mockResp, mockChain);

		// this verifies that no 'wrapping' has occured
		verify(mockChain).doFilter(mockReq, mockResp);

	}

	@Test
	public void testDoFilter_headersPresent_service() throws Exception
	{
		FilterChain mockChain = mock(FilterChain.class);
		HttpServletRequest mockReq = mock(HttpServletRequest.class);
		ServletResponse mockResp = mock(ServletResponse.class);
		ArgumentCaptor<HttpHeadersRequestWrapper> chainRequest = ArgumentCaptor.forClass(
				HttpHeadersRequestWrapper.class);
		ArgumentCaptor<ServletResponse> chainResponse = ArgumentCaptor.forClass(ServletResponse.class);
		
		when(mockReq.getParameter(anyString())).thenReturn("service");
		when(mockReq.getHeader("wbctoken")).thenReturn("dummy");
		when(mockReq.getHeader("esi2token")).thenReturn("dummy");
		when(mockReq.getHeader(SECURITY_HEADER_USERNAME.value())).thenReturn("username");
        when(mockReq.getHeader(SECURITY_HEADER_XFORWARDHOST.value())).thenReturn("dummy");

		simulateWebsealFilter.doFilter(mockReq, mockResp, mockChain);

		// this verifies that no 'wrapping' has occured
		verify(mockChain).doFilter(chainRequest.capture(), chainResponse.capture());
		assertThat(chainRequest.getValue(), Is.isA(HttpHeadersRequestWrapper.class));

	}
	
	@Test
	public void testDoFilter_headersPresent_default() throws Exception
	{
		FilterChain mockChain = mock(FilterChain.class);
		HttpServletRequest mockReq = mock(HttpServletRequest.class);
		ServletResponse mockResp = mock(ServletResponse.class);
		ArgumentCaptor<HttpHeadersRequestWrapper> chainRequest = ArgumentCaptor.forClass(
				HttpHeadersRequestWrapper.class);
		ArgumentCaptor<ServletResponse> chainResponse = ArgumentCaptor.forClass(ServletResponse.class);
		
		when(mockReq.getParameter(anyString())).thenReturn("admin");
		when(mockReq.getHeader("wbctoken")).thenReturn("dummy");
		when(mockReq.getHeader("esi2token")).thenReturn("dummy");
		when(mockReq.getHeader(SECURITY_HEADER_USERNAME.value())).thenReturn("username");
        when(mockReq.getHeader(SECURITY_HEADER_XFORWARDHOST.value())).thenReturn("dummy");

		simulateWebsealFilter.doFilter(mockReq, mockResp, mockChain);

		// this verifies that no 'wrapping' has occured
		verify(mockChain).doFilter(chainRequest.capture(), chainResponse.capture());
		assertThat(chainRequest.getValue(), Is.isA(HttpHeadersRequestWrapper.class));

	}

	@Test
	public void testDoFilter_headersPresent() throws Exception
	{
		FilterChain mockChain = mock(FilterChain.class);
		HttpServletRequest mockReq = mock(HttpServletRequest.class);
		ServletResponse mockResp = mock(ServletResponse.class);
		ArgumentCaptor<HttpHeadersRequestWrapper> chainRequest = ArgumentCaptor.forClass(
				HttpHeadersRequestWrapper.class);
		ArgumentCaptor<ServletResponse> chainResponse = ArgumentCaptor.forClass(ServletResponse.class);
		
		when(mockReq.getHeader("wbctoken")).thenReturn("dummy");
		when(mockReq.getHeader("esi2token")).thenReturn("dummy");
		when(mockReq.getHeader(SECURITY_HEADER_USERNAME.value())).thenReturn("username");
        when(mockReq.getHeader(SECURITY_HEADER_XFORWARDHOST.value())).thenReturn("dummy");

		simulateWebsealFilter.doFilter(mockReq, mockResp, mockChain);

		// this verifies that no 'wrapping' has occured
		verify(mockChain).doFilter(chainRequest.capture(), chainResponse.capture());
		assertThat(chainRequest.getValue(), Is.isA(HttpHeadersRequestWrapper.class));

	}
}
