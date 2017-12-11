package com.bt.nextgen.core.web.interceptor;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.RequestMethod;

public class CrossSiteScriptingFilterTest
{
	private static final Logger logger = LoggerFactory.getLogger(CrossSiteScriptingFilterTest.class);

	@Test
	public void test_doFilter_MethodIsCalled() throws Exception
	{
		logger.debug("test_doFilter_MethodIsCalled");
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain  filterChain = mock(FilterChain.class);
		doNothing().doThrow(new RuntimeException()).when(filterChain).doFilter(request, response);
		CrossSiteScriptingFilter crossSiteScriptingFilter =  new CrossSiteScriptingFilter();
		crossSiteScriptingFilter.doFilter(request, response, filterChain);
		verify(filterChain, times(1)).doFilter(request, response);
	}

	@Test
	public void test_ValidInputReturn_OkStatusCode() throws Exception
	{
		Map<String, String> validParams = new HashMap<>();
		validParams.put("date","08 May 2013");
		validParams.put("amount","200.00");
		validParams.put("name","Mohammad Hossain");
		validParams.put("underscore","request_wrapper");
		validParams.put("hyphen","request-wrapper");
		validParams.put("amount_with_dollar_sign","$300,00.00");

		MockHttpServletRequest request= new MockHttpServletRequest();
		request.setServletPath("/secure/page/serviceOps/");
		request.setRequestURI("/ng/secure/page/serviceOps/home/");
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain  filterChain = mock(FilterChain.class);
		CrossSiteScriptingFilter crossSiteScriptingFilter =  new CrossSiteScriptingFilter();

		for(String param: validParams.keySet())
		{
			request.addParameter(param,validParams.get(param));
			crossSiteScriptingFilter.doFilter(request, response, filterChain);
			assertThat(HttpServletResponse.SC_OK,equalTo(response.getStatus()));
		}
	}

	@Test
	public void test_InvalidInputReturn_ForbiddenStatusCode() throws Exception
	{
		Map<String,String> invalidParams = new HashMap<>();
		invalidParams.put("javaScript","<script>alert('You are hacked')</script>");
		invalidParams.put("sqlInjection","select * from User where 1 = 1");
		invalidParams.put("garbage","@#%^&*");

		FilterChain  filterChain = mock(FilterChain.class);
		CrossSiteScriptingFilter crossSiteScriptingFilter =  new CrossSiteScriptingFilter();

		for(String param: invalidParams.keySet())
		{
			MockHttpServletRequest request= new MockHttpServletRequest(RequestMethod.GET.name(), "/secure/api/serviceOps/");
			request.setRequestURI("/ng/secure/page/serviceOps/");
			request.setServletPath("/secure/page/serviceOps/");
			MockHttpServletResponse response = new MockHttpServletResponse();
			request.addParameter(param,invalidParams.get(param));
			crossSiteScriptingFilter.doFilter(request, response, filterChain);
			assertThat(HttpServletResponse.SC_FORBIDDEN,equalTo(response.getStatus()));
		}
	}

	@Test
	public void test_validInputReturnForAdmin_Ok_StatusCode() throws Exception
	{
		Map<String,String> invalidParams = new HashMap<>();
		invalidParams.put("javaScript","<script>alert('You are hacked')</script>");
		invalidParams.put("sqlInjection","select * from User where 1 = 1");
		invalidParams.put("garbage","@#%^&*");

		FilterChain  filterChain = mock(FilterChain.class);
		CrossSiteScriptingFilter crossSiteScriptingFilter =  new CrossSiteScriptingFilter();

		for(String param: invalidParams.keySet())
		{
			MockHttpServletRequest request= new MockHttpServletRequest(RequestMethod.GET.name(), "/secure/api/serviceOps/");
			request.setRequestURI("/ng/secure/page/serviceOps/admin/");
			request.setServletPath("/ng/secure/page/serviceOps/admin/");
			MockHttpServletResponse response = new MockHttpServletResponse();
			request.addParameter(param,invalidParams.get(param));
			crossSiteScriptingFilter.doFilter(request, response, filterChain);
			assertThat(HttpServletResponse.SC_OK,equalTo(response.getStatus()));
		}
	}


