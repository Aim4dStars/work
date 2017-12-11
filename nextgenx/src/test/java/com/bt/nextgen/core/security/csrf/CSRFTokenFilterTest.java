package com.bt.nextgen.core.security.csrf;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

public class CSRFTokenFilterTest {
    @InjectMocks
    private CSRFTokenFilter csrfTokenFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;
    private static final String csrfToken = UUID.randomUUID().toString();
    private static final String CSRF_TOKEN = "securityToken";
    private static final String CSRF_TOKEN_ERROR = "securityTokenError";
    private static final String CSRF_ERROR_MSG = "Missing or non-matching CSRF token";

    @Before
    public void setUp() {
        csrfTokenFilter = new CSRFTokenFilter();
        request = new MockHttpServletRequest();
        request.setServletPath("/secure/api/accounts/123456/deposits/submitDeposit");
        request.setMethod(RequestMethod.POST.name());
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);
    }

    @Test
    public void testSuccessfulTokenMatch() throws ServletException, IOException {
        request.addHeader(CSRF_TOKEN, csrfToken);
        request.setCookies(new Cookie(CSRF_TOKEN, csrfToken));
        csrfTokenFilter.doFilterInternal(request, response, filterChain);

        assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
        assertNull(response.getHeader(CSRF_TOKEN_ERROR));
    }

    @Test
    public void testSuccessfulTokenMatchForUploadRequest() throws ServletException, IOException {
        request.setServletPath("/secure/upload");
        request.setMethod(RequestMethod.POST.name());
        request.addHeader(CSRF_TOKEN, csrfToken);
        request.setCookies(new Cookie(CSRF_TOKEN, csrfToken));

        csrfTokenFilter.doFilterInternal(request, response, filterChain);
        assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
        assertNull(response.getHeader(CSRF_TOKEN_ERROR));
    }

    @Test
    public void testNullTokenValue() throws ServletException, IOException {
        request.setCookies(new Cookie(CSRF_TOKEN, csrfToken));
        csrfTokenFilter.doFilterInternal(request, response, filterChain);

        assertEquals(response.getStatus(), HttpServletResponse.SC_FORBIDDEN);
        assertEquals(response.getHeader(CSRF_TOKEN_ERROR), CSRF_ERROR_MSG);
    }

    @Test
    public void testMissingCookieTokenValue() throws ServletException, IOException {
        request.addHeader(CSRF_TOKEN, csrfToken);
        request.setCookies(new Cookie("nonSecurityToken", csrfToken));
        csrfTokenFilter.doFilterInternal(request, response, filterChain);

        assertEquals(response.getStatus(), HttpServletResponse.SC_FORBIDDEN);
        assertEquals(response.getHeader(CSRF_TOKEN_ERROR), CSRF_ERROR_MSG);
    }

    @Test
    public void testNonMatchingCookieTokenValue() throws ServletException, IOException {
        request.addHeader(CSRF_TOKEN, csrfToken);
        request.setCookies(new Cookie(CSRF_TOKEN, "123"));
        csrfTokenFilter.doFilterInternal(request, response, filterChain);

        assertEquals(response.getStatus(), HttpServletResponse.SC_FORBIDDEN);
        assertEquals(response.getHeader(CSRF_TOKEN_ERROR), CSRF_ERROR_MSG);
    }

    @Test
    public void testIgnoreGetRequest() throws ServletException, IOException {
        request.setMethod(RequestMethod.GET.name());
        request.addHeader(CSRF_TOKEN, csrfToken);
        request.setCookies(new Cookie(CSRF_TOKEN, csrfToken));
        csrfTokenFilter.doFilterInternal(request, response, filterChain);

        assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
        assertNull(response.getHeader(CSRF_TOKEN_ERROR));
    }

    @Test
    public void testIgnoreUncheckedPath() throws ServletException, IOException {
        request.setServletPath("/public/api/accounts/123456/deposits/submitDeposit");
        request.addHeader(CSRF_TOKEN, csrfToken);
        request.setCookies(new Cookie(CSRF_TOKEN, csrfToken));
        csrfTokenFilter.doFilterInternal(request, response, filterChain);
        csrfTokenFilter.doFilterInternal(request, response, filterChain);

        assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
        assertNull(response.getHeader(CSRF_TOKEN_ERROR));
    }
}
