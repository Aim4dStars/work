package com.bt.nextgen.api.saml.service;

import com.btfin.panorama.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.saml.model.SamlTokenRefreshDto;
import com.btfin.panorama.core.security.profile.Profile;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.CustomerCredentialManagementIntegrationService;
import com.bt.nextgen.service.group.customer.CustomerCredentialManagementInformation;
import com.bt.nextgen.web.controller.cash.util.Attribute;

/**
 * Force a refresh of the logged in users saml token.
 * <p/>
 * Created by F030695 on 18/01/2016.
 */
@Service
public class SamlTokenRefreshDtoServiceImpl implements SamlTokenRefreshDtoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SamlTokenRefreshDtoServiceImpl.class);

    @Autowired
    private CustomerCredentialManagementIntegrationService credentialIntegrationService;

    @Override
    public SamlTokenRefreshDto refreshSamlToken(String websealAppServerId, ServiceErrors serviceErrors) {
        if (StringUtil.isNotNullorEmpty(websealAppServerId)) {
            LOGGER.info("Webseal application server identifier {}", websealAppServerId);
            CustomerCredentialManagementInformation response = credentialIntegrationService.refreshCredential(websealAppServerId, serviceErrors);

            if (isRefreshSuccessful(response)) {
                refreshSamlToken();
                LOGGER.info("SAML token has been successfully refreshed.");
                return new SamlTokenRefreshDto(true);
            }
            return new SamlTokenRefreshDto(false);
        }
        LOGGER.error("Webseal application server identifier missing. Cannot Refresh Credential");
        return new SamlTokenRefreshDto(false);
    }

    private boolean isRefreshSuccessful(CustomerCredentialManagementInformation response) {
        return response != null && Attribute.SUCCESS_MESSAGE.equalsIgnoreCase(response.getServiceLevel());
    }

    /**
     * Manually force SAML token to expire, then rebuilt profile using new SAML token taken from request header
     */
    private void refreshSamlToken() {
        Profile currentProfile = (Profile) SecurityContextHolder.getContext().getAuthentication().getDetails();
        currentProfile.forceExpiry();
    }
}
