package com.bt.nextgen.core.web.interceptor;

import com.bt.nextgen.core.security.HttpContentRequestWrapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.FilterChain;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DecodeJsonPostContentFilterTest {

    @InjectMocks
    private DecodeJsonPostContentFilter decodeJsonPostContentFilter;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Before
    public void setup(){
        when(request.getRequestURI()).thenReturn(StringUtils.EMPTY);
    }

    @Test
    public void doFilterShouldNotFilterRequestOtherThanPost() throws Exception {
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());

        ArgumentCaptor<HttpServletRequest> captor = ArgumentCaptor.forClass(HttpServletRequest.class);
        doNothing().when(filterChain).doFilter(captor.capture(), any(HttpServletResponse.class));
        decodeJsonPostContentFilter.doFilter(request, response, filterChain);

        HttpServletRequest value = captor.getValue();
        Assert.assertFalse(value instanceof HttpContentRequestWrapper);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void doFilterShouldNotFilterRequestForClientInfo() throws Exception {
        when(request.getMethod()).thenReturn(RequestMethod.POST.toString());
        when(request.getRequestURI()).thenReturn("log/v1_0/client-info");

        ArgumentCaptor<HttpServletRequest> captor = ArgumentCaptor.forClass(HttpServletRequest.class);
        doNothing().when(filterChain).doFilter(captor.capture(), any(HttpServletResponse.class));
        decodeJsonPostContentFilter.doFilter(request, response, filterChain);

        HttpServletRequest value = captor.getValue();
        Assert.assertFalse(value instanceof HttpContentRequestWrapper);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void doFilter_ShouldNotFilterNonJSONRequests() throws Exception {
        when(request.getMethod()).thenReturn(RequestMethod.POST.toString());
        when(request.getContentType()).thenReturn(MediaType.APPLICATION_ATOM_XML_VALUE);

        ArgumentCaptor<HttpServletRequest> captor = ArgumentCaptor.forClass(HttpServletRequest.class);
        doNothing().when(filterChain).doFilter(captor.capture(), any(HttpServletResponse.class));
        decodeJsonPostContentFilter.doFilter(request, response, filterChain);

        HttpServletRequest value = captor.getValue();
        Assert.assertFalse(value instanceof HttpContentRequestWrapper);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void doFilter_ShouldFilterJSONRequestsThatArePOSTs() throws Exception {
        when(request.getMethod()).thenReturn(RequestMethod.POST.toString());
        when(request.getContentType()).thenReturn(MediaType.APPLICATION_JSON_VALUE);
        when(request.getInputStream()).thenReturn(createInputStream());

        ArgumentCaptor<HttpServletRequest> captor = ArgumentCaptor.forClass(HttpServletRequest.class);
        doNothing().when(filterChain).doFilter(captor.capture(), any(HttpServletResponse.class));
        decodeJsonPostContentFilter.doFilter(request, response, filterChain);

        HttpServletRequest value = captor.getValue();
        Assert.assertThat(IOUtils.toString(value.getInputStream()), is("Some encoded String with + and %"));
        Assert.assertTrue(value instanceof HttpContentRequestWrapper);
        verify(filterChain).doFilter(any(HttpContentRequestWrapper.class), any(HttpServletResponse.class));
    }

    @Test
    public void doFilter_ShouldNotFilterRequestsThatDoNotHaveAContentTypeHeaderValue() throws Exception {
        when(request.getMethod()).thenReturn(RequestMethod.POST.toString());
        when(request.getContentType()).thenReturn(null);

        ArgumentCaptor<HttpServletRequest> captor = ArgumentCaptor.forClass(HttpServletRequest.class);
        doNothing().when(filterChain).doFilter(captor.capture(), any(HttpServletResponse.class));
        decodeJsonPostContentFilter.doFilter(request, response, filterChain);

        HttpServletRequest value = captor.getValue();
        Assert.assertFalse(value instanceof HttpContentRequestWrapper);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void doFilter_ShouldFilterJSONRequestsThatHaveACharsetInTheirContentTypeHeader() throws Exception {
        when(request.getMethod()).thenReturn(RequestMethod.POST.toString());
        when(request.getContentType()).thenReturn(MediaType.APPLICATION_JSON_VALUE + "; charset=UTF-8");
        when(request.getInputStream()).thenReturn(createInputStream());

        ArgumentCaptor<HttpServletRequest> captor = ArgumentCaptor.forClass(HttpServletRequest.class);
        doNothing().when(filterChain).doFilter(captor.capture(), any(HttpServletResponse.class));
        decodeJsonPostContentFilter.doFilter(request, response, filterChain);

        HttpServletRequest value = captor.getValue();
        Assert.assertThat(IOUtils.toString(value.getInputStream()), is("Some encoded String with + and %"));
        Assert.assertTrue(value instanceof HttpContentRequestWrapper);
        verify(filterChain).doFilter(any(HttpContentRequestWrapper.class), any(HttpServletResponse.class));
    }

    private ServletInputStream createInputStream() throws IOException{

        return new ServletInputStream() {
            private final InputStream inputStream = IOUtils.toInputStream("Some%20encoded%20String%20with%20%2B%20and%20%25", "UTF-8");

            @Override
            public int read() throws IOException {
                return inputStream.read();
            }
        };

    }
}