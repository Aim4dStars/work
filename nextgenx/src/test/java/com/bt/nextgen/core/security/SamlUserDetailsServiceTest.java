package com.bt.nextgen.core.security;

import com.btfin.panorama.core.security.profile.Profile;
import com.bt.nextgen.core.util.SETTINGS;
import com.bt.nextgen.util.SamlUtil;
import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SamlUserDetailsServiceTest {
    private SamlUserDetailsService eud = new SamlUserDetailsService();

    @Test
    public void testRetrieveSimulatedUser() throws Exception {
        PreAuthenticatedAuthenticationToken mockToken = mock(PreAuthenticatedAuthenticationToken.class);
        when(mockToken.getDetails()).thenReturn(new Profile(new com.btfin.panorama.core.security.saml.SamlToken(loadSimulatedSaml())));

        UserDetails result = eud.loadUserDetails(mockToken);

        assertThat(result, IsNull.notNullValue());
        assertThat(result.getUsername(), is("56463638"));
        assertThat(result.getAuthorities().contains(com.btfin.panorama.core.security.Roles.ROLE_ADVISER.AUTHORITY), is(true));
    }


    @Test
    public void testRetrieveUnauthorisedUser() throws Exception {
        PreAuthenticatedAuthenticationToken mockToken = mock(PreAuthenticatedAuthenticationToken.class);
        when(mockToken.getDetails()).thenReturn(new Profile(new com.btfin.panorama.core.security.saml.SamlToken(loadUnauthSaml())));

        UserDetails result = eud.loadUserDetails(mockToken);

        assertThat(result, IsNull.notNullValue());
        assertThat(result.getUsername(), is(SETTINGS.SECURITY_UNAUTHED_USERNAME.value()));
        assertThat(result.getAuthorities().contains(com.btfin.panorama.core.security.Roles.ROLE_ANONYMOUS.AUTHORITY), is(true));
    }

    @Test
    public void testRetrieveDirectInvestor_newClient() throws Exception {
        PreAuthenticatedAuthenticationToken mockToken = mock(PreAuthenticatedAuthenticationToken.class);
        when(mockToken.getDetails()).thenReturn(new Profile(new com.btfin.panorama.core.security.saml.SamlToken(loadDirectInvestorSaml())));

        UserDetails result = eud.loadUserDetails(mockToken);

        assertThat(result, IsNull.notNullValue());
        assertThat(result.getUsername(), is("29027771"));
        assertThat(result.getAuthorities().contains(com.btfin.panorama.core.security.Roles.ROLE_INVESTOR.AUTHORITY), is(true));
    }

    private String loadUnauthSaml() {
        return SamlUtil.loadSaml("/unauthenticated-saml.xml");
    }

    private String loadSimulatedSaml() {
        return SamlUtil.loadSaml("/enrich-saml-sample.xml");
    }

    private String loadDirectInvestorSaml() {
        return SamlUtil.loadSaml("/direct-saml.xml");
    }

}
