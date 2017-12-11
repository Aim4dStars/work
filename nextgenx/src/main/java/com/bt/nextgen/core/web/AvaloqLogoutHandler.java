package com.bt.nextgen.core.web;

import com.btfin.panorama.core.security.avaloq.AvaloqBankingAuthorityService;
import com.bt.nextgen.logout.service.LogoutService;
import com.bt.nextgen.service.prm.service.PrmService;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class used to do actions before logging out.
 * Before Redirecting, it sends request to the Avaloq to deregister user from sending notification count invalidation message
 */
public class AvaloqLogoutHandler implements org.springframework.security.web.authentication.logout.LogoutHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AvaloqLogoutHandler.class);

    @Autowired
    private LogoutService logoutService;

    @Autowired
    private PrmService prmService;

    @Resource(name = "userDetailsService")
    private AvaloqBankingAuthorityService userSamlService;

    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {
        if (userSamlService.getSamlToken() == null) {
            LOGGER.info("User SAML is not available. Will not deregister the user from Avaloq.");
            return;
        }
        LOGGER.info("Sending Avaloq request before logging out...");
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        prmService.triggerLogOffPrmEvent(httpServletRequest, serviceErrors);
        logoutService.notifyLogoutUser(serviceErrors);
    }
}
