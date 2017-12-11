package com.bt.nextgen.core.web;

import org.junit.Test;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.bt.nextgen.core.util.SETTINGS.SECURITY_HEADER_XFORWARDHOST;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: m034259
 * Date: 26/09/13
 * Time: 9:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class DynamicRedirectFilterTest {
    @Test
    public void testDoFilter_fullUrlNotChanged() throws Exception {
        final String fullUrl = "http://full/url";

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        when(mockResponse.encodeRedirectURL(anyString())).thenReturn(fullUrl);

        MockFilterChain mockChain = new MockFilterChain(fullUrl);

        new DynamicRedirectFilter().doFilter(mockRequest, mockResponse, mockChain);

        assertThat(mockChain.answer, is(fullUrl));
    }

    @Test
    public void testDoFilter_partialChanged() throws Exception {
        final String url = "/url";

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        when(mockRequest.getHeader(SECURITY_HEADER_XFORWARDHOST.value())).thenReturn("hostname");

        MockFilterChain mockChain = new MockFilterChain(url);
        mockChain.doFilter(mockRequest, mockResponse);

        new DynamicRedirectFilter().doFilter(mockRequest, mockResponse, mockChain);

        assertThat(mockChain.answer, is("http://hostname"+url));
    }

    @Test
    public void testDoFilter_logoutSSO() throws Exception {
        final String url = "/pkmslogout";

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        when(mockRequest.getHeader(SECURITY_HEADER_XFORWARDHOST.value())).thenReturn("hostname/ng_dev2");

        MockFilterChain mockChain = new MockFilterChain(url);
        mockChain.doFilter(mockRequest, mockResponse);

        new DynamicRedirectFilter().doFilter(mockRequest, mockResponse, mockChain);

        assertThat(mockChain.answer, is("http://hostname"+url));
    }

    @Test
    public void testDoFilter_otherSSO() throws Exception {
        final String url = "/not_logout_fake_url";

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        when(mockRequest.getHeader(SECURITY_HEADER_XFORWARDHOST.value())).thenReturn("hostname/ng_dev2");

        MockFilterChain mockChain = new MockFilterChain(url);
        mockChain.doFilter(mockRequest, mockResponse);

        new DynamicRedirectFilter().doFilter(mockRequest, mockResponse, mockChain);

        assertThat(mockChain.answer, is("http://hostname/ng_dev2"+url));
    }

    private class MockFilterChain implements FilterChain
    {
        private final String redirectUrl;
        String answer;

        private MockFilterChain(String redirectUrl) {
            this.redirectUrl = redirectUrl;
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            answer = ((HttpServletResponse)response).encodeRedirectURL(redirectUrl);
        }
    }
}
