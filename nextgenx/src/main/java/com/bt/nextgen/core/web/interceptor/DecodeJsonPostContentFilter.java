package com.bt.nextgen.core.web.interceptor;

import com.bt.nextgen.core.security.HttpContentRequestWrapper;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class DecodeJsonPostContentFilter extends GenericFilterBean {

    private static final String UTF_8 = StandardCharsets.UTF_8.toString();
    private static final String POST = RequestMethod.POST.toString();
    private static final String CLIENT_INFO_URL_V1 = "log/v1_0/client-info";

    /**
     * Filters the POST requests with Content-type : application/json
     *
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final String contentType = request.getContentType();
        // Skipping client-info from this filter
        if (!request.getRequestURI().contains(CLIENT_INFO_URL_V1) &&
                POST.equalsIgnoreCase(request.getMethod()) &&
                contentType != null && contentType.startsWith(APPLICATION_JSON_VALUE)) {
            final String requestContent = IOUtils.toString(request.getInputStream(), UTF_8);
            final String decodedContent = URLDecoder.decode(requestContent, UTF_8);
            final HttpContentRequestWrapper wrapper = new HttpContentRequestWrapper(request, decodedContent);
            filterChain.doFilter(wrapper, servletResponse);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}
