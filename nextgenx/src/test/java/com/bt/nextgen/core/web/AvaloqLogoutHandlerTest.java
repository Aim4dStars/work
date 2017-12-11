package com.bt.nextgen.core.web;

import com.btfin.panorama.core.security.avaloq.AvaloqBankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.logout.service.LogoutService;
import com.bt.nextgen.service.prm.service.PrmService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.springframework.security.core.Authentication;

/**
 * Created by l082026 on 17/01/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class AvaloqLogoutHandlerTest {

    @InjectMocks
    private AvaloqLogoutHandler avaloqLogoutHandler;

    @Mock
    private LogoutService logoutService;

    @Mock
    private PrmService prmService;

    @Mock
    private MockHttpServletRequest mockHttpServletRequest;

    @Mock
    private MockHttpServletResponse mockHttpServletResponse;

    @Mock
    private Authentication authentication;

    @Mock
    private AvaloqBankingAuthorityService userSamlService;



    @Test
    public void logoutWithoutSamlTest(){
        when(userSamlService.getSamlToken()).thenReturn(null);
        avaloqLogoutHandler.logout(mockHttpServletRequest, mockHttpServletResponse,authentication);
        verify(prmService, never()).triggerLogOffPrmEvent(any(MockHttpServletRequest.class), any(ServiceErrorsImpl.class));
        verify(logoutService, never()).notifyLogoutUser(any(ServiceErrorsImpl.class));
    }

    @Test
    public void logoutTest(){
        when(userSamlService.getSamlToken()).thenReturn(new SamlToken(""));
        avaloqLogoutHandler.logout(mockHttpServletRequest, mockHttpServletResponse,authentication);
        verify(prmService, times(1)).triggerLogOffPrmEvent(any(MockHttpServletRequest.class), any(ServiceErrorsImpl.class));
        verify(logoutService, times(1)).notifyLogoutUser(any(ServiceErrorsImpl.class));
    }
}
