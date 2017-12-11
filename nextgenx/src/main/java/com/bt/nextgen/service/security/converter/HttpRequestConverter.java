package com.bt.nextgen.service.security.converter;

import com.bt.nextgen.service.security.model.HttpRequestParams;

import javax.servlet.http.HttpServletRequest;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class HttpRequestConverter {

    public static HttpRequestParams toHttpRequestParams(HttpServletRequest request) {
        String httpAcceptChars = request.getHeader("Content-Type");
        String httpAcceptEncoding = request.getHeader("Accept-Encoding");
        String clientIpAddress = request.getHeader("X-FORWARDED-FOR");

        if (isBlank(httpAcceptChars)) {
            httpAcceptChars = "ISO-8859-1";
        }

        if (isBlank(httpAcceptEncoding)) {
            httpAcceptEncoding = "gzip, deflate";
        }
        if (isBlank(clientIpAddress)) {
            clientIpAddress = request.getRemoteAddr();
        }

        final HttpRequestParams httpParams = new HttpRequestParams();
        httpParams.setHttpAccept(request.getHeader("Accept"));
        httpParams.setHttpAcceptChars(httpAcceptChars);
        httpParams.setHttpAcceptEncoding(httpAcceptEncoding);
        httpParams.setHttpAcceptLanguage(request.getHeader("Accept-Language"));
        httpParams.setHttpOriginatingIpAddress(clientIpAddress);
        httpParams.setHttpReferrer(request.getHeader("Referer"));
        httpParams.setHttpUserAgent(request.getHeader("User-Agent"));

        return httpParams;
    }
}
