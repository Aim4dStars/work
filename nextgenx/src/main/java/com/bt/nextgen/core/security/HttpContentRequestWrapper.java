package com.bt.nextgen.core.security;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class HttpContentRequestWrapper extends HttpServletRequestWrapper {

    private final CachedServletInputStream contentStream;

    private BufferedReader reader = null;

    public HttpContentRequestWrapper(HttpServletRequest request, String content)
    {
        super(request);
        this.contentStream = new CachedServletInputStream(content);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (reader == null) {
            reader = new BufferedReader(new InputStreamReader(getInputStream()));
        }
        return reader;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return contentStream;
    }

    public static class CachedServletInputStream extends ServletInputStream {

        private ByteArrayInputStream input;

        public CachedServletInputStream(String content) {
            input = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public int read() throws IOException {
            return input.read();
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return input.read(b, off, len);
        }
    }
}
