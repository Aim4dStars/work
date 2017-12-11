package com.bt.nextgen.core.web.interceptor;

import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.bt.nextgen.core.log.performance.Settings.PERF;
import static com.bt.nextgen.core.util.SETTINGS.LOGGING_HEADER_PREFIX;

public class LoggingContextManager extends GenericFilterBean implements ServletRequestListener {
    private static final Logger logger = LoggerFactory.getLogger(LoggingContextManager.class);
    private static final String CLASS_NAME = LoggingContextManager.class.getCanonicalName();

    private static final String CONTAINER_SESSION = "container.sessionid";
    private static final String ACTING_USER = "actingUser";
    private static final String CONTAINER_REQUEST = "container.requestid";
    private static final String REQUEST_URL = "request.url";
    private static final String REQUEST_IP = "request.ip";

    private static final FastDateFormat dateFormat = FastDateFormat.getInstance("yyyyMMdd HH:mm:ss.SSS");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        Date startTime = new Date();
        String url=null;
        try {
            // TODO we need to populate the emulated user info
            // MDC.put(ACTING_USER, getBankReferenceId());
            try
            {
                    url = ((HttpServletRequest)request).getRequestURI();
                    logger.info(PERF,"API : {}",url);
            }
            catch(Exception e)
            {
                logger.info("Error Getting API information {}",e);
            }
            chain.doFilter(request, response);
        } finally {
            MDC.remove(ACTING_USER);
            long executeTime = System.currentTimeMillis() - startTime.getTime();
            logger.info(PERF, "API ,{},doFilter,{},{},- {} ms", CLASS_NAME, dateFormat.format(startTime),url, executeTime);
        }

    }

    private String getGcmId() {
        try {
            return String.valueOf(String.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
        } catch (NullPointerException e) {
            return "UNKNOWN";
        }
    }

    private void removeHeaders(List<String> headers) {
        for (String header : headers) {
            MDC.remove(LOGGING_HEADER_PREFIX.value() + header);
        }
    }

    private void addHttpHeaders(HttpServletRequest httpRequest, List<String> headers) {
        for (String header : headers) {
            MDC.put(LOGGING_HEADER_PREFIX.value() + header, httpRequest.getHeader(header));
        }
    }

    @Override
    public void requestDestroyed(ServletRequestEvent event) {
        HttpServletRequest httpRequest = (HttpServletRequest) event.getServletRequest();
        final List<String> headers = Collections.<String> list(httpRequest.getHeaderNames());

        MDC.remove(CONTAINER_SESSION);
        MDC.remove(CONTAINER_REQUEST);
        MDC.remove(REQUEST_URL);
        MDC.remove(ACTING_USER);

        removeHeaders(headers);
    }

    @Override
    public void requestInitialized(ServletRequestEvent event) {
        HttpServletRequest httpRequest = (HttpServletRequest) event.getServletRequest();
        // add headers
        final List<String> headers = Collections.<String> list(httpRequest.getHeaderNames());

        AtomicInteger requestId = (AtomicInteger) httpRequest.getSession().getAttribute(CONTAINER_REQUEST);
        if (requestId == null) {
            requestId = new AtomicInteger(0);
            httpRequest.getSession().setAttribute(CONTAINER_REQUEST, requestId);
        }

        addHttpHeaders(httpRequest, headers);

        // add local session info
        MDC.put(CONTAINER_SESSION, httpRequest.getSession().getId());

        // add request info
        StringBuffer requestURL = httpRequest.getRequestURL();
        if (httpRequest.getQueryString() != null) {
            requestURL.append("?").append(httpRequest.getQueryString());
        }
        String ipAddress = httpRequest.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = httpRequest.getRemoteAddr();
        }

        MDC.put(REQUEST_URL, requestURL.toString());
        MDC.put(REQUEST_IP, ipAddress);
        MDC.put(CONTAINER_REQUEST, String.valueOf(requestId.getAndIncrement()));
    }
}
