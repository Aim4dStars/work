package com.bt.nextgen.core.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;

import com.btfin.panorama.core.security.Roles;

/**
 * This class is used to change the current session inactive timeout
 */
public class AlterSessionTimeoutFilter extends GenericFilterBean
{
    private static final Logger logger = LoggerFactory.getLogger(AlterSessionTimeoutFilter.class);
    private final int newSessionTimeout;

    public AlterSessionTimeoutFilter(int newSessionTimeout) {
        this.newSessionTimeout = newSessionTimeout;
    }

    @Override public void doFilter(ServletRequest request, ServletResponse response,
                                   FilterChain chain) throws IOException, ServletException
    {
        final HttpServletRequest httpRequest = (HttpServletRequest)request;

        if (httpRequest.isUserInRole(Roles.ROLE_ANONYMOUS.name()) ) {
            HttpSession session = httpRequest.getSession();

            if(session.getMaxInactiveInterval() != newSessionTimeout){
                logger.info("Changing session unauthenticated session timeout from {} to {}", session.getMaxInactiveInterval(), newSessionTimeout);
                session.setMaxInactiveInterval(newSessionTimeout);
            }
        }

        chain.doFilter(request, response);
    }
}