package com.bt.nextgen.core.security;

import javax.servlet.http.HttpServletRequest;

import com.btfin.panorama.core.security.profile.UserProfileService;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.btfin.panorama.core.security.profile.Profile;
import com.bt.nextgen.util.SamlUtil;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SamlAuthenticationDetailsSourceTest {

    @Mock
    private UserProfileService userProfileService;

    @InjectMocks
    private SamlAuthenticationDetailsSource samlDetailsSource;

    @Before
    public void setup() {
        when(userProfileService.isEmulating()).thenReturn(false);
    }

    @Test(expected = IllegalStateException.class)
    public void testBuildDetails_exceptionSamlMissing() throws Exception {
        samlDetailsSource.buildDetails(Mockito.mock(HttpServletRequest.class));
    }

    @Test
    public void testBuildDetails_inHeader() throws Exception {
        final String saml = SamlUtil.loadSaml();

        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        when(mockRequest.getHeader("wbctoken")).thenReturn(saml);

        Profile result = samlDetailsSource.buildDetails(mockRequest);
        Assert.assertThat(result.getToken().getToken(), Is.is(saml));
    }
}
