package com.bt.nextgen.core.security;

import com.bt.nextgen.util.SamlUtil;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by F057654 on 11/12/2015.
 */
public class WPLSamlTokenTest {

    private static java.util.Properties properties;

    @Test
    public void testSamlToken_NonWPLUser() throws Exception{
        Resource resource = new ClassPathResource("/common.properties");
        properties = PropertiesLoaderUtils.loadProperties(resource);
        properties.putAll(System.getProperties());
        System.setProperty("wpl.integration.enabled","false");

        com.btfin.panorama.core.security.saml.SamlToken token = new com.btfin.panorama.core.security.saml.SamlToken(SamlUtil.loadSaml("saml-sample.xml"));
       // assertThat(token.getUserGroup(), is(UserGroup.PAN_USER));
        assertThat(token.getAvaloqId(), is("217082760"));
        assertThat(token.getBankReferenceId(), is("217082760"));
        assertThat(token.getCISKey().getId(), is("123456789"));
        assertThat(token.getCredentialGroups().get(0), is(com.btfin.panorama.core.security.Roles.ROLE_ADVISER));
        assertThat(token.getGcmId(), is("217082760"));
        //assertThat(token.getUserGroup(), is(UserGroup.PAN_USER));
        assertThat(token.getUsername(), is("adviser"));
        assertThat(token.getRoleNames().length, is(1));
        assertThat(token.getDealerGroup(), is("WPAC"));
        assertThat(token.getBankDefinedLogin(), is("217082760"));
    }
}
