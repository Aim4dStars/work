package com.bt.nextgen.api.safi.facade;

import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.safi.TwoFactorAuthenticationIntegrationService;
import com.bt.nextgen.service.safi.model.SafiAnalyzeAndChallengeResponse;
import com.bt.nextgen.service.safi.model.SafiAuthenticateRequest;
import com.bt.nextgen.service.safi.model.SafiAuthenticateResponse;
import com.bt.nextgen.service.security.model.HttpRequestParams;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpSession;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TwoFactorAuthenticationServiceTest {

    @Mock
    private UserProfileService userProfileService;

    @InjectMocks
    private TwoFactorAuthenticationServiceImpl twoFactorAuthenticationServiceImpl;

    @Mock
    private TwoFactorAuthenticationIntegrationService twoFactorAuthenticationService;

    @Mock
    private SafiAuthenticateRequest safiAuthenticateRequest;

    @Mock
    private UserProfile userProfile;

    @Mock
    private MockHttpSession httpSession;

    @Before
    public void setUp() throws Exception {
        when(safiAuthenticateRequest.getSmsCode()).thenReturn("123456");

        SafiAuthenticateResponse safiAuthenticateResponse = new SafiAuthenticateResponse();
        safiAuthenticateResponse.setSuccessFlag(true);

        when(userProfile.getClientKey()).thenReturn(ClientKey.valueOf("123"));
        when(userProfileService.getActiveProfile()).thenReturn(userProfile);

        when(
                twoFactorAuthenticationService.authenticate(eq("123456"), any(HttpRequestParams.class),
                        any(SafiAnalyzeAndChallengeResponse.class), any(ServiceErrors.class)))
                .thenReturn(safiAuthenticateResponse);
    }

    @Test
    public void testAuthenticate() throws Exception {
        when(userProfileService.isLoggedIn()).thenReturn(true);

        SafiAuthenticateResponse safiAuthenticateResponse = twoFactorAuthenticationServiceImpl.authenticate(
                safiAuthenticateRequest);

        assertNotNull(safiAuthenticateResponse);
    }

    @Test
    public void testAuthenticateNoABSUpdate() throws Exception {
        when(userProfileService.isLoggedIn()).thenReturn(true);
        SafiAuthenticateResponse safiAuthenticateResponse = twoFactorAuthenticationServiceImpl.authenticate(safiAuthenticateRequest);
        assertNotNull(safiAuthenticateResponse);
    }

    @Test
    public void testWhenUserNotLoggedIn() throws Exception {
        when(userProfileService.isLoggedIn()).thenReturn(false);
        SafiAuthenticateResponse safiAuthenticateResponse = twoFactorAuthenticationServiceImpl.authenticate(
                safiAuthenticateRequest);
        assertNotNull(safiAuthenticateResponse);
    }

    @Test
    public void testInvestorUserNot2FAVerified() throws Exception {
        when(userProfileService.isInvestor()).thenReturn(true);
        when(httpSession.getAttribute("USER_DETAILS_2FA_VERIFIED")).thenReturn(false);
        boolean result = twoFactorAuthenticationServiceImpl.is2FAVerified("USER_DETAILS");
        assertFalse(result);
    }

    @Test
    public void testInvestorUserIsFAVerified() throws Exception {
        when(userProfileService.isInvestor()).thenReturn(true);
        when(httpSession.getAttribute("USER_DETAILS_2FA_VERIFIED")).thenReturn(true);
        boolean result = twoFactorAuthenticationServiceImpl.is2FAVerified("USER_DETAILS");
        assertTrue(result);
    }

    @Test
    public void testNotInvestorUserNot2FAVerified() throws Exception {
        when(userProfileService.isInvestor()).thenReturn(false);
        boolean result = twoFactorAuthenticationServiceImpl.is2FAVerified("USER_DETAILS");
        assertTrue(result);
    }

    @Test
    public void testInvestorUserNot2FAVerifiedAndNull() throws Exception {
        when(userProfileService.isInvestor()).thenReturn(true);
        when(httpSession.getAttribute("USER_DETAILS_2FA_VERIFIED")).thenReturn(null);
        boolean result = twoFactorAuthenticationServiceImpl.is2FAVerified("USER_DETAILS");
        assertFalse(result);
    }

}
