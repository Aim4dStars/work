package com.bt.nextgen.service.security.converter;

import com.bt.nextgen.service.security.model.HttpRequestParams;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class HttpRequestConverterTest {

    @Test
    public void toHttpRequestParams() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Content-Type", "application/json, text/javascript");
        request.addHeader("Accept-Encoding", "gzip");
        request.addHeader("X-FORWARDED-FOR", "11.11.11.11");
        request.addHeader("Accept", "*/*");
        request.addHeader("Accept-Language", "en-GB");
        request.addHeader("Referer", "https://www.panoramainvestor.com.au/");
        request.addHeader("User-Agent", "Mozilla/5.0");
        request.setRemoteAddr("10.10.10.10");

        HttpRequestParams params = HttpRequestConverter.toHttpRequestParams(request);
        assertEquals(params.getHttpAccept(), "*/*");
        assertEquals(params.getHttpAcceptChars(), "application/json, text/javascript");
        assertEquals(params.getHttpAcceptEncoding(), "gzip");
        assertEquals(params.getHttpAcceptLanguage(), "en-GB");
        assertEquals(params.getHttpOriginatingIpAddress(), "11.11.11.11");
        assertEquals(params.getHttpReferrer(), "https://www.panoramainvestor.com.au/");
        assertEquals(params.getHttpUserAgent(), "Mozilla/5.0");
    }

    @Test
    public void toHttpRequestParams_missingParams() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Accept", "*/*");
        request.addHeader("Accept-Language", "en-GB");
        request.addHeader("Referer", "https://www.panoramainvestor.com.au/");
        request.addHeader("User-Agent", "Mozilla/5.0");
        request.setRemoteAddr("10.10.10.10");

        HttpRequestParams params = HttpRequestConverter.toHttpRequestParams(request);
        assertEquals(params.getHttpAccept(), "*/*");
        assertEquals(params.getHttpAcceptChars(), "ISO-8859-1");
        assertEquals(params.getHttpAcceptEncoding(), "gzip, deflate");
        assertEquals(params.getHttpAcceptLanguage(), "en-GB");
        assertEquals(params.getHttpOriginatingIpAddress(), "10.10.10.10");
        assertEquals(params.getHttpReferrer(), "https://www.panoramainvestor.com.au/");
        assertEquals(params.getHttpUserAgent(), "Mozilla/5.0");
    }

}