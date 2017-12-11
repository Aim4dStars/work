package com.bt.nextgen.api.bgl.controller;

import com.bt.nextgen.api.bgl.service.AccountingSoftwareConnectionService;
import com.bt.nextgen.api.smsf.model.AccountingSoftwareDto;
import com.bt.nextgen.api.smsf.service.AccountingSoftwareDtoService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.authentication.model.TokenIssuer;
import com.bt.nextgen.service.integration.authentication.model.TokenResponseStatus;
import com.bt.nextgen.service.integration.authentication.model.TokenResponseStatusImpl;
import com.bt.nextgen.service.integration.authentication.service.TokenIntegrationService;
import org.apache.struts.mock.MockHttpSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BglConnectionControllerTest {
    @InjectMocks
    BglConnectionController bglConnectionController;

    @Mock
    private TokenIntegrationService tokenIntegrationService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private AccountingSoftwareDtoService accountingSoftwareDtoService;

    @Mock
    private AccountingSoftwareConnectionService accountingSoftwareConnectionService;

    @Mock
    private AccountIntegrationService accountService;


    @Before
    public void setup() {
        TokenResponseStatus tokenResponseStatus = new TokenResponseStatusImpl("SUCCESS", "all is good");

        when(tokenIntegrationService.getToken(anyString(), any(TokenIssuer.class))).thenReturn(tokenResponseStatus);
        when(userProfileService.getGcmId()).thenReturn("201609980");
        when(accountingSoftwareDtoService.update(any(AccountingSoftwareDto.class), any(ServiceErrors.class))).thenReturn(null);
        Mockito.when(accountingSoftwareConnectionService.getAccountantGcmIdForAccount(anyString())).thenReturn("123456");
    }

    @Test
    // Ensure internal redirects use the x-forwarded-host header value
    public void testAccountantConnectionWithAnExistingToken() throws Exception {
        final MockHttpServletResponse mockHttpResponse = new MockHttpServletResponse();
        final MockHttpServletRequest mockHttpRequest = new MockHttpServletRequest();
        final MockHttpSession mockHttpSession = new MockHttpSession();
        final String accountId = EncodedString.fromPlainText("12345").toString();

        mockHttpRequest.addHeader("x-forwarded-host", "dev2.panoramaadviser.srv.com.au");
        bglConnectionController.accountantConnectToAccountingSoftware(accountId, "BGL", mockHttpResponse, mockHttpRequest,
                mockHttpSession);

        assertEquals(mockHttpResponse.getRedirectedUrl(),
                "https://dev2.panoramaadviser.srv.com.au/ng/secure/app/#ng/account/portfolio/externalassets/viewdetails?a="
                        + accountId + "&connectStatus=success");
    }
}