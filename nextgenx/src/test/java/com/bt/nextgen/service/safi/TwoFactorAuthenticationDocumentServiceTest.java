package com.bt.nextgen.service.safi;

import au.com.rsa.ps.smsotp.SMSOTPChallengeRequest;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.safi.model.SafiAnalyzeAndChallengeResponse;
import com.bt.nextgen.service.security.model.HttpRequestParams;
import com.rsa.csd.ws.ChallengeType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class TwoFactorAuthenticationDocumentServiceTest {

    @Mock
    private UserProfileService userProfileService;

    @InjectMocks
    private TwoFactorAuthenticationDocumentServiceImpl twoFactorAuthenticationDocumentService;

    @Mock
    private HttpRequestParams requestParams;

    private SafiAnalyzeAndChallengeResponse safiResponse;

    @Before
    public void setup() {
        safiResponse = mock(SafiAnalyzeAndChallengeResponse.class, RETURNS_DEEP_STUBS);
    }

    @Test
    public void testInitDeviceParameter() {
        ChallengeType challengeType = twoFactorAuthenticationDocumentService.createChallengeRequest(safiResponse, requestParams,
                "avaloqId", "safiDeviceId",
                "samlAssertion", "123456789");

        assertNotNull(challengeType);
        assertNotNull(challengeType.getRequest().getCredentialChallengeRequestList().getAcspChallengeRequestData().getPayload());
        SMSOTPChallengeRequest sMSOTPChallengeRequest = (SMSOTPChallengeRequest) challengeType.getRequest()
                .getCredentialChallengeRequestList().getAcspChallengeRequestData().getPayload();
        assertEquals(sMSOTPChallengeRequest.isInitDevice(), false);

    }
}
