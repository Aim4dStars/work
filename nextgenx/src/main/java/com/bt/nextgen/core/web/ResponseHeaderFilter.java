package com.bt.nextgen.core.web;

import java.io.IOException;
import java.util.EnumSet;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bt.nextgen.util.Environment;

import static com.bt.nextgen.core.util.SETTINGS.SECURITY_HEADER_STRICT_TRANSPORT_SECURITY;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_HEADER_X_FRAME_OPTIONS;
import static com.bt.nextgen.util.Environment.ENVIRONMENT.*;

/**
 * This class takes care of adding any extra headers to responses
 */
public class ResponseHeaderFilter implements Filter
{
    private static final Logger logger = LoggerFactory.getLogger(ResponseHeaderFilter.class);

    private boolean shouldApplyStrictTransportSecurity;
    private String xFrameOptionsValue;
    private String strictTransportSecurityValue;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        shouldApplyStrictTransportSecurity = EnumSet.of(SIT, UAT, SVP, PROD).contains(Environment.environment());
        strictTransportSecurityValue = SECURITY_HEADER_STRICT_TRANSPORT_SECURITY.value();
        if (shouldApplyStrictTransportSecurity)
        {
            logger.info("Enabling strict transport security header with value : {}", strictTransportSecurityValue);
        }
        xFrameOptionsValue = SECURITY_HEADER_X_FRAME_OPTIONS.value();
    }

    @Override
    public void destroy() {
        // no implementation required
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException
	{
		final HttpServletRequest request = (HttpServletRequest)req;
        final HttpServletResponse response = (HttpServletResponse)resp;

        // https://www.owasp.org/index.php/Cross_Frame_Scripting
        applyXFrameOptions(response);
        applyStrictTransportSecurity(response);

        applyHostnameTag(response);

        filterChain.doFilter(request, response);
    }

    private void applyHostnameTag(HttpServletResponse response) {
        response.setHeader("x-source-correlator", Environment.getHostnameHash());
    }

    private void applyStrictTransportSecurity(HttpServletResponse response) {
        if (shouldApplyStrictTransportSecurity)
        {
            response.setHeader("Strict-Transport-Security", strictTransportSecurityValue);
        }
    }

    private void applyXFrameOptions(HttpServletResponse response) {
        response.setHeader("X-FRAME-OPTIONS", xFrameOptionsValue);
    }

}
