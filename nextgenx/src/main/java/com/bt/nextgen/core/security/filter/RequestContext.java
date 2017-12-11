package com.bt.nextgen.core.security.filter;

import static com.bt.nextgen.core.util.SETTINGS.SECURITY_UNAUTHED_USERNAME;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_USERNAME_PARAM;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

/**
 * Handy context to wrap the current request so we can simulate saml
 */
class RequestContext
{
	RequestContext(HttpServletRequest request)
	{
		this.request = request;
		this.username = getUsername(request);
		usernameParts = parseUsername(username);
	}

	final HttpServletRequest request;
	final String[] usernameParts;
	final String username;

	private String getUsername(HttpServletRequest request)
	{
		if(request.getParameter(SECURITY_USERNAME_PARAM.value()) != null)
		{
			return request.getParameter(SECURITY_USERNAME_PARAM.value());
		}
		else if(SecurityContextHolder.getContext() != null
			&& SecurityContextHolder.getContext().getAuthentication() != null)
		{
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			return user.getUsername();
		}
		else
		{
			return SECURITY_UNAUTHED_USERNAME.value();
		}
	}

	private String[] parseUsername(String username)
	{
		String[] usernameParts = username.split("_");
		switch (usernameParts.length)
		{
			case 1:
				// assume username = role
				usernameParts = new String[] {usernameParts[0], usernameParts[0]};
			case 2:
				// assume username = avaloq id
				usernameParts = new String[] {usernameParts[0], usernameParts[1], usernameParts[0]};
		}
		return usernameParts;
	}

	public boolean currentlyAuthenticating()
	{
		return request.getParameter(SECURITY_USERNAME_PARAM.value()) != null;
	}

	public String getRole()
	{
		return usernameParts[1];
	}
}
