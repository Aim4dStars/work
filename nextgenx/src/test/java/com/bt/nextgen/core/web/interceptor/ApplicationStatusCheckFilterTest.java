package com.bt.nextgen.core.web.interceptor;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.core.IServiceStatus;
import org.apache.struts.mock.MockHttpSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationStatusCheckFilterTest {
	
	private static final Logger logger = LoggerFactory.getLogger(ApplicationStatusCheckFilterTest.class);
	
	@InjectMocks
	ApplicationStatusCheckFilter applicationFilter;
	
	@Mock
	IServiceStatus appStatusService;

	@Before
	public void setup() {
		applicationFilter.setJsonMessageConverter(getJsonConverter());
	}

	private HttpMessageConverter getJsonConverter() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setObjectMapper(new JsonObjectMapper());
		return converter;
	}

	@Test
	public void test_CachePopulated_Error() throws Exception
	{
		logger.info("test_CachePopulated_Error");
		
		MockHttpServletRequest request = mock(MockHttpServletRequest.class);
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpSession session = mock(MockHttpSession.class);
		FilterChain filterChain = mock(FilterChain.class);
		
		when(request.getSession()).thenReturn(session);
		when(appStatusService.checkCacheStatus()).thenReturn(true);

		applicationFilter.doFilter(request,response,filterChain);

		assertThat(response.getStatus(),equalTo(HttpServletResponse.SC_OK));
	}

	@Test
	public void test_CacheNotPopulated_Error() throws Exception
	{
		logger.info("test_CacheNotPopulated_Error");
		
		MockHttpServletRequest request = mock(MockHttpServletRequest.class);
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpSession session = mock(MockHttpSession.class);
		FilterChain filterChain = mock(FilterChain.class);

		when(request.getRequestURI()).thenReturn("anything.html");
		when(request.getSession()).thenReturn(session);
		when(appStatusService.checkCacheStatus()).thenReturn(false);

		applicationFilter.doFilter(request,response,filterChain);
		assertThat(response.getRedirectedUrl(),equalTo(request.getContextPath()+"/public/static/page/starting.html"));
	}

	@Test
	public void test_CacheNotPopulated_Success() throws Exception
	{
		logger.info("test_CachePopulated_Success");
		
		MockHttpServletRequest request = mock(MockHttpServletRequest.class);
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpSession session = mock(MockHttpSession.class);
		FilterChain filterChain = mock(FilterChain.class);

		when(request.getSession()).thenReturn(session);
		when(appStatusService.checkCacheStatus()).thenReturn(true);

		applicationFilter.doFilter(request,response,filterChain);
		assertThat(response.getStatus(),equalTo(HttpServletResponse.SC_OK));
	}
	
	@Test
	public void test_CachePopulated_Success() throws Exception
	{
		logger.info("test_CachePopulated_Success");
		
		MockHttpServletRequest request = mock(MockHttpServletRequest.class);
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpSession session = mock(MockHttpSession.class);
		FilterChain filterChain = mock(FilterChain.class);

		when(request.getRequestURI()).thenReturn("starting.html");
		when(request.getSession()).thenReturn(session);
		when(appStatusService.checkCacheStatus()).thenReturn(false);

		applicationFilter.doFilter(request,response,filterChain);
		assertThat(response.getStatus(),equalTo(HttpServletResponse.SC_FOUND));
	}
	
	@Test
	public void test_CacheNotPopulated_CheckAgain_Success() throws Exception
	{
		logger.info("test_CachePopulated_Success");
		
		MockHttpServletRequest request = mock(MockHttpServletRequest.class);
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpSession session = mock(MockHttpSession.class);
		FilterChain filterChain = mock(FilterChain.class);

		when(request.getSession()).thenReturn(session);
		when(appStatusService.checkCacheStatus()).thenReturn(true);

		applicationFilter.doFilter(request,response,filterChain);
		assertThat(response.getStatus(),equalTo(HttpServletResponse.SC_OK));
	}

	@Test
	public void test_RevealOn() throws Exception
	{
		MockHttpServletRequest request = mock(MockHttpServletRequest.class);
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpSession session = mock(MockHttpSession.class);
		FilterChain filterChain = mock(FilterChain.class);

		when(request.getSession()).thenReturn(session);
		when(session.getAttribute(ApplicationStatusCheckFilter.SESSION_KEY)).thenReturn(true);
		when(appStatusService.checkCacheStatus()).thenReturn(false);

		applicationFilter.doFilter(request,response,filterChain);
		verify(filterChain, times(1)).doFilter(Matchers.<ServletRequest>any(), Matchers.<ServletResponse>any());
	}

	@Test
	public void test_RevealOff() throws Exception
	{
		MockHttpServletRequest request = mock(MockHttpServletRequest.class);
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpSession session = mock(MockHttpSession.class);
		FilterChain filterChain = mock(FilterChain.class);

		when(request.getSession()).thenReturn(session);
		when(session.getAttribute(ApplicationStatusCheckFilter.SESSION_KEY)).thenReturn(false);
		when(appStatusService.checkCacheStatus()).thenReturn(false);

		applicationFilter.doFilter(request, response, filterChain);
		verify(session, never()).setAttribute(anyString(), any());
		verify(filterChain, never()).doFilter(Matchers.<ServletRequest>any(), Matchers.<ServletResponse>any());
	}

	@Test
	public void test_RevealToggleOn() throws Exception
	{
		MockHttpServletRequest request = mock(MockHttpServletRequest.class);
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpSession session = mock(MockHttpSession.class);
		FilterChain filterChain = mock(FilterChain.class);

		when(request.getSession()).thenReturn(session);
		when(request.getQueryString()).thenReturn("reveal");
		applicationFilter.doFilter(request, response, filterChain);

		verify(session, times(1)).setAttribute(anyString(), eq(true));
	}


	@Test
	public void test_jsonRequestJsonResponse() throws Exception
	{
		MockHttpServletRequest request = mock(MockHttpServletRequest.class);
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpSession session = mock(MockHttpSession.class);
		FilterChain filterChain = mock(FilterChain.class);

		when(request.getSession()).thenReturn(session);
		when(request.getContentType()).thenReturn("application/json");
		applicationFilter.doFilter(request, response, filterChain);

		assertThat(response.getContentType(), equalTo("application/json"));
		assertThat(response.getStatus(), equalTo(HttpServletResponse.SC_ACCEPTED));
	}

}
