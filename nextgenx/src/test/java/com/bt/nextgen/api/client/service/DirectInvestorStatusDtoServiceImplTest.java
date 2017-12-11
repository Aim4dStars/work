package com.bt.nextgen.api.client.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.bt.nextgen.api.client.model.GcmKey;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.test.MockAuthentication;
import com.bt.nextgen.util.SamlUtil;

import static org.junit.Assert.assertEquals;

/**
 * Created by F030695 on 18/01/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class DirectInvestorStatusDtoServiceImplTest extends MockAuthentication {

    @InjectMocks
    private DirectInvestorStatusDtoServiceImpl directInvestorStatusDtoService;

    @Mock
    private UserProfileService userProfileService;

    private SamlToken samlToken;
    private Authentication authentication;
    @Before
    public void setup() {
        samlToken = new SamlToken(SamlUtil.loadWplSaml());
        authentication = SecurityContextHolder.getContext().getAuthentication();
    }

    @Test
    public void testResponse_withPanNumber() {
        mockAuthentication("investor");
        Mockito.when(userProfileService.getSamlToken()).thenReturn(samlToken);
        GcmKey response = directInvestorStatusDtoService.findOne(new ServiceErrorsImpl());
        assertEquals(response.getBankReferenceId(), "29027771");
        //assertEquals(response.getBankReferenceId(), "201618199"); TODO: this is correct response when wplIntegration is true
    }

    @Test
    public void testResponse_withNoPanNumber() {
        samlToken = new SamlToken(SamlUtil.loadWplSamlNewPanoramaCustomer());
        mockAuthentication("investor");
        Mockito.when(userProfileService.getSamlToken()).thenReturn(samlToken);
        GcmKey response = directInvestorStatusDtoService.findOne(new ServiceErrorsImpl());
        //assertNull(response.getBankReferenceId()); TODO: this is correct response when wplIntegration is true
        assertEquals(response.getBankReferenceId(), "29027771");
    }

    @After
    public void restoreAuthenticationContext() {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
