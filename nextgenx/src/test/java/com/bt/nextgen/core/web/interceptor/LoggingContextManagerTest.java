package com.bt.nextgen.core.web.interceptor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import java.net.MalformedURLException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class LoggingContextManagerTest {

    @InjectMocks
    LoggingContextManager loggingContextManager;

    @Mock
    HttpServletRequest httpServletRequest;

    @Mock
    ServletResponse servletResponse;



    @Test
    public void testDoFilter() throws Exception {

        FilterChain mockChain = mock(FilterChain.class);

        new LoggingContextManager().doFilter(mock(ServletRequest.class), mock(ServletResponse.class), mockChain);

        Mockito.verify(mockChain).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }

    @Test
    public void testDoFilterHttpRequest() throws Exception {

        FilterChain mockChain = mock(FilterChain.class);

        new LoggingContextManager().doFilter(mock(HttpServletRequest.class), mock(ServletResponse.class), mockChain);

        Mockito.verify(mockChain).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }

    @Test
    public void testDoFilterHttpRequestNegativeScenarioShowsNoImpact() throws Exception {

        FilterChain mockChain = mock(FilterChain.class);
        Mockito.when(httpServletRequest.getRequestURI()).thenThrow(new RuntimeException());
        loggingContextManager.doFilter(httpServletRequest, servletResponse, mockChain);

        Mockito.verify(mockChain).doFilter(httpServletRequest, servletResponse);
    }

    @Test
    public void testRequestDestroyed() throws Exception {

    }

    @Test
    public void testRequestInitialized() throws Exception {

    }
}