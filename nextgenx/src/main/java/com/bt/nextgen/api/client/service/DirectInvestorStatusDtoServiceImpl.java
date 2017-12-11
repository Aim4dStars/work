package com.bt.nextgen.api.client.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.client.model.GcmKey;
import com.btfin.panorama.core.security.profile.Profile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;

/**
 * Check a user's SAML token for a PAN number
 * <p/>
 * Created by F030695 on 18/01/2016.
 */
@Service
public class DirectInvestorStatusDtoServiceImpl implements DirectInvestorStatusDtoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectInvestorStatusDtoServiceImpl.class);

    @Autowired
    private UserProfileService userProfileService;

    @Override
    public GcmKey findOne(ServiceErrors serviceErrors) {
        refreshSamlToken();
        String gcmId = userProfileService.getSamlToken().getGcmId();
        LOGGER.debug("gcmId found in SAML token: {}", gcmId);
        return new GcmKey(gcmId);
    }

    /**
     * Manually force SAML token to expire, then rebuilt profile using new SAML token taken from request header
     */
    private void refreshSamlToken() {
        Profile currentProfile = (Profile) SecurityContextHolder.getContext().getAuthentication().getDetails();
        currentProfile.forceExpiry();
    }
}
