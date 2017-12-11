package com.bt.nextgen.config.secure;

import com.bt.nextgen.core.util.Properties;
import com.btfin.panorama.core.config.secure.CredentialLocator;

import javax.resource.spi.security.PasswordCredential;

/**
 * This class will look up credentials stored in properties - for when you just gotta
 *
 * Prefer J2c for a more secure way
 *
 * @see au.com.westpac.sparrow.security.WebSphereJ2CAccessor
 */
public class PropertiesCredentialLocator implements CredentialLocator {

    /**
     * Given a 'name' the string '.password' and '.username' will be appended to locate the property
     * @param byName the base name, '.password' and '.username' will be use to locate the same
     * @return The credential
     */
    @Override
    public PasswordCredential locateByName(String byName) {
        return new PasswordCredential(Properties.get(byName+".username"), Properties.get(byName+".password").toCharArray());
    }
}
