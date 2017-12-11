package com.bt.nextgen.core.security;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.w3c.dom.Document;

import com.btfin.panorama.core.security.integration.customer.CredentialType;
import com.bt.nextgen.service.integration.user.CISKey;
import com.btfin.panorama.core.security.UserAccountStatus;
import com.bt.nextgen.util.SamlUtil;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;


public class SamlTokenTest {

    private static java.util.Properties properties;

    @Test
    public void testGetToken() throws Exception {
        Resource resource = new ClassPathResource("/common.properties");
        properties = PropertiesLoaderUtils.loadProperties(resource);
        properties.putAll(System.getProperties());
        System.setProperty("wpl.integration.enabled","false");

        String stringToken = SamlUtil.loadSaml();
        com.btfin.panorama.core.security.saml.SamlToken token = new com.btfin.panorama.core.security.saml.SamlToken(stringToken);

        assertThat(token.getToken(), is(stringToken));
    }

    @Test
    public void testGetGcmId() throws Exception {
        com.btfin.panorama.core.security.saml.SamlToken token = new com.btfin.panorama.core.security.saml.SamlToken(SamlUtil.loadSaml());

        assertThat(token.getGcmId(), is("217082760"));
    }

    @Test
    public void testGetCustomerId() {
        com.btfin.panorama.core.security.saml.SamlToken token = new com.btfin.panorama.core.security.saml.SamlToken(SamlUtil.loadSaml());

        assertThat(token.getBankReferenceId(), is("217082760"));
    }

    @Test
    public void testGetUsername() {
        com.btfin.panorama.core.security.saml.SamlToken token = new com.btfin.panorama.core.security.saml.SamlToken(SamlUtil.loadSaml());
        assertThat(token.getUsername(), is("adviser"));
    }


    public void testGetPrimaryStatus() {
        com.btfin.panorama.core.security.saml.SamlToken token = new com.btfin.panorama.core.security.saml.SamlToken(SamlUtil.loadSaml());

        assertThat(token.getPrimaryStatus(), is(UserAccountStatus.ACTIVE));
    }

    @Test
    public void testGetRoleName() throws Exception {
        com.btfin.panorama.core.security.saml.SamlToken token = new com.btfin.panorama.core.security.saml.SamlToken(SamlUtil.loadSaml());

        assertThat(token.getRoleNames()[0], is("bt-adviser"));
    }

    @Test
    public void testDealerGroup() throws Exception {
        com.btfin.panorama.core.security.saml.SamlToken token = new com.btfin.panorama.core.security.saml.SamlToken(SamlUtil.loadSaml());

        assertThat(token.getDealerGroup(), is("WPAC"));
    }

    @Test
    public void testUserId() throws Exception {
        com.btfin.panorama.core.security.saml.SamlToken token = new com.btfin.panorama.core.security.saml.SamlToken(SamlUtil.loadSaml());

        assertThat(token.getUserId(), is("217082760"));
    }

    @Test
    public void testAvaloqId() throws Exception {
        com.btfin.panorama.core.security.saml.SamlToken token = new com.btfin.panorama.core.security.saml.SamlToken(SamlUtil.loadSaml());

        assertThat(token.getAvaloqId(), is("217082760"));
    }

    @Test
    public void testGetAttribute() throws Exception {
        com.btfin.panorama.core.security.saml.SamlToken token = new com.btfin.panorama.core.security.saml.SamlToken(SamlUtil.loadSaml());

        assertThat(token.getAttributeValue("AZN_CRED_MECH_ID"), is("IV_LDAP_V3.0"));
    }

    @Test
    public void testCredentialId() throws Exception {
        com.btfin.panorama.core.security.saml.SamlToken token = new com.btfin.panorama.core.security.saml.SamlToken(SamlUtil.loadSaml());

        assertThat(token.getCredentialId(), is("79c7f55c-a4a2-4640-be47-311ca728328c"));
    }

    @Test
    public void testGetCustDefinedLogin() {
        com.btfin.panorama.core.security.saml.SamlToken token = new com.btfin.panorama.core.security.saml.SamlToken(SamlUtil.loadSaml(com.btfin.panorama.core.security.Roles.ROLE_ADVISER));

        assertThat(token.getCustDefinedLogin(), is("adviser"));
    }

    @Test
    public void testIsAuthenticated() {
        com.btfin.panorama.core.security.saml.SamlToken token = new com.btfin.panorama.core.security.saml.SamlToken(SamlUtil.loadSaml());
        assertTrue(token.isAuthenticated());

        token = new com.btfin.panorama.core.security.saml.SamlToken(SamlUtil.loadSaml("unauthenticated-saml.xml"));
        assertFalse(token.isAuthenticated());
    }

    @Test
    public void testGetTokenAsDocument() {
        com.btfin.panorama.core.security.saml.SamlToken token = new com.btfin.panorama.core.security.saml.SamlToken(SamlUtil.loadSaml());

        Document document = token.getTokenAsDocument();
        assertThat(document, notNullValue());
    }

