package com.bt.nextgen.core.web.interceptor;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.WebUtils;

public class CrossSiteReqForgeryFilter implements Filter
{

	public static final String CSRF_TOKEN = "cssftoken";

	private static final Logger logger = LoggerFactory.getLogger(CrossSiteReqForgeryFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
		throws IOException, ServletException
	{
		HttpServletRequest request = (HttpServletRequest)servletRequest;
		HttpServletResponse response = (HttpServletResponse)servletResponse;
		try
		{
			String token = getTokenFromSession(request.getSession());
			if (request.getMethod().equalsIgnoreCase("GET"))
			{
				request.setAttribute(CSRF_TOKEN, token);
			}
			else
			{
				if (request.getServletPath() != null && request.getServletPath().startsWith("/secure/api/"))
				{
					// TODO remove this check for secure/api when a solution is put in for csrf
				}
				else
				{
					String sessionToken = getTokenFromSession(request.getSession());
					String requestToken = getTokenFromRequest(request);

					if (StringUtils.isBlank(sessionToken) || !sessionToken.equals(requestToken))
					{
						logger.info("Request token: " + requestToken + ", session token:" + sessionToken);
						logger.warn("<===================CSRF THREAT FOUND========================>");
						logger.warn("CROSS SITE REQUEST FORGERY ATTACK FOUND FROM IP:" + request.getRemoteAddr());
						logger.warn("<============================================================>");
						response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bad or missing CSRFToken");
						return;
					}
				}
			}
		}
		catch (Exception e)
		{
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bad or missing CSRFToken");
			return;
		}
		filterChain.doFilter(request, response);
	}

	@Override
	public void destroy()
	{}

	public static String getTokenFromSession(HttpSession session)
	{
		String csrfToken;
		Object mutex = WebUtils.getSessionMutex(session);
		synchronized (mutex)
		{
			csrfToken = (String)session.getAttribute(CSRF_TOKEN);
			if (StringUtils.isBlank(csrfToken))
			{
				csrfToken = UUID.randomUUID().toString();
				session.setAttribute(CSRF_TOKEN, csrfToken);
			}
		}
		return csrfToken;
	}

	public static String getTokenFromRequest(HttpServletRequest request)
	{
		return request.getParameter(CSRF_TOKEN);
	}
}
