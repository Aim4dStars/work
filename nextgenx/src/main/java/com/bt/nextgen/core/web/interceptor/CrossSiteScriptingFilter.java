package com.bt.nextgen.core.web.interceptor;

import java.io.IOException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class representing a Cross Site Request Input Validator.
 * All the request will go through this filter to ensure it does not contain any vulnerable character
 * It will allow only white list characters{A-Za-z0-9$,._- } otherwise it will return HttpServletResponse.SC_FORBIDDEN (403)
 *
 * @author Mohammad Hossain
 * @see <a href="http://dwgps0026/twiki/bin/view/NextGen/TechnicalCrossSiteScripting">Cross Site Scripting WikiPage Link</a>
 */

public class CrossSiteScriptingFilter implements Filter
{
	private static final Logger logger = LoggerFactory.getLogger(CrossSiteScriptingFilter.class);
	public final Pattern pattern;

	/**
	 * Use the pattern passed.
	 *
	 * @param pattern a valid Pattern.
	 */
	public CrossSiteScriptingFilter(String pattern)
	{
		this.pattern = Pattern.compile(pattern);
	}

	/**
	 * Use default pattern "^[A-Za-z0-9\\$,. _-]*"
	 */
	public CrossSiteScriptingFilter()
	{
		this.pattern = Pattern.compile("^[A-Za-z0-9\\$,. _-]*");
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		logger.debug("init() method");
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException
	{
		logger.debug("In doFilter CrossScriptingFilter  ...............");

		HttpServletRequest request = (HttpServletRequest)servletRequest;
		HttpServletResponse response = (HttpServletResponse)servletResponse;
		logger.debug("In doFilter CrossScriptingFilter  servletPath..............."+ request.getServletPath() );
		if (request.getServletPath() != null
				&& (request.getRequestURI().startsWith("/secure/api/")  || request.getRequestURI().startsWith("/public/api/")))
		{
			// ignore api requests
		}
		else if(request.getRequestURI() != null && (request.getRequestURI().startsWith("/ng/secure/page/serviceOps/") && !request.getRequestURI().startsWith("/ng/secure/page/serviceOps/admin/")))
		{
			logger.info("Inside else if condition");
			Enumeration <String> enumeration = request.getParameterNames();
			if (enumeration != null)
			{
				while (enumeration.hasMoreElements())
				{
					String paramName = enumeration.nextElement();
					String paramValue = request.getParameter(paramName);
			 		logger.info("Inside While condition" +paramName +paramName );
					//Ignoring the baseUrl parameter for the time being as UI needs relying on baseUrl ( sessionScope
					if (!StringUtils.isBlank(paramName) && !paramName.equalsIgnoreCase("baseUrl"))
					{
						if (!validateInput(paramName, paramValue))
						{
							logger.info("Inside failed condition condition" +paramName +paramName );
							response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid input data");
							return;
						}
					}
				}
			}
		}
		logger.debug("Out doFilter CrossScriptingFilter ...............");
		filterChain.doFilter(request, response);
	}

	@Override
	public void destroy()
	{
		logger.debug("destroy()");
	}

	public boolean validateInput(String paramName, String paramValue)
	{
		Matcher matcher = pattern.matcher(paramValue);
		if (!matcher.matches())
		{
			logger.info("<============================XSS THREAT FOUND===================================>");
			logger.warn("XSS threat (invalid character) found in param '" + paramName + "' =[" + paramValue + "]");
			logger.info("<===============================================================================>");
			return false;
		}
		logger.info("No XSS  character found in param '" + paramName + "' =[" + paramValue + "]");
		return true;
	}
}
