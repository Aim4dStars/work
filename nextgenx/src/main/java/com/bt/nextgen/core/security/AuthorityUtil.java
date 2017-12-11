package com.bt.nextgen.core.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by M041926 on 23/03/2016.
 */
public class AuthorityUtil {

    private static final Logger logger = LoggerFactory.getLogger(AuthorityUtil.class);

    private AuthorityUtil() {

    }

    public static Collection<GrantedAuthority> grantAuthorities(com.btfin.panorama.core.security.Roles[] samlRoles) {
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        for (com.btfin.panorama.core.security.Roles parsedRole : samlRoles) {
            try {
                grantedAuthorities.add(parsedRole.AUTHORITY);
            } catch (IllegalArgumentException e) {
                logger.info("Problem mapping role {} - ignoring", parsedRole);
            }
        }

        if (grantedAuthorities.size() < 1) {
            throw new BadCredentialsException("We haven't found any roles for you");
        }

        if (!grantedAuthorities.contains(com.btfin.panorama.core.security.Roles.ROLE_ANONYMOUS.AUTHORITY)) {
            grantedAuthorities.add(com.btfin.panorama.core.security.Roles.ROLE_USER.AUTHORITY);
        }

        return grantedAuthorities;
    }
}
