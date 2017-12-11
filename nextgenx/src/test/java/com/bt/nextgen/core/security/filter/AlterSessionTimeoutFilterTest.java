package com.bt.nextgen.core.security.filter;

import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Test;
import org.mockito.Mockito;

import com.btfin.panorama.core.security.Roles;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class AlterSessionTimeoutFilterTest {

    @Test
    public void testDoFilterUnauthenticatedChanged() throws Exception {
        HttpSession mockSession = mock(HttpSession.class);
        when(mockSession.getMaxInactiveInterval()).thenReturn(1);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockRequest.isUserInRole(eq(Roles.ROLE_ANONYMOUS.name()))).thenReturn(true);

        final int timeoutSetting = 10;
        AlterSessionTimeoutFilter filter = new AlterSessionTimeoutFilter(timeoutSetting);

        filter.doFilter(mockRequest, mock(ServletResponse.class), mock(FilterChain.class));

        verify(mockSession, times(1)).setMaxInactiveInterval(eq(timeoutSetting));
    }

    @Test
    public void testDoFilterUnauthenticatedTimeoutNotChangedWhenAlreadySet() throws Exception {
        final int timeoutSetting = 10;

        HttpSession mockSession = mock(HttpSession.class);
        when(mockSession.getMaxInactiveInterval()).thenReturn(timeoutSetting);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockRequest.isUserInRole(eq(Roles.ROLE_ANONYMOUS.name()))).thenReturn(true);

        AlterSessionTimeoutFilter filter = new AlterSessionTimeoutFilter(timeoutSetting);

        filter.doFilter(mockRequest, mock(ServletResponse.class), mock(FilterChain.class));

        verify(mockSession, never()).setMaxInactiveInterval(eq(timeoutSetting));
    }

    @Test
    public void testDoFilterAuthenticatedUnchanged() throws Exception {
        HttpSession mockSession = mock(HttpSession.class);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockRequest.isUserInRole(eq(Roles.ROLE_ANONYMOUS.name()))).thenReturn(false);

        AlterSessionTimeoutFilter filter = new AlterSessionTimeoutFilter(10);

        filter.doFilter(mockRequest, mock(ServletResponse.class), mock(FilterChain.class));

        verify(mockSession, never()).setMaxInactiveInterval(Mockito.anyInt());
    }
}