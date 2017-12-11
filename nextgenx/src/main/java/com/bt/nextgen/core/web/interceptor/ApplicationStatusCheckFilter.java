package com.bt.nextgen.core.web.interceptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bt.nextgen.core.IServiceStatus;
import com.bt.nextgen.core.cache.CacheType;
import com.bt.nextgen.core.cache.EhCacheInfo;
import com.bt.nextgen.core.cache.EhCacheInfoImpl;
import com.bt.nextgen.core.util.Properties;
import net.sf.ehcache.Cache;
import net.sf.ehcache.TransactionController;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.bt.nextgen.core.web.model.AjaxResponse;

/**
 * Created by Deepshikha Singh on 13/01/2015.
 * This filter intercepts every request and redirects the client to appropriate page on the basis of cache status 
 * In memory flag indicating cache status is verified before returning the response
 */

public class ApplicationStatusCheckFilter implements Filter{

	private static final Logger logger = LoggerFactory.getLogger(ApplicationStatusCheckFilter.class);

	public static final String HUMAN_URL = "/public/static/page/starting.html";

	private static final Marker REQUEST_TRACKING_MARKER = MarkerFactory.getMarker("REQTRAK");

	static final String SESSION_KEY = ApplicationStatusCheckFilter.class.getCanonicalName()+".reveal";

	private IServiceStatus appStatusService;

	private HttpMessageConverter jsonMessageConverter;

	private Environment environment;

	private EhCacheInfo cacheInfo;

	private List<String> cacheTypes;


	public void doFilter(ServletRequest request, ServletResponse response,
		FilterChain filterChain) throws IOException, ServletException {
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		logger.info(REQUEST_TRACKING_MARKER,"Resource invoked: {}", httpRequest.getRequestURI());
		final String queryString = httpRequest.getQueryString();
		if(queryString != null && queryString.toLowerCase().contains("reveal")){
			logger.info("Reveal present in request, bypass blocking pages...");
			httpRequest.getSession().setAttribute(SESSION_KEY, true);
		}
		if(null != Properties.get("cache.enabled.services")) {
			cacheTypes = Arrays.asList(Properties.get("cache.enabled.services").split(","));
		}

		boolean cachePopulated = appStatusService.checkCacheStatus();
		if(!revealEnabled(httpRequest) && !cachePopulated){
			httpResponse.addHeader("Retry-After", "30");
			if(MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(httpRequest.getContentType())) {
				httpResponse.setStatus(HttpServletResponse.SC_ACCEPTED);
				jsonMessageConverter.write(makeAjaxResponse(httpResponse, httpRequest)
						, MediaType.APPLICATION_JSON
						, new ServletServerHttpResponse(httpResponse));
			}
			else {
				logger.info("Application not available for LOGIN, Returning STARTING page");
				httpResponse.sendRedirect(getRedirectUrl(httpResponse, httpRequest));
			}
			return;
		}
		else{
			logger.debug("Application available for request processing");
			filterChain.doFilter(request, response);
		}
	}

	private boolean revealEnabled(HttpServletRequest request) {
		return (boolean) ObjectUtils.defaultIfNull(request.getSession().getAttribute(SESSION_KEY), false);
	}

	private AjaxResponse makeAjaxResponse(HttpServletResponse httpResponse, HttpServletRequest httpRequest) {
		return new AjaxResponse(true
				, new StatusCheckResponse("NOT_READY"
						, getRedirectUrl(httpResponse, httpRequest)
						, "The server is starting up, please wait..."));
	}

	private String getRedirectUrl(HttpServletResponse httpResponse, HttpServletRequest httpRequest) {
		return httpResponse.encodeRedirectURL(httpRequest.getContextPath()+HUMAN_URL);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		ServletContext servletContext = filterConfig.getServletContext();
		ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(
				servletContext);
		appStatusService = ctx.getBean(IServiceStatus.class);
		jsonMessageConverter = ctx.getBean("jsonMessageConverter", MappingJackson2HttpMessageConverter.class);
		cacheInfo = ctx.getBean("ehCacheInfo", EhCacheInfoImpl.class);
		environment = ctx.getBean(Environment.class);
	}

	@Override
	public void destroy() {}

	void setJsonMessageConverter(HttpMessageConverter jsonMessageConverter) {
		this.jsonMessageConverter = jsonMessageConverter;
	}

}