	@Test
	public void test_InvalidvalidInputReturnForSearch_ForbiddenStatusCode() throws Exception
	{
		Map<String,String> invalidParams = new HashMap<>();
		invalidParams.put("javaScript","<script>alert('You are hacked')</script>");
		invalidParams.put("sqlInjection","select * from User where 1 = 1");
		invalidParams.put("garbage","@#%^&*");

		FilterChain  filterChain = mock(FilterChain.class);
		CrossSiteScriptingFilter crossSiteScriptingFilter =  new CrossSiteScriptingFilter();

		for(String param: invalidParams.keySet())
		{
			MockHttpServletRequest request= new MockHttpServletRequest(RequestMethod.GET.name(), "/secure/api/serviceOps/");
			request.setRequestURI("/ng/secure/page/serviceOps/home?searchCriteria=201601509&selection=%23jq-intermediariesSearch");
			request.setServletPath("/ng/secure/page/serviceOps/home?searchCriteria=201601509&selection=%23jq-intermediariesSearch");
			MockHttpServletResponse response = new MockHttpServletResponse();
			request.addParameter(param,invalidParams.get(param));
			crossSiteScriptingFilter.doFilter(request, response, filterChain);
			assertThat(HttpServletResponse.SC_FORBIDDEN,equalTo(response.getStatus()));
		}
	}

	@Test
	public void test_InvalidvalidInputReturnForDetail_ForbiddenStatusCode() throws Exception
	{
		Map<String,String> invalidParams = new HashMap<>();
		invalidParams.put("javaScript","<script>alert('You are hacked')</script>");
		invalidParams.put("sqlInjection","select * from User where 1 = 1");
		invalidParams.put("garbage","@#%^&*");

		FilterChain  filterChain = mock(FilterChain.class);
		CrossSiteScriptingFilter crossSiteScriptingFilter =  new CrossSiteScriptingFilter();

		for(String param: invalidParams.keySet())
		{
			MockHttpServletRequest request= new MockHttpServletRequest(RequestMethod.GET.name(), "/secure/api/serviceOps/");
			request.setRequestURI("/ng/secure/page/serviceOps/BC8143924773CE654EFC9CFB01A38ECCDC1432BE7F4C9187/detail");
			request.setServletPath("/ng/secure/page/serviceOps/BC8143924773CE654EFC9CFB01A38ECCDC1432BE7F4C9187/detail");
			MockHttpServletResponse response = new MockHttpServletResponse();
			request.addParameter(param,invalidParams.get(param));
			crossSiteScriptingFilter.doFilter(request, response, filterChain);
			assertThat(HttpServletResponse.SC_FORBIDDEN,equalTo(response.getStatus()));
		}
	}

	@Test
	public void test_ValidDetailInputReturn_OkStatusCode() throws Exception
	{
		Map<String, String> validParams = new HashMap<>();
		validParams.put("date","08 May 2013");
		validParams.put("amount","200.00");
		validParams.put("name","Mohammad Hossain");
		validParams.put("underscore","request_wrapper");
		validParams.put("hyphen","request-wrapper");
		validParams.put("amount_with_dollar_sign","$300,00.00");

		MockHttpServletRequest request= new MockHttpServletRequest();
		request.setServletPath("/ng/secure/page/serviceOps/");
		request.setRequestURI("/ng/secure/page/serviceOps/");
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain  filterChain = mock(FilterChain.class);
		CrossSiteScriptingFilter crossSiteScriptingFilter =  new CrossSiteScriptingFilter();

		for(String param: validParams.keySet())
		{
			request.addParameter(param,validParams.get(param));
			crossSiteScriptingFilter.doFilter(request, response, filterChain);
			assertThat(HttpServletResponse.SC_OK,equalTo(response.getStatus()));
		}
	}





}
