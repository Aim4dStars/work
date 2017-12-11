package com.bt.nextgen.core.security.filter;

import static com.bt.nextgen.core.util.SETTINGS.SAML_HEADER;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_UNAUTHED_USERNAME;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

import com.btfin.panorama.core.security.saml.SamlToken;

/**
 * Webseal will send through 'unauthenticated' for users that haven't yet authenticated.
 */
public class UnauthenticatedSamlFilter extends RequestHeaderAuthenticationFilter
{
	private static final Logger logger = LoggerFactory.getLogger(UnauthenticatedSamlFilter.class);

	@Override public void doFilter(ServletRequest request, ServletResponse response,
		FilterChain chain) throws IOException, ServletException
	{
		SamlToken samlToken = new SamlToken(((HttpServletRequest)request).getHeader(SAML_HEADER.value()));
		if(SECURITY_UNAUTHED_USERNAME.value().equalsIgnoreCase(samlToken.getGcmId()))
		{
			logger.debug("Seen unauthenticated request, not authenticating user");
			chain.doFilter(request, response);
		}
		else
		{
			super.doFilter(request, response, chain);
		}
	}
}
