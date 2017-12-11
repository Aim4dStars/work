package com.bt.nextgen.core.security.csrf;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * This filter implements CSRF protection via the double submit pattern.
 * <p>
 * <a href="http://dwgps0026/twiki/bin/view/NextGen/CoreSinglePageAppCSRFProtection">CoreSinglePageAppCSRFProtection</a>
 * and <a href="http://appsandsecurity.blogspot.com.au/2012/01/stateless-csrf-protection.html">blog post</a>
 *</p>
 *
 */
public class CSRFTokenFilter extends OncePerRequestFilter
{
    private static final Logger logger = LoggerFactory.getLogger(CSRFTokenFilter.class);
    private static final String CSRF_TOKEN = "securityToken";
    private static final String CSRF_TOKEN_ERROR = "securityTokenError";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException
    {
        final Pattern allowed = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");
        if (!allowed.matcher(request.getMethod()).matches())
        {
            logger.info("Starting CSRF token verification");
            final String csrfToken = request.getHeader(CSRF_TOKEN);
            final Cookie[] cookies = request.getCookies();
            String csrfCookie = null;

            if (cookies != null)
            {
                for (Cookie cookie : cookies)
                {
                    if (cookie.getName().equals(CSRF_TOKEN))
                    {
                        csrfCookie = cookie.getValue();
                    }
                }
            }

            if (csrfToken == null || !csrfToken.equals(csrfCookie))
            {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setHeader(CSRF_TOKEN_ERROR, "Missing or non-matching CSRF token");
                logger.error("Missing or non-matching CSRF token for request: {}", request.getServletPath());
                return;
            }
            logger.info("CSRF token verification completed successfully");
        }
        filterChain.doFilter(request, response);

        // clear the cookie for extra security
        Cookie ck = new Cookie(CSRF_TOKEN, "");
        ck.setHttpOnly(true);
        ck.setSecure(true);
        ck.setPath("/");
        ck.setMaxAge(0);
        response.addCookie(ck);
    }
}