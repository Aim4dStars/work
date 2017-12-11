package com.bt.nextgen.config.secure;

import javax.resource.spi.security.PasswordCredential;

import au.com.westpac.sparrow.security.WebSphereJ2CAccessor;
import com.btfin.panorama.core.config.secure.CredentialLocator;

/**
 * Information provided by Sparrow - <a href="https://library.sparrow.srv.westpac.com.au/confluence/display/javastrategy/Sparrow+Security+API">Sparrow Security API</a>
 */
public class WasCredentialLocator implements CredentialLocator {

    /**
     * This will locate your request credential by name.
     *
     * @param byName name must match what's in the J2C Authentication Alias definition

     * @return
     */
    @Override
    public PasswordCredential locateByName(String byName) {

        WebSphereJ2CAccessor webSphereJ2CAccessor = new WebSphereJ2CAccessor();
        webSphereJ2CAccessor.setAuthenticationAliasName(byName);
        return webSphereJ2CAccessor.getPasswordCredential();
    }
}
