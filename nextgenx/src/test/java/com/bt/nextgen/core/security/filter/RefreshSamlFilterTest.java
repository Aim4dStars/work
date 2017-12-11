package com.bt.nextgen.core.security.filter;

import com.bt.nextgen.core.security.SamlAuthenticationDetailsSource;
import com.btfin.panorama.core.security.profile.Profile;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.*;

/**
 * Created by M041926 on 7/03/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class RefreshSamlFilterTest {

    @Mock
    private SamlAuthenticationDetailsSource samlSource;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ServletResponse response;

    @Mock
    private FilterChain chain;

    @Mock
    private Authentication authentication;

    @Mock
    private Profile currentProfile;

    @InjectMocks
    private RefreshSamlFilter refreshSamlFilter;

    @Before
    public void setup() {
        when(currentProfile.isExpired()).thenReturn(true);
        when(authentication.getDetails()).thenReturn(currentProfile);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void doFilter() throws Exception {
        refreshSamlFilter.doFilter(request, response, chain);
        verify(samlSource, times(1)).buildDetails(request);
    }

}