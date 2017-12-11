package com.bt.nextgen.api.saml.service;

import com.bt.nextgen.api.saml.model.SamlTokenRefreshDto;
import com.bt.nextgen.core.api.dto.FindOneDtoService;
import com.bt.nextgen.service.ServiceErrors;

/**
 * Interface for service to force a refresh of a SAML token for a user
 */
public interface SamlTokenRefreshDtoService {
    SamlTokenRefreshDto refreshSamlToken(String websealAppServerId,ServiceErrors serviceErrors);
}
