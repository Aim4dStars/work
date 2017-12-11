package com.bt.nextgen.api.client.service;

import com.bt.nextgen.api.client.model.ClientDto;
import com.bt.nextgen.api.client.model.ClientKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.service.ServiceErrors;

/**
 * DTO service interface for retrieving full demographic data for a single customer from UCM.
 */
public interface GlobalCustomerDtoService extends FindByKeyDtoService<ClientKey, ClientDto> {

    /**
     * Fetch full details of a single group-wide client, by CIS key.
     * @param cisKey CIS key of the client.
     * @param errors service errors.
     * @return the relevant client, or <b>null</b> if no such client exists.
     */
    @Override
    ClientDto find(ClientKey cisKey, ServiceErrors errors);

}
