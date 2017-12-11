package com.bt.nextgen.core.security;

import javax.servlet.http.HttpServletRequest;

import com.btfin.panorama.core.security.profile.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationDetailsSource;

import com.btfin.panorama.core.security.profile.Profile;

import static com.bt.nextgen.core.util.SETTINGS.SAML_HEADER;

/**
 * This class allows us to run a username/password authentication, when the saml is missing.
 */
public class SamlAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, Profile> {

    private static final Logger logger = LoggerFactory.getLogger(SamlAuthenticationDetailsSource.class);

    @Autowired
    private UserProfileService userProfileService;

    @Override
    public Profile buildDetails(HttpServletRequest context) {
        Profile profile;

        if (userProfileService.isEmulating()) {
            profile = userProfileService.getEffectiveProfile();
        }
        else {
            profile = new Profile(new com.btfin.panorama.core.security.saml.SamlToken(findSamlTokenInHeader(context)));
        }

        return profile;
    }

    private static String findSamlTokenInHeader(HttpServletRequest context) {
        String[] headerOptions = SAML_HEADER.value().split(",");
        for (String headerOption : headerOptions) {
            if (context.getHeader(headerOption) != null) {
                logger.info("Found saml token in request header location {} ", headerOption);
                return context.getHeader(headerOption);
            }
        }

        throw new IllegalStateException("We are trying to get user details, without a saml token being present in " + SAML_HEADER.value());
    }
}
