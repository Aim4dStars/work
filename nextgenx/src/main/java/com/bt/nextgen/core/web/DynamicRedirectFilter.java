package com.bt.nextgen.core.web;

import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bt.nextgen.core.security.HttpHeadersRequestWrapper;
import com.bt.nextgen.core.util.Properties;

import static com.bt.nextgen.core.util.SETTINGS.*;

public class DynamicRedirectFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(DynamicRedirectFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // no implementation required
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)req;
        HttpHeadersRequestWrapper wrappedRequest = new HttpHeadersRequestWrapper(request);

        addForwardedHostHeader(wrappedRequest);

        HttpServletResponse response = new DynamicRedirectingResponse((HttpServletResponse)resp,
                wrappedRequest.getHeader(SECURITY_HEADER_XFORWARDHOST.value()));
        logger.debug("DynamicRedirectFilter:doFilter:host = {}", wrappedRequest.getHeader(SECURITY_HEADER_XFORWARDHOST.value()));
        filterChain.doFilter(wrappedRequest, response);
	}

    @Override
    public void destroy() {
        // no implementation required
    }


    private void addForwardedHostHeader(HttpHeadersRequestWrapper wrappedRequest) {
        if (wrappedRequest.getHeader(SECURITY_HEADER_XFORWARDHOST.value()) == null) {
            final String host = HTTP_HOST_DEFAULT.value(wrappedRequest.getHeader("Host"));
            logger.debug("DynamicRedirectFilter:addForwardedHostHeader - XFORWARDHOST not set - adding custom header: '{}'", host);
            wrappedRequest.addCustomHeader(SECURITY_HEADER_XFORWARDHOST.value(), host);
        }
    }

    private class DynamicRedirectingResponse extends HttpServletResponseWrapper {

        private String host;

        /**
         * Constructs a response adaptor wrapping the given response.
         *
         * @throws IllegalArgumentException
         *          if the response is null
         */
        public DynamicRedirectingResponse(HttpServletResponse response, String host) {
            super(response);
            this.host = host;
        }

        @Override
        public String encodeRedirectURL(String url) {
            logger.debug("DynamicRedirectFilter:encodeRedirectURL:url - '{}'", url);
            if(url.contains(":") || StringUtils.isEmpty(HTTP_REDIRECT_TEMPLATE.value() )) {
                logger.debug("Given a fully qualified url {} will pass through", url);
                return super.encodeRedirectURL(url);
            }
            else {
                String base = HTTP_REDIRECT_TEMPLATE.value();
                base = MessageFormat.format(base, stripPath(url, host));
                final String result = base + url;
                logger.debug("Encoded redirect url {} -> {}", url, result);
                return result;
            }
        }

        private String stripPath(String suffixUrl, String host) {
            String filteredHost = host;

            if (host.contains("/")) {
                final boolean isLogoutRequest = suffixUrl.equals(Properties.get("security.logout.redirect.success"));

                if (isLogoutRequest) {
                    final String[] parts = host.split("/");
                    filteredHost = parts[0];
                }
            }
            logger.debug("DynamicRedirectFilter:stripPath:result - '{}'", filteredHost);
            return filteredHost;
        }
    }
}
