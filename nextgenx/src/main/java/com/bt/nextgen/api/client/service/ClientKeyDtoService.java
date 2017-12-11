package com.bt.nextgen.api.client.service;

import com.bt.nextgen.api.client.model.ClientIdentificationDto;
import com.bt.nextgen.api.client.model.ClientKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

/**
 * DTO service interface for retrieving the client key for an investor via their GCM id
 */
public interface ClientKeyDtoService extends FindByKeyDtoService<ClientKey, ClientIdentificationDto> {


}
