package com.bt.nextgen.core.web;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.RequestMatcher;

public class PublicRequestMatcher implements RequestMatcher
{
	private static final String publicPattern = "/[public]+/*([^^]+)";
	private static final String logoutPattern = "/[public]+/([doLogout]+)+/*([^^]+)";

	@Override
	public boolean matches(HttpServletRequest request)
	{
		String url = request.getServletPath();

		if (Pattern.matches(publicPattern, url))
		{
			if (Pattern.matches(logoutPattern, url))
			{
				return false;
			}
			return true;
		}
		return false;
	}
}
