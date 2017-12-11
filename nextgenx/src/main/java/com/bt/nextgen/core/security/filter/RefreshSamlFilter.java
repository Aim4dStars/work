package com.bt.nextgen.core.security.filter;

import com.bt.nextgen.core.security.SamlAuthenticationDetailsSource;
import com.btfin.panorama.core.security.profile.Profile;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Webseal will send through 'unauthenticated' for users that haven't yet authenticated.
 */
public class RefreshSamlFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(RefreshSamlFilter.class);

    @Autowired
    private SamlAuthenticationDetailsSource samlSource;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        logger.debug("Start creating profile from SAML");
        Profile currentProfile = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            currentProfile =  (Profile) authentication.getDetails();
        }

        // performance optimisation, keep using what we have until it has expired
        // TODO: expiry needs to occur prior to actual hard expiry (15 sec) -- 
        // if tokens dont match but iv-user does then refresh 
        if (currentProfile != null && currentProfile.isExpired()) {
            logger.debug("Profile expired, refreshing profile using request");
            Profile profile = samlSource.buildDetails((HttpServletRequest) request);
            currentProfile.refreshFrom(profile);
            logger.debug(StringUtils.isEmpty(currentProfile.getCustDefinedLogin()) ? "No user customer defined login" : "customer defined login is = {}", currentProfile.getCustDefinedLogin());
            logger.debug(StringUtils.isEmpty(currentProfile.getUserName()) ? "No user customer defined login" : "Username is = {}", currentProfile.getUserName());
        }
        chain.doFilter(request, response);

    }
}