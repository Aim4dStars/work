package com.bt.nextgen.api.client.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.client.model.ClientIdentificationDto;
import com.bt.nextgen.api.client.model.ClientKey;
import com.bt.nextgen.api.client.model.GcmKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.userinformation.GenericClient;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class ClientKeyDtoServiceImpl implements ClientKeyDtoService {

    private static final Logger LOGGER = getLogger(ClientKeyDtoServiceImpl.class);

    @Autowired
    @Qualifier("avaloqClientIntegrationService")
    private ClientIntegrationService clientIntegrationService;

    @Override
    public ClientIdentificationDto find(ClientKey key, ServiceErrors errors) {
        if (StringUtils.isNotBlank(key.getClientId())) {
            GenericClient client = clientIntegrationService.loadClientDetailsByGcmId(new GcmKey(key.getClientId()), errors);
            if (client != null && client.getClientKey() != null) {
                LOGGER.info("Found existing client with PAN number: {}", key.getClientId());
                ClientIdentificationDto clientIdentificationDto = new ClientIdentificationDto();
                clientIdentificationDto.setKey(new ClientKey(EncodedString.fromPlainText(client.getClientKey().getId()).toString()));
                return clientIdentificationDto;
            }
        }
        return null;
    }
}
