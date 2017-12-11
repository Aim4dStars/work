package com.bt.nextgen.core.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class HttpHeadersRequestWrapper extends HttpServletRequestWrapper {

    /**
     * Custom headers storage
     */
    private Map<String, String> customHeaders = new HashMap<>();

    public HttpHeadersRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * @param name
     * @param value
     */
    public void addCustomHeader(String name, String value) {
        customHeaders.put(name, value);
    }

    /**
     *  Returns custom or parent's header value
     *  @param name header's name
     *  @return header's value
     */
    public String getHeader(String name) {
        return customHeaders.containsKey(name) ? customHeaders.get(name) : super.getHeader(name);
    }

    /**
     * Method returns custom or parent's header values
     * @param name header's name
     * @return header's values
     */
    @Override
    public Enumeration<String> getHeaders(String name) {
        if (customHeaders.containsKey(name)) {
            Vector<String> headerValues = new Vector<>();
            headerValues.add(customHeaders.get(name));
            return headerValues.elements();
        }
        return super.getHeaders(name);
    }

    /**
     * Method need to be overridden to return combined list of custom and original header names
     * @return request header names
     */
    @Override
    public Enumeration<String> getHeaderNames() {
        Vector<String> headers = new Vector<>();
        headers.addAll(customHeaders.keySet());
        Enumeration<String> parentHeaderNames = super.getHeaderNames();
        while(parentHeaderNames.hasMoreElements()) {
            headers.add(parentHeaderNames.nextElement());
        }
        return headers.elements();
    }
}