    @Test
    public void testIsExpired() {
        com.btfin.panorama.core.security.saml.SamlToken token = new com.btfin.panorama.core.security.saml.SamlToken(SamlUtil.loadSaml());
        boolean isExpired = token.isExpired();
        assertTrue(isExpired);
    }

    @Test
    public void testGetSession() {
        com.btfin.panorama.core.security.saml.SamlToken token = new com.btfin.panorama.core.security.saml.SamlToken(SamlUtil.loadSaml());
        assertThat(token.getSession(), notNullValue());
    }

    @Test
    public void testGetCredentialType() {
        com.btfin.panorama.core.security.saml.SamlToken token = new com.btfin.panorama.core.security.saml.SamlToken(SamlUtil.loadSaml());
        assertThat(token.getCredentialType(), is(CredentialType.ONL));

        token = new com.btfin.panorama.core.security.saml.SamlToken(SamlUtil.loadSaml("unauthenticated-saml.xml"));
        assertThat(token.getCredentialType(), is(CredentialType.UNKNOWN));
    }

    @Test
    public void testGetCredentialGroups() {
        com.btfin.panorama.core.security.saml.SamlToken token = new com.btfin.panorama.core.security.saml.SamlToken(SamlUtil.loadSaml());
        assertThat(token.getCredentialGroups().contains(com.btfin.panorama.core.security.Roles.ROLE_ADVISER), is(true));

        token = new com.btfin.panorama.core.security.saml.SamlToken(SamlUtil.loadSaml("unauthenticated-saml.xml"));
        assertThat(token.getCredentialGroups(), contains(com.btfin.panorama.core.security.Roles.ROLE_ANONYMOUS));
    }

    @Test
    public void testGetLastUsed() {
        com.btfin.panorama.core.security.saml.SamlToken token = new com.btfin.panorama.core.security.saml.SamlToken(SamlUtil.loadSaml());
        assertThat(token.getLastUsed(), is("14 Oct 2014"));
    }

    public void testGetCISKey() throws Exception {
        com.btfin.panorama.core.security.saml.SamlToken token = new com.btfin.panorama.core.security.saml.SamlToken(SamlUtil.loadSaml());
        assertThat(token.getCISKey(), is(CISKey.valueOf("123456789")));
    }

    @Test
    public void testGetUserGroup_PAN_SamlToken()  throws Exception{
        Resource resource = new ClassPathResource("/common.properties");
        properties = PropertiesLoaderUtils.loadProperties(resource);
        properties.putAll(System.getProperties());
        System.setProperty("wpl.integration.enabled","false");

        com.btfin.panorama.core.security.saml.SamlToken token = new com.btfin.panorama.core.security.saml.SamlToken(SamlUtil.loadSaml("saml-sample.xml"));
        assertNotNull(token.getUserGroup());
        assertThat(token.getUserGroup().size(), is(1));
        assertThat(token.getUserReferenceId(), is("217082760"));
    }

    @Test
      public void testGetUserGroup_WPL_SamlToken()  throws Exception{
        Resource resource = new ClassPathResource("/common.properties");
        properties = PropertiesLoaderUtils.loadProperties(resource);
        properties.putAll(System.getProperties());
        System.setProperty("wpl.integration.enabled","true");

        com.btfin.panorama.core.security.saml.SamlToken token = new com.btfin.panorama.core.security.saml.SamlToken(SamlUtil.loadSaml("saml-sample-wpl.xml"));
        assertNotNull(token.getUserGroup());
        assertThat(token.getUserGroup().size(), is(1));
       assertThat(token.getUserReferenceId(), is("19471122"));
    }

    @Test
    public void testSamlToken_Staff() {
        com.btfin.panorama.core.security.saml.SamlToken token = new com.btfin.panorama.core.security.saml.SamlToken(SamlUtil.loadSaml("staff-saml.xml"));
        assertThat(token.getUserGroup().size(), is(1));
        assertThat(token.getCredentialGroups().get(0), is(com.btfin.panorama.core.security.Roles.ROLE_SERVICE_OP));
        assertThat(token.getRoleNames().length, is(3));

    }
    @Test
    public void testGetPPId_whenNotEmpty(){
        com.btfin.panorama.core.security.saml.SamlToken token = new com.btfin.panorama.core.security.saml.SamlToken(SamlUtil.loadSaml("saml-sample-updated.xml"));
        assertNotNull(token.getPpId());
        assertThat(token.getPpId() , is("826340597"));
    }
    @Test
    public void testGetPPId_whenEmpty(){
        com.btfin.panorama.core.security.saml.SamlToken token = new com.btfin.panorama.core.security.saml.SamlToken(SamlUtil.loadSamlwithoutPPId());
        assertNull(token.getPpId());
    }
}
