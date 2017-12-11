package com.bt.nextgen.emulation.controller;

import com.bt.nextgen.core.security.EmulationAuthenticationDetailsSource;
import com.bt.nextgen.core.security.EmulationRequestInfo;
import com.btfin.panorama.core.security.Roles;
import com.btfin.panorama.core.security.profile.Profile;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Created by M041926 on 29/03/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class EmulationControllerTest {

    private static final String CLIENT_KEY = "clientKey";

    @Mock
    private EmulationAuthenticationDetailsSource samlAuthenticationBuilder;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private Profile profile;

    @Mock
    private Profile emulatedProfile;

    @Mock
    private UserProfile activeProfile;

    @InjectMocks
    private EmulationController controller;

    @Before
    public void setup() throws Exception {
        when(activeProfile.getClientKey()).thenReturn(ClientKey.valueOf(CLIENT_KEY));
        when(profile.getActiveProfile()).thenReturn(activeProfile);
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        when(userProfileService.getBaseProfile()).thenReturn(profile);
        when(emulatedProfile.getRoles()).thenReturn(new Roles[]{Roles.ROLE_ADVISER});
        when(samlAuthenticationBuilder.buildDetails(Matchers.<EmulationRequestInfo>anyObject())).thenReturn(emulatedProfile);
    }

    @Test
    public void testStartEmulation() throws Exception {
        //Initially have service op role
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("", "", Roles.ROLE_SERVICE_OP.name());
        authentication.setDetails(profile);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String profileId = "123456";
        String gcmId = "456788";
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        String redirect = controller.startEmulation(profileId, gcmId, "emulated-username", redirectAttributes);
        assertEquals("Verify resulting redirect", redirect, EmulationController.REDIRECT_SECURE_APP);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertEquals("Check principal", "emulated-username", ((UserDetails) auth.getPrincipal()).getUsername());
        assertEquals("Check number of authorities", 3, auth.getAuthorities().size());

        boolean foundSwitchUserAuth = false;
        for (GrantedAuthority authority : auth.getAuthorities()) {
            if (authority instanceof SwitchUserGrantedAuthority) {
                foundSwitchUserAuth = true;
                assertEquals("Check authority value", "ROLE_SERVICE_OP",  authority.getAuthority());
            }
        }

        assertTrue("Check switch user authority present", foundSwitchUserAuth);
    }

    @Test
    public void testStopEmulation() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(createEmulatedAuthentication());
        String redirect = controller.stopEmulation("");
        assertTrue("Verify resulting redirect", redirect.startsWith(EmulationController.REDIRECT_SERVICE_OPS_ROOT) && redirect.endsWith("/detail"));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertEquals("Check principal", "original-username", auth.getPrincipal());
        assertEquals("Check number of authorities", 1, auth.getAuthorities().size());
        assertEquals("Check ROLE_SERVICE_OP", Roles.ROLE_SERVICE_OP.name(), auth.getAuthorities().toArray(new GrantedAuthority[1])[0].getAuthority());
    }

    @Test
    public void testStopEmulationRedirectToSearch() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(createEmulatedAuthentication());
        String redirect = controller.stopEmulation("search");
        assertEquals("Verify resulting redirect", EmulationController.REDIRECT_SERVICE_OPS_HOME, redirect);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertEquals("Check principal", "original-username", auth.getPrincipal());
        assertEquals("Check number of authorities", 1, auth.getAuthorities().size());
        assertEquals("Check ROLE_SERVICE_OP", Roles.ROLE_SERVICE_OP.name(), auth.getAuthorities().toArray(new GrantedAuthority[1])[0].getAuthority());
    }

    private Authentication createEmulatedAuthentication() {
        //Setup current emulating authentication
        TestingAuthenticationToken originalAuth = new TestingAuthenticationToken("original-username", "original-password", Roles.ROLE_SERVICE_OP.name());
        GrantedAuthority origAuthority = new SwitchUserGrantedAuthority(Roles.ROLE_SERVICE_OP.name(), originalAuth);
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(origAuthority);

        UsernamePasswordAuthenticationToken emulatedAuth = new UsernamePasswordAuthenticationToken("emulated-username", "emulated-password", authorities);
        emulatedAuth.setDetails(profile);
        return emulatedAuth;
    }
}
