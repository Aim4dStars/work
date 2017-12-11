package com.bt.nextgen.core.security;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * Created by M041926 on 31/03/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class HttpHeadersRequestWrapperTest {

    @Mock
    private HttpServletRequest request;

    private HttpHeadersRequestWrapper wrapper;

    @Before
    public void setup() {
        Vector<String> originalHeaders = new Vector<>();
        originalHeaders.add("header1");
        originalHeaders.add("header2");

        Vector<String> header3Values = new Vector<>();
        header3Values.add("header3Value1");
        header3Values.add("header3Value2");

        when(request.getHeaderNames()).thenReturn(originalHeaders.elements());
        when(request.getHeader(eq("header1"))).thenReturn("header1Value");
        when(request.getHeader(eq("header2"))).thenReturn("header2Value");
        when(request.getHeaders(eq("header3"))).thenReturn(header3Values.elements());


        wrapper = new HttpHeadersRequestWrapper(request);
        wrapper.addCustomHeader("customHeader1", "customHeader1Value");
        wrapper.addCustomHeader("customHeader2", "customHeader2Value");
    }

    @Test
    public void testGetHeader() throws Exception {
        assertEquals("Check parent's header", "header2Value", wrapper.getHeader("header2"));
        assertEquals("Check custom header", "customHeader2Value", wrapper.getHeader("customHeader2"));
    }

    @Test
    public void testGetHeaders() throws Exception {
        Enumeration<String> headers = wrapper.getHeaders("header3");
        assertTrue("Verify headers from parent.", headers.nextElement() != null && headers.nextElement() != null);
        Enumeration<String> customHeaders = wrapper.getHeaders("customHeader2");
        assertTrue("Verify custom headers", customHeaders.nextElement() != null);
    }

    @Test
    public void testGetHeaderNames() throws Exception {
        Enumeration<String> names = wrapper.getHeaderNames();
        Set<String> headerNames = new HashSet<>();
        while(names.hasMoreElements()) {
            headerNames.add(names.nextElement());
        }
        assertEquals("Check how many header names", 4, headerNames.size());
    }
}